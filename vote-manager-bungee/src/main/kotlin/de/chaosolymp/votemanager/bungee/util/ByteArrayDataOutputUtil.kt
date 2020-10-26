package de.chaosolymp.votemanager.bungee.util

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import de.chaosolymp.votemanager.core.UUIDUtils
import java.util.*

object ByteArrayDataOutputUtil {
    fun createCommitOutput(uniqueId: UUID, id: Int, count: Int): ByteArray {
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput(47)

        out.writeUTF("vote:commit") // 4 byte + length
        out.write(UUIDUtils.getBytesFromUUID(uniqueId)) // 16 byte
        out.writeInt(id) // 4 byte
        out.writeInt(count) // 4 byte
        out.writeDouble(50.0) // 8 byte

        return out.toByteArray()
    }
}