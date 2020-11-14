package de.chaosolymp.votemanager.bungee.listener

import de.chaosolymp.votemanager.bungee.ACHIEVEMENT_SERVER
import de.chaosolymp.votemanager.bungee.BungeePlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class PlayerListener(private val plugin: BungeePlugin) : Listener {

    @EventHandler
    fun handleJoin(event: PostLoginEvent) {
        this.checkServerAndFetch(event.player)
    }

    @EventHandler
    fun handleSwitch(event: ServerSwitchEvent) {
        this.checkServerAndFetch(event.player)
    }

    private fun checkServerAndFetch(player: ProxiedPlayer) {
        if(player.server.info.name.equals(ACHIEVEMENT_SERVER, true)) {
            val achievementCount = this.plugin.databaseManager.fetchUncommittedAchievementCount(player)
            achievementCount?.let {
                    this.plugin.voteDispatcher.commitAchievements(player, it)
                    this.plugin.logger.info("Achievement commit #${it.first} (incrementation of ${it.second})")
            }

        }
    }
}