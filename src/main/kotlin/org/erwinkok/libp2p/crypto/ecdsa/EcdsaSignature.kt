package org.erwinkok.libp2p.crypto.ecdsa

import java.math.BigInteger

data class EcdsaSignature(val r: BigInteger, val s: BigInteger)
