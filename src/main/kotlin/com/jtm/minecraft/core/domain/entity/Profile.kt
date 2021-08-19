package com.jtm.minecraft.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("profiles")
data class Profile(
    @Id val id: UUID = UUID.randomUUID(),
    val email: String = "",
    val pluginsAuthenticated: MutableList<UUID> = mutableListOf()) {

    fun isAuthenticated(id: UUID): Boolean {
        return pluginsAuthenticated.contains(id)
    }

    fun addAccess(id: UUID): Profile {
        pluginsAuthenticated.add(id)
        return this
    }

    fun removeAccess(id: UUID): Profile {
        pluginsAuthenticated.remove(id)
        return this
    }
}