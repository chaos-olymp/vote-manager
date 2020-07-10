package de.chaosolymp.votemanager.bungee.listener

import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import com.vexsoftware.votifier.bungee.events.VotifierEvent
import com.vexsoftware.votifier.model.Vote
import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.config.Replacement
import de.chaosolymp.votemanager.bungee.sendMessage

class VoteListener(private val plugin: BungeePlugin) : Listener {

    @EventHandler
    fun handleVote(event: VotifierEvent) {
        val vote: Vote = event.vote
        this.plugin.logger.info(String.format("%s has voted on %s(%s)", vote.username, vote.serviceName, vote.address))

        val count = this.plugin.databaseManager.countVotes(this.plugin.proxy.getPlayer(vote.username).uniqueId) + 1

        for (player in this.plugin.proxy.players) {
            if(count % 50 == 0) {
                player.sendMessage(
                    this.plugin.messageConfiguration.getMessage(
                        "vote.special", arrayOf(
                            Replacement("player", vote.username),
                            Replacement("votes", count)
                        )
                    )
                )
            } else {
                player.sendMessage(
                    this.plugin.messageConfiguration.getMessage(
                        "vote", arrayOf(
                            Replacement("player", vote.username),
                            Replacement("votes", count)
                        )
                    )
                )
            }
            if(player.name.equals(vote.username, true)) {
                this.plugin.databaseManager.addVote(player.uniqueId, player.name, vote.serviceName)
                this.plugin.tneDispatcher.depositMoney(player, 50.0)
            }
        }
    }

}