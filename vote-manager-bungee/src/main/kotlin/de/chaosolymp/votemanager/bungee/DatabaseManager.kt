package de.chaosolymp.votemanager.bungee

import de.chaosolymp.votemanager.bungee.model.TopVoter
import de.chaosolymp.votemanager.bungee.model.VoteResponse
import de.chaosolymp.votemanager.core.UUIDUtils
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.sql.Timestamp
import java.time.Instant
import java.util.*

class DatabaseManager(plugin: BungeePlugin) {

    private val dataSource = plugin.databaseConfig.dataSource;

    fun createTable() {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("CREATE TABLE IF NOT EXISTS `votes` (`id` INT NOT NULL AUTO_INCREMENT, `votestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,`uuid` BINARY(16) NOT NULL,`username` VARCHAR(16) NOT NULL,`vote_page` VARCHAR(32) NOT NULL, `achievement` BOOLEAN, PRIMARY KEY(id))")
            statement.execute()
        }
    }

    fun addVote(uuid: UUID, name: String, votePage: String, achievement: Boolean = false) {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("INSERT INTO `votes` (`uuid`, `votestamp`, `username`, `vote_page`, `achievement`) VALUES (?, ?, ?, ?, ?)")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.setTimestamp(2, Timestamp.from(Instant.now()))
            statement.setString(3, name)
            statement.setString(4, votePage)
            statement.setBoolean(5, achievement)
            statement.execute()
        }
    }

    fun fetchUncommittedAchievementCount(player: ProxiedPlayer): Pair<Int, Int>? {
        this.dataSource.connection.use {
            val statement = it.prepareStatement("SELECT id, COUNT(*) FROM `votes` WHERE uuid = ? AND achievement = FALSE")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(player.uniqueId))
            val rs = statement.executeQuery()
            return if(rs.next()) {
                Pair(rs.getInt(1), rs.getInt(2))
            } else {
                null
            }
        }
    }

    fun setAchievementCommitted(id: Int, value: Boolean) {
        this.dataSource.connection.use {
            val statement = it.prepareStatement("UPDATE `votes` SET achievement = ? WHERE id = ?")
            statement.setBoolean(1, value)
            statement.setInt(2, id)
        }
    }



    fun countVotes(uuid: UUID): Int {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("SELECT COUNT(*) from `votes` where uuid = ? AND MONTH(votestamp) = MONTH(now()) AND YEAR(votestamp) = YEAR(now())")
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
            val statement = it.prepareStatement("SELECT rnk FROM (SELECT uuid, RANK() OVER (ORDER BY cnt DESC) rnk FROM (SELECT uuid, COUNT(*) cnt FROM votes WHERE MONTH(votestamp) = MONTH(now()) AND YEAR(votestamp) = YEAR(now()) GROUP BY votes.uuid) vote_cnt) ranks WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            val rs = statement.executeQuery()
            return if (rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        }
    }

    fun getVoteResult(uuid: UUID): Optional<VoteResponse> {
        this.dataSource.connection.use {
            val statement = it.prepareStatement("SELECT position, count FROM (SELECT uuid, count, @rownum := @rownum + 1 AS position FROM (SELECT uuid, COUNT(*) AS count FROM votes GROUP BY username ORDER BY count DESC) a JOIN (SELECT @rownum := 0) r) b WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            val rs = statement.executeQuery()
            return if (rs.next()) {
                Optional.of(VoteResponse(rs.getInt(1), rs.getInt(2)))
            } else {
                Optional.empty()
            }
        }
    }

    fun  getTopVoters(count: Int): List<TopVoter> {
        this.dataSource.connection.use {
            val statement =
                it.prepareStatement("SELECT uuid, username, COUNT(votestamp) as vote_count from `votes` where MONTH(votestamp) = MONTH(now()) AND YEAR(votestamp) = YEAR(now()) GROUP BY uuid, username ORDER BY vote_count DESC LIMIT ?")
            statement.setInt(1, count)

            val rs = statement.executeQuery()
            val list = mutableListOf<TopVoter>()

            while (rs.next()) {
                val uuid = UUIDUtils.getUUIDFromBytes(rs.getBytes("uuid"))
                val username = rs.getString("username")
                val voteCount = rs.getInt("vote_count")

                val rank = this.getVoteRank(uuid)

                list.add(TopVoter(uuid, username, voteCount, rank))
            }
            return list
        }
    }

}