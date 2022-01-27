package com.jtm.profile.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("profiles")
data class Profile(@Id val id: String, var subscription: Boolean = false, val plugins: MutableList<UUID> = mutableListOf(), var banned: Boolean = false, val created: Long = System.currentTimeMillis()) {

    fun addSubscription(): Profile {
        this.subscription = true
        return this
    }

    fun removeSubscription(): Profile {
        this.subscription = false
        return this
    }

    fun addPlugin(id: UUID): Profile {
        this.plugins.add(id)
        return this
    }

    fun hasPlugin(id: UUID): Boolean {
        return this.plugins.contains(id)
    }

    fun removePlugin(id: UUID): Profile {
        this.plugins.remove(id)
        return this
    }

    fun ban(): Profile {
        this.banned = true;
        return this
    }

    fun isBanned(): Boolean {
        return this.banned
    }

    fun unban(): Profile {
        this.banned = false;
        return this
    }
}