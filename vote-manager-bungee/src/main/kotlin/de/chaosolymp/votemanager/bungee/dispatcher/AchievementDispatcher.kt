package de.chaosolymp.votemanager.bungee.dispatcher

import de.chaosolymp.votemanager.bungee.BungeePlugin
import java.util.*

class AchievementDispatcher(plugin: BungeePlugin) {

    private val dataSource = plugin.achievementDatabaseConfig.dataSource

    fun insertOrUpdateVotes(target: UUID) {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("INSERT INTO aac_custom (`playername`, `customname`, `custom`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `custom` = `custom` + 1;")
            statement.setString(1, target.toString())
            statement.setString(2, "votes")
            statement.setInt(3, 1)
            statement.execute()
        }
    }

}