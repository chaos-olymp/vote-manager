package de.chaosolymp.votemanager.bungee.dispatcher

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.bungee.BungeePlugin
import de.chaosolymp.votemanager.core.UUIDUtils
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*


class TNEDispatcher(private val plugin: BungeePlugin) {

    fun depositMoney(uuid: UUID, amount: Double) {
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()

        out.writeUTF("vote:deposit")
        out.write(UUIDUtils.getBytesFromUUID(uuid))
        out.writeDouble(amount)

        this.plugin.proxy.servers["survival"]?.sendData("BungeeCord", out.toByteArray())
    }

}
