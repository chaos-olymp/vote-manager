package de.chaosolymp.votemanager.bungee

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import net.md_5.bungee.api.connection.ProxiedPlayer


class TNEDispatcher(private val plugin: BungeePlugin) {

    fun depositMoney(player: ProxiedPlayer, amount: Double) {
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()

        out.writeUTF("vote:deposit")
        out.writeDouble(amount)

        player.sendData("BungeeCord", out.toByteArray())
    }

}
