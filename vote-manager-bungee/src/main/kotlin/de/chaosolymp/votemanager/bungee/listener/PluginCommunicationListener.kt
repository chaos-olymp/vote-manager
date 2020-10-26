package de.chaosolymp.votemanager.bungee.listener

import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.bungee.BungeePlugin
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class PluginCommunicationListener(val plugin: BungeePlugin) : Listener {
    @EventHandler
    fun handlePluginMessage(event: PluginMessageEvent) {
        if(event.tag == "BungeeCord" || event.tag == "bungeecord:main") {
            val input = ByteStreams.newDataInput(event.data)
            val subChannel = input.readUTF()
            if(subChannel == "vote:commit_success") {
                val id = input.readInt()
                if(id != -1) {
                    this.plugin.databaseManager.setAchievementCommitted(id, true)
                } else {
                    this.plugin.logger.warning("Received -1 response by ${event.sender.socketAddress}")
                }
            }
        }
    }
}