package de.chaosolymp.votemanager.bukkit

import com.google.common.io.ByteArrayDataInput
import de.chaosolymp.votemanager.core.UUIDUtils
import java.util.*

fun ByteArrayDataInput.readUUID(): UUID {
    val uuidArray = ByteArray(16)
    this.readFully(uuidArray)
    return UUIDUtils.getUUIDFromBytes(uuidArray)
}