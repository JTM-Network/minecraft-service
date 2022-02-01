package com.jtm.plugin.core.domain.entity

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.usecase.currency.PriceConverter
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("plugins")
data class Plugin(@Id val id: UUID = UUID.randomUUID(), var name: String = "", var basic_description: String = "",
                  var description: String = "", var version: String? = null, var active: Boolean = false,
                  var premium: Boolean = false, var price: Double = 0.0, val createdTime: Long = System.currentTimeMillis(),
                  var lastUpdated: Long = System.currentTimeMillis()) {

    constructor(dto: PluginDto): this(name = dto.name!!, basic_description = dto.basic_description!!, description = dto.description!!)

    fun updateName(name: String): Plugin {
        this.name = name
        this.lastUpdated = System.currentTimeMillis()
        return this
    }

    fun updateBasicDesc(basic_description: String): Plugin {
        this.basic_description = basic_description
        this.lastUpdated = System.currentTimeMillis()
        return this
    }

    fun updateDesc(description: String): Plugin {
        this.description = description
        this.lastUpdated = System.currentTimeMillis()
        return this
    }

    fun updateVersion(version: String): Plugin {
        this.version = version
        this.lastUpdated = System.currentTimeMillis()
        return this
    }

    fun updateActive(active: Boolean): Plugin {
        this.active = active
        this.lastUpdated = System.currentTimeMillis()
        return this
    }

    fun updatePrice(price: Double): Plugin {
        println("Price: ${this.price > 0.0}")
        this.premium = this.price > 0.0
        this.price = if (price <= 0.0) 0.0 else price
        this.lastUpdated = System.currentTimeMillis()
        return this
    }

    fun convertPrice(converter: PriceConverter, currency: String): Plugin {
        this.price = converter.convert(this.price, currency)
        return this
    }
}
