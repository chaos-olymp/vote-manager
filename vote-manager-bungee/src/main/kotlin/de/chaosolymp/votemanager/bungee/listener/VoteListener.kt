package de.chaosolymp.votemanager.bungee.listener

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.vexsoftware.votifier.bungee.events.VotifierEvent
import com.vexsoftware.votifier.model.Vote
import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.bungee.config.Replacement
import de.chaosolymp.votemanager.bungee.sendMessage
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class VoteListener(private val plugin: BungeePlugin) : Listener {

    @EventHandler
    fun handleVote(event: VotifierEvent) {
        val vote: Vote = event.vote
        this.plugin.logger.info(String.format("%s has voted on %s(%s)", vote.username, vote.serviceName, vote.address))

        this.getUniqueId(vote.username).ifPresent {

            val count = this.plugin.databaseManager.countVotes(it) + 1

            this.plugin.databaseManager.addVote(it, vote.username, vote.serviceName)
            this.plugin.achievementDispatcher.insertOrUpdateVotes(it)
            this.plugin.tneDispatcher.depositMoney(it, 50.0)

            for (player in this.plugin.proxy.players) {
                val key: String = if (count % 50 == 0) {
                    "vote.special"
                } else {
                    "vote.default"
                }

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

    private fun getUniqueId(username: String): Optional<UUID> {
        for(onlinePlayer in this.plugin.proxy.players) {
            if(onlinePlayer.name.equals(username, true)) {
                return Optional.of(onlinePlayer.uniqueId)
            }
        }

        val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        if(connection.responseCode == 200) {
            val gson = Gson()
            val obj = gson.fromJson(InputStreamReader(connection.inputStream), JsonObject::class.java)
            return Optional.of(UUID.fromString(obj.get("id").asString))
        }
        return Optional.empty()
    }

}