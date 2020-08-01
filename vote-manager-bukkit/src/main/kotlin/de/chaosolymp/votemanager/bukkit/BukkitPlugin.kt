package de.chaosolymp.votemanager.bukkit

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.core.UUIDUtils
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener

class BukkitPlugin: JavaPlugin(), PluginMessageListener {

    lateinit var economy: Economy

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        if (!setupEconomy() ) {
            this.logger.severe("The vote manager plugin needs Vault.")
            this.server.pluginManager.disablePlugin(this)
            return
        }
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
        this.economy = rsp.provider
        return true
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if(channel.equals("BungeeCord", ignoreCase = true) || channel.equals("bungeecord:main", ignoreCase = true)) {
            val input: ByteArrayDataInput = ByteStreams.newDataInput(message)
            val subChannel: String = input.readUTF()
            if (subChannel.equals("vote:deposit", ignoreCase = true)) {
                val uuidArray = ByteArray(16)
                input.readFully(uuidArray)
                val uuid = UUIDUtils.getUUIDFromBytes(uuidArray)
                val amount: Double = input.readDouble()
                val target = this.server.getOfflinePlayer(uuid)
                this.depositMoney(target, amount)
            }
        }
    }

    private fun depositMoney(target: OfflinePlayer, amount: Double) {
        this.economy.depositPlayer(target, amount)
    }
}