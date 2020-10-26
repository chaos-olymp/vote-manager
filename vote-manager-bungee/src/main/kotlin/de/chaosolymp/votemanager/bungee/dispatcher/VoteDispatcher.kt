package de.chaosolymp.votemanager.bungee.dispatcher

import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.util.ByteArrayDataOutputUtil
import net.md_5.bungee.api.connection.ProxiedPlayer

class VoteDispatcher(private val plugin: BungeePlugin) {

    fun commitAchievements(player: ProxiedPlayer, achievement: Pair<Int, Int>) {
        val request = ByteArrayDataOutputUtil.createCommitOutput(player.uniqueId, achievement.first, achievement.second)
        this.plugin.proxy.servers["Survival"]?.sendData("BungeeCord", request)
    }

}