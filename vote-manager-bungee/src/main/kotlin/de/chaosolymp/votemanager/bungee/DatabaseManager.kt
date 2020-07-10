package de.chaosolymp.votemanager.bungee

import de.chaosolymp.votemanager.core.UUIDUtils
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

class DatabaseManager(plugin: BungeePlugin) {

    private val dataSource = plugin.databaseConfig.dataSource;

    fun createTable() {
        val statement =
            this.dataSource.connection.prepareStatement("CREATE TABLE `votes` (`votestamp` TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,`uuid` BINARY(16) NOT NULL,`username` VARCHAR(16) NOT NULL,`vote_page` VARCHAR(32) NOT NULL);")
        statement.execute()
    }

    fun addVote(uuid: UUID, name: String, votePage: String) {
        val statement =
            this.dataSource.connection.prepareStatement("INSERT INTO `votes` (`uuid`, `username`, `vote_page`) VALUES (?, ?, ?)")
        statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
        statement.setString(2, name)
        statement.setString(3, votePage)
        statement.execute()
    }

    fun countVotes(uuid: UUID): Int {
        val statement =
            this.dataSource.connection.prepareStatement("SELECT count (*) from `votes` where uuid = ? And votestamp > now() - interval 30 day")
        statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
        val rs = statement.executeQuery()
        return if (rs.next()) {
            rs.getInt(1)
        } else {
            0
        }
    }

    fun getTopVoters(count: Int): List<TopVoter> {
        val statement =
            this.dataSource.connection.prepareStatement("SELECT uuid, username, COUNT(votestamp) as vote_count from `votes` where votestamp > now() - interval 30 day GROUP BY uuid, username ORDER BY vote_count LIMIT ?")
        statement.setInt(1, count)
        val rs = statement.executeQuery()
        val list = mutableListOf<TopVoter>();
        if (rs.next()) {
            val uuid = UUIDUtils.getUUIDFromBytes(rs.getBytes("uuid"))
            val username = rs.getString("username")
            val voteCount = rs.getInt("vote_count")
            list.add(TopVoter(uuid, username, voteCount))
        }
        return list
    }
}