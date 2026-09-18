package ru.citeck.idea.inspections

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import ru.citeck.idea.artifacts.ArtifactKind
import ru.citeck.idea.artifacts.ArtifactTypeMeta
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.artifacts.ArtifactsMetaLoader
import ru.citeck.idea.artifacts.ArtifactsService

/**
 * End-to-end: the inspection recognises a file as a Citeck artifact only inside an ECOS module,
 * so every case builds the module layout ArtifactsService expects: a pom.xml with the ECOS parent
 * groupId in the module root and the artifact under the artifacts root.
 */
class ModelAttColumnNameLengthInspectionTest : BasePlatformTestCase() {

    companion object {

        private val log = Logger.getInstance(ModelAttColumnNameLengthInspectionTest::class.java)

        // Own descriptor: the light project is shared between test classes otherwise,
        // and CiteckProject caches module info per module.
        private val DESCRIPTOR = LightProjectDescriptor()

        private const val POM = "<project><parent><groupId>ru.citeck.ecos.eapps.project</groupId></parent></project>"
        private const val ASPECTS_ROOT = "src/main/resources/app/artifacts/model/aspect/"
        private const val TYPES_ROOT = "src/main/resources/app/artifacts/model/type/"

        private const val LIMIT_MSG = "but PostgreSQL truncates identifiers to 63 bytes."
        private const val ASPECT_FIX = "Shorten the attribute id or set a short 'prefix' for the aspect."
        private const val TYPE_FIX = "Shorten the attribute id."
    }

    override fun getProjectDescriptor(): LightProjectDescriptor = DESCRIPTOR

    override fun setUp() {
        super.setUp()
        ensureArtifactTypesRegistered()
        myFixture.addFileToProject("pom.xml", POM)
        myFixture.enableInspections(ModelAttColumnNameLengthInspection::class.java)
    }

    private fun ensureArtifactTypesRegistered() {
        try {
            ApplicationManager.getApplication().getService(ArtifactsMetaLoader::class.java)
        } catch (e: Throwable) {
            log.warn("ArtifactsMetaLoader is not available in tests, artifact types are registered manually", e)
        }
        val service = ArtifactsService.getInstance()
        for ((typeId, sourceId) in listOf(ArtifactTypes.TYPE_ASPECT to "emodel/aspect", ArtifactTypes.TYPE_TYPE to "emodel/type")) {
            if (!service.getArtifactTypes().contains(typeId)) {
                service.register(
                    ArtifactTypeMeta.create().withTypeId(typeId).withSourceId(sourceId).withKind(ArtifactKind.YAML).build(),
                    emptyList()
                )
            }
        }
    }

    private fun checkArtifact(path: String, text: String) {
        val file = myFixture.addFileToProject(path, text)
        myFixture.configureFromExistingVirtualFile(file.virtualFile)
        myFixture.checkHighlighting()
    }

    private fun aspectError(name: String, bytes: Int): String {
        return "Column name '$name' is $bytes bytes long, $LIMIT_MSG $ASPECT_FIX"
    }

    private fun typeError(name: String, bytes: Int): String {
        return "Column name '$name' is $bytes bytes long, $LIMIT_MSG $TYPE_FIX"
    }

    fun testAspectWithBlankPrefixUsesItsIdAndExceedsTheLimit() {
        val name = "has-income-power-of-attorney-aspect:incomePowerOfAttorneyValidationStatusAssoc"
        checkArtifact(
            ASPECTS_ROOT + "has-income-power-of-attorney-aspect.yml",
            """
            ---
            id: has-income-power-of-attorney-aspect
            name:
              ru: Наличие входящей доверенности
              en: Has income power of attorney
            prefix: ''
            attributes:
              - id: incomePowerOfAttorneyAssoc
                type: ASSOC
              - id: <error descr="${aspectError(name, 78)}">incomePowerOfAttorneyValidationStatusAssoc</error>
                type: ASSOC
            systemAttributes: []
            """.trimIndent()
        )
    }

    fun testAspectBoundaryInAttributesAndSystemAttributes() {
        val tooLongAtt = "test-aspect:incomePowerOfAttorneyValidationStatusAssocWithExtraC"
        val tooLongSysAtt = "test-aspect:systemAttributeWithVeryLongIdentifierForBoundaryChkX"
        checkArtifact(
            ASPECTS_ROOT + "test-aspect.yml",
            """
            id: test-aspect
            prefix: ''
            attributes:
              - id: incomePowerOfAttorneyValidationStatusAssocWithExtra
              - id: <error descr="${aspectError(tooLongAtt, 64)}">incomePowerOfAttorneyValidationStatusAssocWithExtraC</error>
            systemAttributes:
              - id: systemAttributeWithVeryLongIdentifierForBoundaryChk
              - id: <error descr="${aspectError(tooLongSysAtt, 64)}">systemAttributeWithVeryLongIdentifierForBoundaryChkX</error>
            """.trimIndent()
        )
    }

    fun testShortPrefixKeepsLongAttributeWithinTheLimit() {
        val tooLong = "edi:incomePowerOfAttorneyValidationStatusAssocWithExtraChars1234"
        checkArtifact(
            ASPECTS_ROOT + "prefixed-aspect.yml",
            """
            id: has-income-power-of-attorney-aspect
            prefix: edi
            attributes:
              - id: incomePowerOfAttorneyValidationStatusAssocWithExtraChars123
              - id: <error descr="${aspectError(tooLong, 64)}">incomePowerOfAttorneyValidationStatusAssocWithExtraChars1234</error>
            """.trimIndent()
        )
    }

    fun testTypeJsonAttributesAreCheckedWithoutPrefix() {
        val tooLongAtt = "incomePowerOfAttorneyValidationStatusAssocWithExtraChars12345678"
        val tooLongSysAtt = "systemAttributeOfTypeWithVeryLongIdentifierForBoundaryCheck12345"
        checkArtifact(
            TYPES_ROOT + "test-type.json",
            """
            {
              "id": "test-type",
              "storageType": "ECOS_MODEL",
              "model": {
                "attributes": [
                  { "id": "incomePowerOfAttorneyValidationStatusAssocWithExtraChars1234567" },
                  { "id": <error descr="${typeError(tooLongAtt, 64)}">"$tooLongAtt"</error> }
                ],
                "systemAttributes": [
                  { "id": <error descr="${typeError(tooLongSysAtt, 64)}">"$tooLongSysAtt"</error> }
                ]
              }
            }
            """.trimIndent()
        )
    }

    fun testIdsThatNeverBecomeColumnsAreIgnored() {
        checkArtifact(
            TYPES_ROOT + "ignored-ids-type.yml",
            """
            id: ignored-ids-type
            model:
              attributes:
                - id: _internalAttributeWithVeryLongIdentifierThatIsNeverStoredAsColumn
                - id: внутреннийАтрибутСОченьДлиннымИдентификатором
            """.trimIndent()
        )
    }

    fun testUnderscoreAttributeOfAspectIsAColumnUnderItsPrefix() {
        // ecos-data checks the prefixed id: "us:_x..." doesn't start with '_', so it is a column
        val attId = "_" + "x".repeat(60)
        checkArtifact(
            ASPECTS_ROOT + "underscore-aspect.yml",
            """
            id: underscore-aspect
            prefix: us
            attributes:
              - id: <error descr="${aspectError("us:$attId", 64)}">$attId</error>
            """.trimIndent()
        )
    }

    fun testFileOutsideArtifactsRootIsNotInspected() {
        myFixture.configureByText(
            "aspect.yml",
            """
            id: has-income-power-of-attorney-aspect
            prefix: ''
            attributes:
              - id: incomePowerOfAttorneyValidationStatusAssoc
            """.trimIndent()
        )
        myFixture.checkHighlighting()
    }
}
