package gg.obsidian.obsidianpermissions

import gg.obsidian.obsidianpermissions.models.Group
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import java.util.*

class Manager(val plugin: Plugin) {
    fun setPermissions(player: Player, force: Boolean = false): Boolean {
        val permName = plugin.PERMISSION_PREFIX + player.uniqueId.toString()
        var perm = plugin.server.pluginManager.getPermission(permName)
        var playerState = getPlayerState(player)
        val hasPermissionAttachment = player.isPermissionSet(permName) && player.hasPermission(permName)

        var needsUpdate = force
        if (!needsUpdate) {
            needsUpdate = perm == null || playerState == null || !hasPermissionAttachment
        }
        if (!needsUpdate) return false

        plugin.logDebug("Updating permissions for ${player.name}")

        val playerGroups = getPlayerGroups(player)
        val combinedPermissions = getCombinedPermissionsFor(player)

        // Create combined permission for player
        if (perm == null) {
            perm = Permission(permName, PermissionDefault.FALSE, combinedPermissions)
            plugin.server.pluginManager.addPermission(perm)
        } else {
            perm.children.clear()
            perm.children.putAll(combinedPermissions)
        }

        perm.recalculatePermissibles()

        if (playerState != null) {
            playerState.groups = playerGroups
        } else {
            playerState = PlayerState(groups = playerGroups);
            player.setMetadata(plugin.PLAYER_METADATA_KEY, FixedMetadataValue(plugin, playerState));
        }

        if (!hasPermissionAttachment) {
            player.addAttachment(plugin, perm.name, true);
        }

        return true
    }

    fun removePermissions(player: Player, recalculate: Boolean = false): Boolean {
        plugin.logDebug("Removing permissions for ${player.name}")
        player.removeMetadata(plugin.PLAYER_METADATA_KEY, plugin)
        val permName = plugin.PERMISSION_PREFIX + player.uniqueId.toString()
        plugin.server.pluginManager.removePermission(permName)
        if (recalculate) {
            for (p in plugin.server.pluginManager.getPermissionSubscriptions(permName)) {
                p.recalculatePermissions()
            }
        }
        return true
    }

    fun addToGroup(player: Player, groupName: String): Boolean {
        return plugin.groupMembershipTable.addPlayerToGroup(player, groupName)
    }

    fun removeFromGroup(player: Player, groupName: String): Boolean {
        return plugin.groupMembershipTable.removePlayerFromGroup(player, groupName)
    }

    fun getPlayerGroups(player: Player): Set<String> {
        return plugin.groupMembershipTable.getPlayerGroups(player).map { it.name }.toSet()
    }

    fun getGroup(name: String): Group? {
        return plugin.configuration.getGroup(name)
    }

    // Private functions

    private fun getPlayerState(player: Player): PlayerState? {
        for (mv in player.getMetadata(plugin.PLAYER_METADATA_KEY)) {
            if (mv.owningPlugin == plugin) {
                return mv.value() as PlayerState
            }
        }
        return null
    }

    private data class PlayerState(var groups: Set<String>) {}

    private fun getCombinedPermissionsFor(player: Player): Map<String, Boolean> {
        val groups = plugin.groupMembershipTable.getPlayerGroups(player)
        val combined: Set<String> = groups.fold(HashSet<String>()) { a: Set<String>, b -> a.intersect(b.permissions) }
        val finalMap = HashMap<String, Boolean>()
        combined.forEach { finalMap.put(it, true) }
        return finalMap
    }
}
