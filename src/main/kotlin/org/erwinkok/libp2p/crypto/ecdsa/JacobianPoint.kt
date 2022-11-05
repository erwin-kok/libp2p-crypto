// Copyright (c) 2022 Erwin Kok. BSD-3-Clause license. See LICENSE file for more details.
package org.erwinkok.libp2p.crypto.ecdsa

import java.math.BigInteger

data class JacobianPoint(val x: BigInteger, val y: BigInteger, val z: BigInteger)
