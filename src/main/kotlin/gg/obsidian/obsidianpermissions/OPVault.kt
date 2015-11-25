package gg.obsidian.obsidianpermissions

import net.milkbowl.vault.permission.Permission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class OPVault(val opPlugin: Plugin) : Permission() {

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
        return false
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

    override fun playerRemove(world: String, player: OfflinePlayer, permission: String): Boolean {
        return false
    }

    override fun playerInGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return false
    }

    override fun playerAddGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return false
    }

    override fun playerRemoveGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return false
    }

    override fun getPlayerGroups(world: String, player: OfflinePlayer): Array<out String>? {
        return ArrayList<String>().toTypedArray()
    }

    override fun getPrimaryGroup(world: String, player: OfflinePlayer): String {
        return ""
    }

    // Deprecated

    @SuppressWarnings("deprecation")
    private fun pFromName(name: String): OfflinePlayer {
        return plugin.server.getOfflinePlayer(name)
    }

    override fun playerHas(world: String, name: String, permission: String): Boolean {
        return playerHas(world, pFromName(name), permission)
    }

    override fun playerAdd(world: String, name: String, permission: String): Boolean {
        return playerAdd(world, pFromName(name), permission)
    }

    override fun playerRemove(world: String, name: String, permission: String): Boolean {
        return playerRemove(world, pFromName(name), permission)
    }

    override fun playerInGroup(world: String, name: String, group: String): Boolean {
        return playerInGroup(world, pFromName(name), group)
    }

    override fun playerAddGroup(world: String, name: String, group: String): Boolean {
        return playerAddGroup(world, pFromName(name), group)
    }

    override fun playerRemoveGroup(world: String, name: String, group: String): Boolean {
        return playerRemoveGroup(world, pFromName(name), group)
    }

    override fun getPlayerGroups(world: String, name: String): Array<out String>? {
        return getPlayerGroups(world, pFromName(name))
    }

    override fun getPrimaryGroup(world: String, name: String): String {
        return getPrimaryGroup(world, pFromName(name))
    }

    override fun playerAddTransient(world: String, name: String, permission: String): Boolean {
        return playerAddTransient(world, pFromName(name), permission)
    }

    override fun playerRemoveTransient(world: String, name: String, permission: String): Boolean {
        return playerRemoveTransient(world, pFromName(name), permission)
    }
}
