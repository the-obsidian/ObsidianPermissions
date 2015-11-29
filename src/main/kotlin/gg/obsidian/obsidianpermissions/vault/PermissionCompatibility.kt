package gg.obsidian.obsidianpermissions.vault

import net.milkbowl.vault.permission.Permission
import org.bukkit.OfflinePlayer

abstract class PermissionCompatibility : Permission() {
    @Suppress("DEPRECATION")
    private fun pFromName(name: String): OfflinePlayer? {
        return plugin.server.getOfflinePlayer(name)
    }

    override fun playerHas(world: String, name: String, permission: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerHas(world, player, permission)
    }

    override fun playerAdd(world: String, name: String, permission: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerAdd(world, player, permission)
    }

    override fun playerRemove(world: String, name: String, permission: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerRemove(world, player, permission)
    }

    override fun playerInGroup(world: String, name: String, group: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerInGroup(world, player, group)
    }

    override fun playerAddGroup(world: String, name: String, group: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerAddGroup(world, player, group)
    }

    override fun playerRemoveGroup(world: String, name: String, group: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerRemoveGroup(world, player, group)
    }

    override fun getPlayerGroups(world: String, name: String): Array<out String>? {
        val player = pFromName(name) ?: return null
        return getPlayerGroups(world, player)
    }

    override fun getPrimaryGroup(world: String, name: String): String {
        val player = pFromName(name) ?: return ""
        return getPrimaryGroup(world, player)
    }

    override fun playerAddTransient(world: String, name: String, permission: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerAddTransient(world, player, permission)
    }

    override fun playerRemoveTransient(world: String, name: String, permission: String): Boolean {
        val player = pFromName(name) ?: return false
        return playerRemoveTransient(world, player, permission)
    }
}
