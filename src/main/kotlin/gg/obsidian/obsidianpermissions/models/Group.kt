package gg.obsidian.obsidianpermissions.models

data class Group(
        val name: String,
        val rank: Int,
        val permissions: Set<String>,
        val displayNameTemplate: String? = null
) {}
