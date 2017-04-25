package eu.mikroskeem.shush

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.*
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

/**
 * @author Mark Vainomaa
 */
class ChatPacketListener(private val shush: Shush) : PacketAdapter(shush, PacketType.Play.Server.CHAT) {
    override fun onPacketSending(e: PacketEvent) {
        val player = e.player
        val world = player.location.world.name
        val component = e.packet.chatComponents.read(0)

        /* Do checks */
        var worldFound = false
        shush.messageBlacklist.keys.forEach { worldName ->
            if(!e.isCancelled && !worldFound && worldName.contains(world)) {
                worldFound = true
                shush.messageBlacklist[world]?.forEach { entry ->
                    val obj = JSONParser().parse(component?.json!!) as JSONObject
                    val text = obj["text"] as String
                    if(text.contains(entry)) {
                        if(shush.debug) shush.logger.info("'%s' matched '%s'".format(component.json, entry))
                        e.isCancelled = true
                    }
                }
            }
        }
    }
}