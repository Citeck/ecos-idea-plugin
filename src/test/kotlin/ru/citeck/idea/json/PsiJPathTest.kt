package ru.citeck.idea.json

import com.intellij.openapi.diagnostic.Logger
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.json.Json
import ru.citeck.ecos.commons.utils.resource.ResourceUtils
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import ru.citeck.ecos.commons.json.YamlUtils

class PsiJPathTest : BasePlatformTestCase() {

    fun test() {

        val testsRoot = ResourceUtils.getFile("classpath:ru/citeck/idea/json/PsiJPathTest")

        for (testFile in testsRoot.listFiles() ?: emptyArray()) {

            println("Testing ${testFile.absolutePath}")

            val data = Json.mapper.readNotNull(testFile, TestData::class.java)

            val jsonFile = myFixture.configureByText("test.json", data.source.toString())
            val yamlFile = myFixture.configureByText("test.yaml", YamlUtils.toString(data.source))

            for (test in data.tests) {

                val jpath = PsiJPath.parse(test.jpath)
                println("Testing ${test.jpath}")

                val jsonRes = jpath.getStrListWithElements(jsonFile).map { DataValue.createStr(it.first) }
                TestCase.assertEquals(test.expected, jsonRes)

                val yamlRes = jpath.getStrListWithElements(yamlFile).map { DataValue.createStr(it.first) }
                TestCase.assertEquals(test.expected, yamlRes)
            }
        }
    }

    data class TestData(
        val source: DataValue,
        val tests: List<TestElement>
    )

    data class TestElement(
        val jpath: String,
        val expected: List<DataValue>
    )
}
