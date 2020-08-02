package de.chaosolymp.votemanager.bungee.dispatcher

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.core.UUIDUtils
import java.util.*

class AchievementDispatcher(private val plugin: BungeePlugin) {

    private val dataSource = plugin.achievementDatabaseConfig.dataSource

    fun insertOrUpdateVotes(target: UUID) {
        if(this.plugin.proxy.players.stream().noneMatch { it.uniqueId == target }) {
            this.dataSource.connection.use {
                val statement =
                    it.prepareStatement("INSERT INTO aac_custom (`playername`, `customname`, `custom`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `custom` = `custom` + 1;")
                statement.setString(1, target.toString())
                statement.setString(2, "votes")
                statement.setInt(3, 1)
                statement.execute()
            }
        } else {
            val out: ByteArrayDataOutput = ByteStreams.newDataOutput(33)

            out.writeUTF("vote:achievements")
            out.write(UUIDUtils.getBytesFromUUID(target))

            this.plugin.proxy.servers["Survival"]?.sendData("BungeeCord", out.toByteArray())
        }
    }

}