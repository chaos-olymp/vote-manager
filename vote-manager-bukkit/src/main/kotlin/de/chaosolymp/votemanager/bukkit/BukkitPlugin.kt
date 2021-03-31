package de.chaosolymp.votemanager.bukkit

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.File
import java.util.*
import org.bukkit.configuration.file.FileConfiguration




class BukkitPlugin: JavaPlugin(), PluginMessageListener {

    private lateinit var economy: Economy

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        if (!setupEconomy() ) {
            this.logger.severe("The vote manager plugin needs Vault.")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        config.addDefault("command", "token give {player} votebox")
        config.options().copyDefaults(true)
        saveConfig()

        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        this.server.messenger.registerIncomingPluginChannel(this, "BungeeCord", this)
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

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if(channel.equals("BungeeCord", ignoreCase = true) || channel.equals("bungeecord:main", ignoreCase = true)) {
            val input: ByteArrayDataInput = ByteStreams.newDataInput(message)
            val subChannel: String = input.readUTF()
            if (subChannel == "vote:deposit") {
                val uuid: UUID = input.readUUID()
                val amount: Double = input.readDouble()
                val target = this.server.getOfflinePlayer(uuid)
                this.depositMoney(target, amount)
            } else if(subChannel == "vote:achievements") {
                val uuid: UUID = input.readUUID()
                val optional = this.server.onlinePlayers.stream().filter { it.uniqueId == uuid }.findFirst()

                if(optional.isPresent) {
                    this.server.dispatchCommand(this.server.consoleSender, config.getString("command")!!.replace("{player}", optional.get().name))
                    if(this.server.pluginManager.getPlugin("AdvancedAchievements") != null) {
                        this.server.dispatchCommand(
                            this.server.consoleSender,
                            "aach add custom.vote 1 ${optional.get().name}"
                        )
                    } else {
                        this.logger.warning("Got invalid achievement increase request with mode=AdvancedAchievements.")
                    }
                } else {
                    this.logger.warning("Got achievement increase request of offline player.")
                }
            } else if(subChannel == "vote:mode") {
                val output = ByteStreams.newDataOutput()

                val server = input.readUTF()
                val bool = this.server.pluginManager.getPlugin("AdvancedAchievements") == null

                output.writeUTF("vote:mode")
                output.writeUTF(server)
                output.writeBoolean(bool)

                player.sendPluginMessage(this, "BungeeCord", output.toByteArray())
            }
        }
    }

    private fun depositMoney(target: OfflinePlayer, amount: Double) {
        this.economy.depositPlayer(target, amount)
    }
}
