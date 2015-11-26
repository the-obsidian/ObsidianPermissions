package gg.obsidian.obsidianpermissions.models

import gg.obsidian.obsidianpermissions.Plugin
import org.bukkit.entity.Player
import java.util.*

class GroupMembershipTable(val plugin: Plugin) {

    fun getPlayerGroupMemberships(player: Player): Set<GroupMembership> {
        val results = HashSet<GroupMembership>()

        val query = plugin.database.find(GroupMembership::class.java)
                .where()
                .eq("playerUUID", player.uniqueId)
                .query()
        if (query != null) {
            results.addAll(query.findList())
        }

        return results
    }

    fun getPlayerGroups(player: Player): Set<Group> {
        return getPlayerGroupMemberships(player)
                .map { plugin.configuration.getGroup(it.groupName) }
                .filter { it != null }
                .map { it!! }
                .toHashSet()
    }

    fun addPlayerToGroup(player: Player, groupName: String): Boolean {
        if (plugin.groupMembershipTable.playerInGroup(player, groupName)) return false
        val gm = GroupMembership(
                groupName = groupName,
                playerName = player.name,
                playerUUID = player.uniqueId
        )
        plugin.database.save(gm)
        return true
    }

    fun removePlayerFromGroup(player: Player, groupName: String): Boolean {
        getPlayerGroupMemberships(player)
                .filter { it.groupName.equals(groupName) }
                .forEach { plugin.database.delete(it) }
        return true
    }

    fun playerInGroup(player: Player, groupName: String): Boolean {
        val query = plugin.database.find(GroupMembership::class.java)
                .where()
                .eq("playerUUID", player.uniqueId)
                .eq("groupName", groupName)
                .query()
        return query.findRowCount() > 0
    }
}
