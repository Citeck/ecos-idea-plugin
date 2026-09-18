package ru.citeck.idea.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import ru.citeck.idea.artifacts.ArtifactTypes

/**
 * Column names are computed from the PSI alone, so these cases run on plain in-memory files
 * without the artifact module layout.
 */
class ModelAttColumnNamesTest : BasePlatformTestCase() {

    fun testAspectYamlWithBlankPrefixFallsBackToId() {
        val file = myFixture.configureByText(
            "aspect.yml",
            """
            ---
            id: my-aspect
            prefix: ''
            attributes:
              - id: first
                type: TEXT
              - id: "quoted"
              - id: _meta
              - id: bad.id
              - id: ''
            systemAttributes:
              - id: sys
            """.trimIndent()
        )
        val columns = ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_ASPECT)
        // "_meta" is a column under the aspect prefix: ecos-data checks the prefixed id, "bad.id" never matches
        assertEquals(
            listOf("my-aspect:first", "my-aspect:quoted", "my-aspect:_meta", "my-aspect:sys"),
            columns.map { it.name }
        )
        assertTrue(columns.all { it.aspect })
        assertEquals("first", columns[0].idElement.text)
    }

    fun testAspectJsonWithBlankPrefixFallsBackToId() {
        val file = myFixture.configureByText(
            "aspect.json",
            """
            {
              "id": "my-aspect",
              "prefix": "",
              "attributes": [{ "id": "first" }, { "id": "_meta" }],
              "systemAttributes": [{ "id": "sys" }]
            }
            """.trimIndent()
        )
        val columns = ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_ASPECT)
        assertEquals(listOf("my-aspect:first", "my-aspect:_meta", "my-aspect:sys"), columns.map { it.name })
        assertEquals("\"first\"", columns[0].idElement.text)
    }

    fun testAspectPrefixIsUsedWhenSet() {
        val yaml = myFixture.configureByText(
            "aspect.yml",
            """
            id: my-aspect
            prefix: 'doc'
            attributes:
              - id: x
            """.trimIndent()
        )
        assertEquals(listOf("doc:x"), ModelAttColumnNames.collect(yaml, ArtifactTypes.TYPE_ASPECT).map { it.name })

        val json = myFixture.configureByText(
            "aspect.json",
            """{ "id": "my-aspect", "prefix": "doc", "attributes": [{ "id": "x" }] }"""
        )
        assertEquals(listOf("doc:x"), ModelAttColumnNames.collect(json, ArtifactTypes.TYPE_ASPECT).map { it.name })
    }

    fun testAspectWithoutPrefixKeyUsesId() {
        val file = myFixture.configureByText(
            "aspect.yml",
            """
            id: my-aspect
            attributes:
              - id: x
            """.trimIndent()
        )
        assertEquals(listOf("my-aspect:x"), ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_ASPECT).map { it.name })
    }

    fun testTypeYamlReadsModelAttributesAndSystemAttributes() {
        val file = myFixture.configureByText(
            "type.yml",
            """
            id: my-type
            model:
              attributes:
                - id: first
                - id: _internal
                - id: "quoted"
              systemAttributes:
                - id: sys
            """.trimIndent()
        )
        val columns = ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_TYPE)
        assertEquals(listOf("first", "quoted", "sys"), columns.map { it.name })
        assertTrue(columns.none { it.aspect })
    }

    fun testTypeJsonReadsModelAttributesAndSystemAttributes() {
        val file = myFixture.configureByText(
            "type.json",
            """
            {
              "id": "my-type",
              "model": {
                "attributes": [{ "id": "first" }, { "id": "bad.id" }],
                "systemAttributes": [{ "id": "sys" }]
              }
            }
            """.trimIndent()
        )
        assertEquals(listOf("first", "sys"), ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_TYPE).map { it.name })
    }

    fun testComputedAttributeWithoutStoringTypeIsNotAColumn() {
        val file = myFixture.configureByText(
            "type.yml",
            """
            id: my-type
            model:
              attributes:
                - id: computedOnRead
                  computed:
                    type: SCRIPT
                    config:
                      fn: return 1
                - id: computedWithExplicitNoneStoring
                  computed:
                    type: ATTRIBUTE
                    storingType: NONE
                - id: stored
            """.trimIndent()
        )
        assertEquals(listOf("stored"), ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_TYPE).map { it.name })
    }

    fun testComputedAttributeWithStoringTypeIsAColumn() {
        val file = myFixture.configureByText(
            "type.yml",
            """
            id: my-type
            model:
              attributes:
                - id: onCreate
                  computed:
                    type: COUNTER
                    storingType: ON_CREATE
                - id: notComputed
                  computed:
                    type: NONE
                    storingType: NONE
            """.trimIndent()
        )
        assertEquals(
            listOf("onCreate", "notComputed"),
            ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_TYPE).map { it.name }
        )
    }

    fun testComputedAttributeOfAspectJsonIsNotAColumn() {
        val file = myFixture.configureByText(
            "aspect.json",
            """
            {
              "id": "my-aspect",
              "prefix": "doc",
              "attributes": [
                { "id": "computedOnRead", "computed": { "type": "SCRIPT" } },
                { "id": "onMutate", "computed": { "type": "SCRIPT", "storingType": "ON_MUTATE" } }
              ]
            }
            """.trimIndent()
        )
        assertEquals(listOf("doc:onMutate"), ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_ASPECT).map { it.name })
    }

    fun testNonAsciiIdsAreNotColumns() {
        val file = myFixture.configureByText(
            "type.yml",
            """
            id: my-type
            model:
              attributes:
                - id: внутреннийАтрибутСОченьДлиннымИдентификатором
                - id: ok
            """.trimIndent()
        )
        assertEquals(listOf("ok"), ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_TYPE).map { it.name })
    }

    fun testOtherArtifactTypesGiveNothing() {
        val file = myFixture.configureByText("form.json", """{ "id": "f", "attributes": [{ "id": "x" }] }""")
        assertTrue(ModelAttColumnNames.collect(file, ArtifactTypes.TYPE_FORM).isEmpty())
    }
}
