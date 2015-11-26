package gg.obsidian.obsidianpermissions

import gg.obsidian.obsidianpermissions.models.Group
import gg.obsidian.obsidianpermissions.models.GroupMembership
import gg.obsidian.obsidianpermissions.models.GroupMembershipTable
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import javax.persistence.PersistenceException

class Plugin : JavaPlugin(), Listener {

    val PERMISSION_PREFIX = "ObsidianPermissions."
    val PLAYER_METADATA_KEY = "ObsidianPermissions.PlayerState"

    val configuration = Configuration(this)
    val manager = Manager(this)
    val groupMembershipTable = GroupMembershipTable(this)

    override fun onEnable() {
        loadConfig(description.version)
        setupDatabase()

        server.pluginManager.registerEvents(this, this)

        if (server.pluginManager.isPluginEnabled("Vault")) {
            val vault = OPVault(this)
            server.servicesManager.register(Permission::class.java, vault, this, ServicePriority.High)
            logger.info("Hooked into Vault permissions interface")
        }
    }

    // Event Listeners

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        // manager.UpdateDisplayName(event.uniqueId, event.name)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        logDebug("${event.player.name} logged in")
        manager.setPermissions(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLoginMonitor(event: PlayerLoginEvent) {
        if (event.result != PlayerLoginEvent.Result.ALLOWED) {
            logDebug("${event.player.name} is not allowed to log in")
            manager.removePermissions(event.player)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        logDebug("${event.player.name} is joining")
        val uuid = event.player.uniqueId.toString()
        manager.setPermissions(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        logDebug("${event.player.name} is quitting")
        manager.removePermissions(event.player)
        // uuidResolver.preload(event.player.name, event.player.uniqueId)
    }

    // Utilities

    fun loadConfig(version: String) {
        this.saveDefaultConfig()
        config.options().copyDefaults(true)
        config.set("version", version)
        saveConfig()
        configuration.load()
    }

    fun setupDatabase() {
        try {
            getDatabase().find(Group::class.java).findRowCount()
        } catch (ex: PersistenceException) {
            logger.info("First run, initializing database.")
            installDDL()
        }
    }

    override fun getDatabaseClasses(): ArrayList<Class<*>> {
        val list = ArrayList<Class<*>>()
        list.add(GroupMembership::class.java)
        return list
    }

    fun logDebug(msg: String) {
        if (!configuration.DEBUG) return;
        logger.info(msg)
    }
}
