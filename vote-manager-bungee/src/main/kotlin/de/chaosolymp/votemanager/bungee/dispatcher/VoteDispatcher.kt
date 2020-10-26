package de.chaosolymp.votemanager.bungee.dispatcher

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.core.UUIDUtils
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class VoteDispatcher(private val plugin: BungeePlugin) {

    fun commitAchievements(player: ProxiedPlayer, achievement: Pair<Int, Int>) {
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()

        out.writeUTF("vote:commit")
        out.write(UUIDUtils.getBytesFromUUID(player.uniqueId))
        out.writeInt(achievement.first)
        out.writeInt(achievement.second)
        out.writeDouble(50.0)

        this.plugin.proxy.servers["Survival"]?.sendData("BungeeCord", out.toByteArray())
    }

}