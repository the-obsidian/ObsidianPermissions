package gg.obsidian.obsidianpermissions

import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin(), Listener {

    val PERMISSION_PREFIX = "ObsidianPermissions."
    val PLAYER_METADATA_KEY = "ObsidianPermissions.PlayerState"

    val configuration = Configuration(this)

    override fun onEnable() {
        loadConfig(description.version)

        server.pluginManager.registerEvents(this, this)

        if (server.pluginManager.isPluginEnabled("Vault")) {
            val vault = OPVault(this)
            server.servicesManager.register(Permission::class.java, vault, this, ServicePriority.High)
            logger.info("Hooked into Vault permissions interface")
        }
    }

    // Event Listeners

    @EventHandler
    fun onPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        // preload the cache
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val uuid = event.player.uniqueId.toString()
        injectPermissions(event.player)
    }

    // Utilities

    fun loadConfig(version: String) {
        this.saveDefaultConfig()
        config.options().copyDefaults(true)
        config.set("version", version)
        saveConfig()
        configuration.load()
    }

    fun logDebug(msg: String) {
        if (!configuration.DEBUG) return;
        logger.info(msg)
    }

    // Internal methods

    fun injectPermissions(player: Player): Boolean {
        val permName = PERMISSION_PREFIX + player.uniqueId.toString()
        val perm = server.pluginManager.getPermission(permName)
        val playerState = getPlayerState(player)
        val hasPermissionAttachment = player.isPermissionSet(permName) && player.hasPermission(permName)

        // No need to update
        if (perm != null && playerState != null && hasPermissionAttachment) return false

        logDebug("Updating permissions for ${player.name}")

        val combinedPermissions = getCombinedPermissionsFor(player)

        return false
    }

    private fun getPlayerState(player: Player): PlayerState? {
        for (mv in player.getMetadata(PLAYER_METADATA_KEY)) {
            if (mv.owningPlugin == this) {
                return mv.value() as PlayerState
            }
        }
        return null
    }

    private data class PlayerState(val world: String, val groups: Set<String>) {}

    private fun getCombinedPermissionsFor(player: Player) {}
}
