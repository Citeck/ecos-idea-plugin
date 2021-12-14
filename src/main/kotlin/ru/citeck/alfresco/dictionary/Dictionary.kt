package ru.citeck.alfresco.dictionary

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.containers.toArray
import ru.citeck.EcosServer


class Dictionary {


    companion object {

        private val dictionaries = HashMap<EcosServer, Dictionary>()

        fun get(ecosServer: EcosServer): Dictionary {
            if (dictionaries.containsKey(ecosServer)) return dictionaries[ecosServer]!!
            val objectMapper = ObjectMapper()
            val json = ecosServer.execute(
                "alfresco/service/api/dictionary",
                clazz = String::class.java,
                method = "GET",
                basicAuth = true
            )

            val dictionary = Dictionary()

            val injectableValues = InjectableValues.Std()
            injectableValues.addValue("dictionary", dictionary)
            injectableValues.addValue("ecosServer", ecosServer)

            val types = objectMapper.reader(injectableValues).forType(Type::class.java).readValues<Type>(json)

            types.forEach { dictionary.add(it) }

            dictionaries[ecosServer] = dictionary
            return dictionary
        }

    }


    private fun add(type: Type) {
        if (type.isAspect) {
            (this.aspects as HashMap)[type.name] = type
        } else {
            (this.types as HashMap)[type.name] = type
        }
    }


    val types: Map<String, Type> = HashMap()
    val aspects: Map<String, Type> = HashMap()

    val ecosTypes: Map<String, Type> by lazy {
        val map = HashMap<String, Type>()
        types.values.filter { it.ecosType != null }.forEach {
            map[it.ecosType!!] = it
        }
        return@lazy map
    }

    val ecosTypesLookupArray: Array<LookupElement> by lazy {
        val set: MutableSet<LookupElement> = LinkedHashSet()
        ecosTypes.keys.sortedBy { it }.forEach {
            set.add(LookupElementBuilder.create(it))
        }
        return@lazy set.toArray(LookupElement.EMPTY_ARRAY)
    }

    val typesLookupArray: Array<LookupElement> by lazy {
        val set: MutableSet<LookupElement> = LinkedHashSet()

        types.values.sortedBy { it.name }.forEach {
            set.add(LookupElementBuilder.create(it.name))
        }
        return@lazy set.toArray(LookupElement.EMPTY_ARRAY)
    }

}