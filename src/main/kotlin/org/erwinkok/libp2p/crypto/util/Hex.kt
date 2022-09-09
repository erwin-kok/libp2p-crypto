package org.erwinkok.libp2p.crypto.util

object Hex {
    private const val hextable = "0123456789abcdef"

    fun encode(bytes: ByteArray): String {
        val dst = ByteArray(bytes.size * 2)
        var index = 0
        for (byte in bytes) {
            dst[index] = hextable[(byte.toUByte().toInt() ushr 4)].code.toByte()
            dst[index + 1] = hextable[(byte.toUByte().toInt() and 0x0f)].code.toByte()
            index += 2
        }
        return String(dst)
    }

    fun decode(data: String): ByteArray {
        val src = data.toByteArray()
        val dst = ByteArray(src.size / 2)
        var srcIndex = 1
        var dstIndex = 0
        while (srcIndex < src.size) {
            val a = fromHexChar(src[srcIndex - 1].toInt().toChar()) ?: throw NumberFormatException("hex: invalid byte: ${src[srcIndex - 1]}")
            val b = fromHexChar(src[srcIndex].toInt().toChar()) ?: throw NumberFormatException("hex: invalid byte: ${src[srcIndex - 1]}")
            dst[dstIndex] = ((a shl 4) or b).toByte()
            srcIndex += 2
            dstIndex++
        }
        if (src.size % 2 == 1) {
            fromHexChar(src[srcIndex - 1].toInt().toChar()) ?: throw NumberFormatException("hex: invalid byte: ${src[srcIndex - 1]}")
            throw NumberFormatException("hex: odd length hex string")
        }
        return dst
    }

    private fun fromHexChar(c: Char): Int? {
        return when (c) {
            in '0'..'9' -> c - '0'
            in 'a'..'f' -> c - 'a' + 10
            in 'A'..'F' -> c - 'A' + 10
            else -> null
        }
    }
}
