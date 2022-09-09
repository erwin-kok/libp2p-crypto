package org.erwinkok.libp2p.crypto.secp256k1

data class SplitK(
    val k1Bytes: ByteArray,
    val k2Bytes: ByteArray,
    val k1Sign: Int,
    val k2Sign: Int
)
