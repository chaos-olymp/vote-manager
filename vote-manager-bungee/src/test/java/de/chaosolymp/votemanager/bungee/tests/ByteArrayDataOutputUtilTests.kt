package de.chaosolymp.votemanager.bungee.tests

import de.chaosolymp.votemanager.bungee.util.ByteArrayDataOutputUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ByteArrayDataOutputUtilTests {

    private val random: Random = Random()

    @Test
    fun `Compare commit ByteArray length`() {
        val uuid = UUID.randomUUID()
        val id = random.nextInt(Int.MAX_VALUE)
        val count = random.nextInt(Int.MAX_VALUE)

        val bytes = ByteArrayDataOutputUtil.createCommitOutput(uuid, id, count)

        Assertions.assertEquals(45, bytes.size)
    }
}