package gg.obsidian.obsidianpermissions

import gg.obsidian.obsidianpermissions.models.Group
import java.util.*

class Configuration(val plugin: Plugin) {

    var GROUPS = HashSet<Group>()
    var DEBUG = false

    fun load() {
        plugin.reloadConfig()

        DEBUG = plugin.config.getBoolean("debug", false)

        for (rawDefinition in plugin.config.getMapList("groups")) {
            val definition = rawDefinition as Map<String, Any>
            var name: String? = null
            var rank = 10;
            val permissions = HashSet<String>()

            if (definition.contains("name")) {
                name = definition["name"] as String
            }

            if (definition.contains("rank")) {
                rank = definition["rank"] as Int
            }

            if (definition.contains("permissions")) {
                val hashmap = definition["permissions"] as ArrayList<String>
                permissions.addAll(hashmap)
            }

            if (name == null) continue

            val group = Group(name, rank, permissions)
            GROUPS.add(group)
        }
    }

    fun getGroupNames(): List<String> {
        return GROUPS.map { it.name }
    }

    fun getGroup(name: String): Group? {
        val results = GROUPS.filter { it.name.equals(name) }
        if (results.size == 0) return null
        return results[0]
    }
}
