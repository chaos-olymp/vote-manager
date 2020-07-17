package de.chaosolymp.votemanager.bungee.command

import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.config.Replacement
import de.chaosolymp.votemanager.bungee.sendMessage
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

class TopVotersCommand(private val plugin: BungeePlugin) : Command("topvoters", null, "vote-top") {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        val topVoters = this.plugin.databaseManager.getTopVoters(10)

        sender?.sendMessage(this.plugin.messageConfiguration.getMessage("command.top.heading", emptyArray()))
        topVoters.forEach {
            sender?.sendMessage(this.plugin.messageConfiguration.getMessage("command.top.element", arrayOf(
                Replacement("rank", it.rank),
                Replacement("player", it.username),
                Replacement("votes", it.count)
            )))
        }

    }
}