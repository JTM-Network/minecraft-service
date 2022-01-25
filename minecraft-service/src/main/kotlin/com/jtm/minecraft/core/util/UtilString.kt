package com.jtm.minecraft.core.util

import java.util.*

class UtilString {
    companion object {
        fun pluginsToString(plugins: Array<UUID>): String {
            val builder = StringBuilder()
            plugins.forEach {
                builder.append(it.toString())
                if (plugins.last() != it) builder.append(",")
            }
            return builder.toString()
        }

        fun stringToPlugins(plugins: String): Array<UUID> {
            val split = plugins.split(",")
            val list: MutableList<UUID> = mutableListOf()
            for (item in split) list.add(UUID.fromString(item))
            return list.toTypedArray()
        }
    }
}