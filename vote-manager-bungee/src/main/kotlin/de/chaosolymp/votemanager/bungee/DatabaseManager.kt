package de.chaosolymp.votemanager.bungee

import de.chaosolymp.votemanager.bungee.model.TopVoter
import de.chaosolymp.votemanager.core.UUIDUtils
import java.sql.Timestamp
import java.time.Instant
import java.util.*

class DatabaseManager(plugin: BungeePlugin) {

    private val dataSource = plugin.databaseConfig.dataSource;

    fun createTable() {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("CREATE TABLE IF NOT EXISTS `votes` (`votestamp` TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,`uuid` BINARY(16) NOT NULL,`username` VARCHAR(16) NOT NULL,`vote_page` VARCHAR(32) NOT NULL);")
            statement.execute()
        }
    }

    fun addVote(uuid: UUID, name: String, votePage: String) {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("INSERT INTO `votes` (`uuid`, `votestamp`, `username`, `vote_page`) VALUES (?, ?, ?, ?)")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.setTimestamp(2, Timestamp.from(Instant.now()))
            statement.setString(3, name)
            statement.setString(4, votePage)
            statement.execute()
        }
    }

    fun countVotes(uuid: UUID): Int {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("SELECT COUNT(*) from `votes` where uuid = ? AND votestamp > now() - interval 30 day")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            val rs = statement.executeQuery()
            return if (rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        }
    }

    fun getVoteRank(uuid: UUID): Int {
        this.dataSource.connection.use {
            val statement = it.prepareStatement("SELECT RANK() OVER (ORDER BY COUNT(*)) ranking FROM `votes` WHERE uuid = ? AND votestamp > now() - interval 30 day")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            val rs = statement.executeQuery()
            return if (rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        }
    }

    fun getTopVoters(count: Int): List<TopVoter> {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("SELECT uuid, username, COUNT(votestamp) as vote_count from `votes` where votestamp > now() - interval 30 day GROUP BY uuid, username ORDER BY vote_count LIMIT ?")
            statement.setInt(1, count)
            val rs = statement.executeQuery()
            val list = mutableListOf<TopVoter>()
            while (rs.next()) {
                val uuid = UUIDUtils.getUUIDFromBytes(rs.getBytes("uuid"))
                val username = rs.getString("username")
                val voteCount = rs.getInt("vote_count")
                list.add(TopVoter(uuid, username, voteCount))
            }
            return list
        }
    }
}