package de.chaosolymp.votemanager.bungee.command

import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.sendMessage
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

class VoteCommand(private val plugin: BungeePlugin) : Command("vote") {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        sender?.sendMessage(this.plugin.messageConfiguration.getMessage("command.vote-list", emptyArray()))
    }
}