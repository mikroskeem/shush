package eu.mikroskeem.shush

import com.comphenix.protocol.ProtocolLibrary
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Mark Vainomaa
 */
class Shush : JavaPlugin() {
    val messageBlacklist : MutableMap<String, List<String>> = HashMap()

    var debug = false
    var listener : ChatPacketListener? = null
        private set

    override fun onEnable() {
        logger.info("Enabling plugin...")
        if(!server.pluginManager.isPluginEnabled("ProtocolLib")) {
            logger.severe("ProtocolLib is not enabled!")
            isEnabled = false
            return
        }

        /* Process configuration */
        debug = config.getBoolean("debug", false)

        val worlds = config.getConfigurationSection("worlds")
        worlds.getKeys(false).forEach { worldKey ->
            messageBlacklist.put(worldKey, worlds.getStringList(worldKey))
        }

        /* Enable listener */
        listener = ChatPacketListener(this)
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(listener)
        logger.info("Ready!")
    }

    override fun onDisable() {
        logger.info("Stopping packet listener...")
        ProtocolLibrary.getProtocolManager().
                removePacketListener(listener)
        logger.info("Done!")
    }
}