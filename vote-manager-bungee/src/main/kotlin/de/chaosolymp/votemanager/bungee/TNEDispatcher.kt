package de.chaosolymp.votemanager.bungee

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.core.UUIDUtils
import net.md_5.bungee.api.connection.ProxiedPlayer


class TNEDispatcher(private val plugin: BungeePlugin) {

    fun depositMoney(player: ProxiedPlayer, amount: Double) {
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()

        out.writeUTF("vote:deposit")
        out.write(UUIDUtils.getBytesFromUUID(player.uniqueId))
        out.writeDouble(amount)

        player.server.sendData("BungeeCord", out.toByteArray())
        this.plugin.logger.info("Sent ${player.server.info.name} vote:deposit for ${player.name} with amount $amount")
    }

}
