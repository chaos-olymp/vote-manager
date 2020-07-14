package de.chaosolymp.votemanager.bungee.command

import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.config.Replacement
import de.chaosolymp.votemanager.bungee.sendMessage
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import java.util.*

class VotesCommand(private val plugin: BungeePlugin) : Command("votes", null, "vote-count") {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (args != null) {
            val target: UUID
            if (args.isEmpty()) {
                if (sender != null) {
                    if (sender is ProxiedPlayer) {
                        target = sender.uniqueId
                        this.sendVotes(sender, target)
                    } else {
                        sender.sendMessage(
                            this.plugin.messageConfiguration.getMessage(
                                "error.not-a-player",
                                emptyArray()
                            )
                        )
                    }
                }
            } else if (args.size == 1) {
                val targetPlayer = this.plugin.proxy.getPlayer(args[0])
                if(targetPlayer != null && targetPlayer.isConnected) {
                    target = targetPlayer.uniqueId
                    this.sendVotes(sender, target)
                } else {
                    sender?.sendMessage(
                        this.plugin.messageConfiguration.getMessage(
                            "error.player-not-found",
                            emptyArray()
                        )
                    )
                }
            } else {
                sender?.sendMessage(
                    this.plugin.messageConfiguration.getMessage(
                        "error.syntax",
                        arrayOf(Replacement("syntax", "/votes [Name]"))
                    )
                )
            }
        }
    }

    private fun sendVotes(sender: CommandSender?, target: UUID?) {
        if (target != null) {
            val count = this.plugin.databaseManager.countVotes(target)
            val rank = this.plugin.databaseManager.getVoteRank(target)
            sender?.sendMessage(
                this.plugin.messageConfiguration.getMessage(
                    "command.votes.self", arrayOf(
                        Replacement("votes", count),
                        Replacement("rank", rank)
                    )
                )
            )
        }
    }
}


