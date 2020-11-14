package de.chaosolymp.votemanager.bungee.listener

import com.vexsoftware.votifier.bungee.events.VotifierEvent
import com.vexsoftware.votifier.model.Vote
import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.config.Replacement
import de.chaosolymp.votemanager.bungee.sendMessage
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class VoteListener(private val plugin: BungeePlugin) : Listener {

    @EventHandler
    fun handleVote(event: VotifierEvent) {
        val vote: Vote = event.vote
        this.plugin.logger.info("${vote.username} has voted on ${vote.serviceName}(${vote.address})")

        this.plugin.uuidResolver.resolve(vote.username)?.let {
            val count = this.plugin.databaseManager.countVotes(it) + 1
            var online = false
            this.plugin.proxy.getPlayer(it)?.let { player ->
                // -1 = unknown id
                this.plugin.voteDispatcher.commitAchievements(player, Pair(-1, 1))
                this.plugin.logger.info("Sent achievement commit of ${player.name}")
                online = true
            }

            this.plugin.databaseManager.addVote(it, vote.username, vote.serviceName, online)
            this.plugin.logger.info("Added vote to database")

            for (player in this.plugin.proxy.players) {
                val key: String = if (count % 50 == 0) "vote.special" else "vote.default"

                player.sendMessage(
                    this.plugin.messageConfiguration.getMessage(
                        key, arrayOf(
                            Replacement("player", vote.username),
                            Replacement("votes", count)
                        )
                    )
                )

                if (player.name.equals(vote.username, true)) {
                    player.sendMessage(this.plugin.messageConfiguration.getMessage("vote.reward", arrayOf(
                        Replacement("amount", 50.0)
                    )))
                }
            }
        }


    }

}