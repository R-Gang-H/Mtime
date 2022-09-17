package com.kotlin.android.api.header

import java.net.NetworkInterface
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 唯一ID生成器
 *
 * Created on 2020/8/7.
 *
 * @author o.s
 */
object CID {

    /**
     * 最低阶的三个字节
     */
    private const val LOW_ORDER_THREE_BYTES = 0x00ffffff

    /**
     * 十六进制数字集合
     */
    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    private val machineId: Int
    private val processId: Short
    private val nextCounter: AtomicInteger

    init {
        machineId = generateMachineId()
        processId = android.os.Process.myPid().toShort()
        nextCounter = AtomicInteger(SecureRandom().nextInt())
    }

    /**
     * 获取唯一ID
     */
    fun get(): String {
        val time = dateToTimestampSeconds(Date())
        val counter = nextCounter.getAndIncrement() and LOW_ORDER_THREE_BYTES

        val bytes = toByteArray(time, machineId, processId, counter)
        val chars = CharArray(24)
        var index = 0
        bytes.forEach { b ->
            chars[index++] = HEX_DIGITS[b.toInt() shr 4 and 0xF]
            chars[index++] = HEX_DIGITS[b.toInt() and 0xF]
        }
        return String(chars)
    }

    private fun generateMachineId(): Int {
        var machineId: Int
        try {
            val sb = StringBuilder()
            val e = NetworkInterface.getNetworkInterfaces()
            while (e.hasMoreElements()) {
                e.nextElement()?.apply {
                    sb.append(toString())
                    hardwareAddress?.apply {
                        try {
                            ByteBuffer.wrap(this).apply {
                                sb.append(char)
                                sb.append(char)
                                sb.append(char)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            machineId = sb.toString().hashCode()
        } catch (e: Exception) {
            machineId = SecureRandom().nextInt()
        }
        return machineId and LOW_ORDER_THREE_BYTES
    }

    private fun toByteArray(time: Int, machineId: Int, processId: Short, counter: Int): ByteArray {
        val bytes = ByteArray(12)
        bytes[0] = int3(time)
        bytes[1] = int2(time)
        bytes[2] = int1(time)
        bytes[3] = int0(time)
        bytes[4] = int2(machineId)
        bytes[5] = int1(machineId)
        bytes[6] = int0(machineId)
        bytes[7] = short1(processId)
        bytes[8] = short0(processId)
        bytes[9] = int2(counter)
        bytes[10] = int1(counter)
        bytes[11] = int0(counter)
        return bytes
    }

    private fun int3(x: Int): Byte {
        return (x shr 24).toByte()
    }

    private fun int2(x: Int): Byte {
        return (x shr 16).toByte()
    }

    private fun int1(x: Int): Byte {
        return (x shr 8).toByte()
    }

    private fun int0(x: Int): Byte {
        return x.toByte()
    }

    private fun short1(x: Short): Byte {
        return (x.toInt() shr 8).toByte()
    }

    private fun short0(x: Short): Byte {
        return x.toByte()
    }

    private fun dateToTimestampSeconds(time: Date): Int {
        return (time.time / 1000).toInt()
    }
}
