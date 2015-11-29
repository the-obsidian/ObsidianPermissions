package gg.obsidian.obsidianpermissions

import gg.obsidian.obsidianpermissions.models.Group
import org.bukkit.ChatColor
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

        val playerGroups = getPlayerGroupNames(player)
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

        setDisplayName(player)

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
        plugin.groupMembershipTable.addPlayerToGroup(player, groupName)
        setPermissions(player, true)
        return true
    }

    fun removeFromGroup(player: Player, groupName: String): Boolean {
        plugin.groupMembershipTable.removePlayerFromGroup(player, groupName)
        return setPermissions(player, true)
    }

    fun getPlayerGroups(player: Player): List<Group> {
        return plugin.groupMembershipTable.getPlayerGroups(player).sortedByDescending { it.rank }
    }

    fun getPlayerGroupNames(player: Player): List<String> {
        return getPlayerGroups(player).map { it.name }
    }

    fun getPlayerDefaultGroup(player: Player): String? {
        val groupNames = getPlayerGroupNames(player)
        if (groupNames.size == 0) return null
        return groupNames.first()
    }

    fun getGroup(name: String): Group? {
        return plugin.configuration.getGroup(name)
    }

    fun setDisplayName(player: Player) {
        var template = getDisplayNameTemplate(player)
        if (template.contains("%p")) {
            template = template.replace("%p", player.name)
        }
        val colorizedName = ChatColor.translateAlternateColorCodes('&', template)
        player.displayName = colorizedName
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

    private data class PlayerState(var groups: List<String>) {}

    private fun getCombinedPermissionsFor(player: Player): Map<String, Boolean> {
        val finalMap = HashMap<String, Boolean>()
        val groups = plugin.groupMembershipTable.getPlayerGroups(player)
        val appliedGroups = HashSet<Group>()

        for (group in groups) {
            val permissions = resolveGroupPermissions(group, appliedGroups) ?: continue
            for (permission in permissions) {
                finalMap.put(permission, true)
            }
            appliedGroups.add(group)
        }
        return finalMap
    }

    private fun resolveGroupPermissions(
            group: Group,
            appliedGroups: MutableSet<Group> = HashSet<Group>()
    ): Set<String>? {
        if (appliedGroups.contains(group)) return null

        if (group.extends.size == 0) return group.permissions

        val permissions = HashSet<String>()
        permissions.addAll(group.permissions)
        appliedGroups.add(group)

        for (groupName in group.extends) {
            val subGroup = getGroup(groupName) ?: continue
            val subPermissions = resolveGroupPermissions(subGroup, appliedGroups) ?: continue
            permissions.addAll(subPermissions)
            appliedGroups.add(group)
        }

        return permissions
    }

    private fun getDisplayNameTemplate(player: Player): String {
        val groups = getPlayerGroups(player)
        for (group in groups) {
            if (group.displayNameTemplate != null) return group.displayNameTemplate
        }
        return "%p"
    }
}
