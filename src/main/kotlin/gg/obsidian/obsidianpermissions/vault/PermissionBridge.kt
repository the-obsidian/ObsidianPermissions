package gg.obsidian.obsidianpermissions.vault

import gg.obsidian.obsidianpermissions.Plugin
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class PermissionBridge(val opPlugin: Plugin) : PermissionCompatibility() {

    override fun getName(): String {
        return opPlugin.name
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun hasSuperPermsCompat(): Boolean {
        return true
    }

    override fun hasGroupSupport(): Boolean {
        return true
    }

    override fun getGroups(): Array<out String>? {
        return this.opPlugin.configuration.getGroupNames().toTypedArray()
    }

    override fun groupHas(world: String, name: String, permission: String): Boolean {
        val group = opPlugin.manager.getGroup(name) ?: return false
        return group.permissions.contains(permission)
    }

    override fun groupAdd(world: String, name: String, permission: String): Boolean {
        return false
    }

    override fun groupRemove(world: String, name: String, permission: String): Boolean {
        return false
    }

    override fun playerHas(world: String, player: OfflinePlayer, permission: String): Boolean {
        return false
    }

    override fun playerAdd(world: String, player: OfflinePlayer, permission: String): Boolean {
        return false
    }

    override fun playerAddTransient(player: OfflinePlayer, permission: String): Boolean {
        return playerAddTransient(null, player, permission)
    }

    override fun playerAddTransient(player: Player, permission: String): Boolean {
        return playerAddTransient(null, player, permission)
    }

    override fun playerAddTransient(world: String?, player: OfflinePlayer, permission: String): Boolean {
        return false
    }

    override fun playerRemoveTransient(world: String?, player: OfflinePlayer, permission: String): Boolean {
        return false
    }

    override fun playerInGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return opPlugin.manager.getPlayerGroups(player as Player).contains(group)
    }

    override fun playerAddGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return opPlugin.manager.addToGroup(player as Player, group)
    }

    override fun playerRemoveGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return opPlugin.manager.removeFromGroup(player as Player, group)
    }

    override fun getPlayerGroups(world: String, player: OfflinePlayer): Array<out String>? {
        return opPlugin.manager.getPlayerGroupNames(player as Player).toTypedArray()
    }

    override fun getPrimaryGroup(world: String, player: OfflinePlayer): String {
        return opPlugin.manager.getPlayerDefaultGroup(player as Player) ?: ""
    }
}
