package de.chaosolymp.votemanager.core.tests

import de.chaosolymp.votemanager.core.UUIDUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class UUIDUtilsTests {
    @Test
    fun `Convert UUIDv1 to ByteArray`() {
        val uuid = UUID.fromString("2d2f6f74-17c0-11eb-adc1-0242ac120002")

        val expected: Array<Byte> = arrayOf(45, 47, 111, 116, 23, -64, 17, -21, -83, -63, 2, 66, -84, 18, 0, 2)
        val actual = UUIDUtils.getBytesFromUUID(uuid)
        Assertions.assertArrayEquals(expected.toByteArray(), actual)
    }

    @Test
    fun `Convert ByteArray to UUIDv1`() {
        val expected = UUID.fromString("57d8548a-17bf-11eb-adc1-0242ac120002")

        val actualBytes: Array<Byte> = arrayOf(87, -40, 84, -118, 23, -65, 17, -21, -83, -63, 2, 66, -84, 18, 0, 2)
        val actual = UUIDUtils.getUUIDFromBytes(actualBytes.toByteArray())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `Convert UUIDv4 to ByteArray`() {
        val uuid = UUID.fromString("430e82d4-2bca-4662-8e6a-e39d3427d1f7")

        val expected: Array<Byte> = arrayOf(67, 14, -126, -44, 43, -54, 70, 98, -114, 106, -29, -99, 52, 39, -47, -9)
        val actual = UUIDUtils.getBytesFromUUID(uuid)
        Assertions.assertArrayEquals(expected.toByteArray(), actual)
    }

    @Test
    fun `Convert ByteArray to UUIDv4`() {
        val expected = UUID.fromString("8188929c-a6b0-bac4-3c46-505a646e787f")

        val actualBytes: Array<Byte> = arrayOf(-127, -120, -110, -100, -90, -80, -70, -60, 60, 70, 80, 90, 100, 110, 120, 127)
        val actual = UUIDUtils.getUUIDFromBytes(actualBytes.toByteArray())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `Convert UUIDv6 to ByteArray`() {
        val uuid = UUID.fromString("1eb17c09-b17a-65b0-3377-69f63ce2be5b")

        val expected: Array<Byte> = arrayOf(30, -79, 124, 9, -79, 122, 101, -80, 51, 119, 105, -10, 60, -30, -66, 91)
        val actual = UUIDUtils.getBytesFromUUID(uuid)
        Assertions.assertArrayEquals(expected.toByteArray(), actual)
    }

    @Test
    fun `Convert ByteArray to UUIDv6`() {
        val expected = UUID.fromString("1eb17c19-bb50-61b0-7a2a-5996f76ad8f6")

        val actualBytes: Array<Byte> = arrayOf(30, -79, 124, 25, -69, 80, 97, -80, 122, 42, 89, -106, -9, 106, -40, -10)
        val actual = UUIDUtils.getUUIDFromBytes(actualBytes.toByteArray())

        Assertions.assertEquals(expected, actual)
    }


}