package de.chaosolymp.votemanager.bukkit

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import com.hm.achievement.api.AdvancedAchievementsAPI
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher
import com.hm.achievement.category.MultipleAchievements
import de.chaosolymp.votemanager.core.*
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.*


class BukkitPlugin: JavaPlugin(), PluginMessageListener {

    lateinit var economy: Economy
    private var advancedAchievementsAPI: AdvancedAchievementsAPI? = null

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        if (!setupEconomy() ) {
            this.logger.severe("The vote manager plugin needs Vault.")
            this.server.pluginManager.disablePlugin(this)
            return
        }
        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        this.server.messenger.registerIncomingPluginChannel(this, "BungeeCord", this)
        AdvancedAchievementsAPIFetcher.fetchInstance().ifPresent {
            this.advancedAchievementsAPI = it
        }
        this.logger.info("Plugin warmup finished (Took ${System.currentTimeMillis() - startTime}ms)")
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp =
            server.servicesManager.getRegistration(
                Economy::class.java
            )
                ?: return false
        economy = rsp.provider
        return true
    }

    @Suppress("UnstableApiUsage")
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if(channel == "BungeeCord" || channel == "bungeecord:main") {
            val input: ByteArrayDataInput = ByteStreams.newDataInput(message)
            val subChannel: String = input.readUTF()
            if (subChannel == VOTE_DEPOSIT_CHANNEL) {
                val uuid: UUID = input.readUUID()
                val amount: Double = input.readDouble()
                val target = this.server.getOfflinePlayer(uuid)
                this.depositMoney(target, amount)
            } else if(subChannel == VOTE_ACHIEVEMENT_CHANNEL) {
                val uuid: UUID = input.readUUID()
                val optional = this.server.onlinePlayers.stream().filter { it.uniqueId == uuid }.findFirst()

                if(optional.isPresent) {
                    this.advancedAchievementsAPI?.incrementCategoryForPlayer(MultipleAchievements.CUSTOM, "vote", optional.get(), 1)
                } else {
                    this.logger.warning("Got achievement increase request of offline player.")
                }
            } else if(subChannel == VOTE_MODE_CHANNEL) {
                val server = input.readUTF()
                val bool = this.server.pluginManager.getPlugin("AdvancedAchievements") == null

                val output = ByteStreams.newDataOutput(18 + server.length)

                output.writeUTF(VOTE_MODE_CHANNEL) // 4 byte + length
                output.writeUTF(server) // 4 byte + length
                output.writeBoolean(bool) // 1 byte

                player.sendPluginMessage(this, "BungeeCord", output.toByteArray())
            } else if(subChannel == VOTE_COMMIT_CHANNEL) {
                val uuid: UUID = input.readUUID()
                val target = this.server.getOfflinePlayer(uuid)

                val id = input.readInt()
                val achievementIncrease = input.readInt()
                val singleBonus = input.readDouble()

                this.logger.info("Got commit #$id by $uuid")

                val bonus = achievementIncrease * singleBonus
                this.advancedAchievementsAPI?.let {
                    it.incrementCategoryForPlayer(MultipleAchievements.CUSTOM, "vote", this.server.getPlayer(uuid), 1)
                    this.logger.info("Increment achievement for commit #$id")
                }
                this.depositMoney(target, bonus)
                this.logger.info("Successfully deposited money for commit #$id (bonus: $bonus)")

                if(id != -1) {
                    val output = ByteStreams.newDataOutput(27)
                    output.writeUTF(VOTE_COMMIT_SUCCESS_CHANNEL) // 4 byte + length
                    output.writeInt(id) // 4 byte

                    player.sendPluginMessage(this, "BungeeCord", output.toByteArray())
                    this.logger.info("Sent commit-success for commit #$id")
                }
            }
        }
    }

    private fun depositMoney(target: OfflinePlayer, amount: Double) {
        this.economy.depositPlayer(target, amount)
    }
}
