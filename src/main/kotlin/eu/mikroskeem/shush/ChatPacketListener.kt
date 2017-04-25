package eu.mikroskeem.shush

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.*
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

/**
 * @author Mark Vainomaa
 */
class ChatPacketListener(plugin: Shush?) : PacketAdapter(plugin, PacketType.Play.Server.CHAT) {
    override fun onPacketSending(e: PacketEvent) {
        val player = e.player
        val world = player.location.world.name
        val component = e.packet.chatComponents.read(0)

        /* Do checks */
        var worldFound = false
        Shush.messageBlacklist.keys.forEach { worldName ->
            if(!e.isCancelled && !worldFound && worldName.contains(world)) {
                worldFound = true
                Shush.messageBlacklist[world]?.forEach { entry ->
                    val obj : JSONObject = JSONParser().parse(component?.json!!) as JSONObject
                    val text = obj["text"] as String
                    if(text.contains(entry)) {
                        if(Shush.debug) plugin.logger.info("'%s' matched '%s'".format(component.json, entry))
                        e.isCancelled = true
                    }
                }
            }
        }
    }
}