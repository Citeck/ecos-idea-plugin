package ru.citeck.idea.records

import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.ecos.webapp.api.entity.EntityRef

class RecordAtts(
    val id: EntityRef = EntityRef.EMPTY,
    val attributes: ObjectData = ObjectData.create()
) {
    fun setAtt(att: String, value: Any?): RecordAtts {
        attributes[att] = value
        return this
    }

    fun getAtt(name: String): DataValue {
        return attributes[name]
    }
}
