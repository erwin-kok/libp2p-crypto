# libp2p-crypto

Cryptographic utilities used by libp2p.

[![ci](https://github.com/erwin-kok/libp2p-crypto/actions/workflows/ci.yaml/badge.svg)](https://github.com/erwin-kok/libp2p-crypto/actions/workflows/ci.yaml)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License](https://img.shields.io/github/license/erwin-kok/libp2p-crypto.svg)](https://github.com/erwin-kok/libp2p-crypto/blob/master/LICENSE)

## Introduction

This project contains various cryptographic utilities used by libp2p. It can generate key pairs (private/public keys),
marshal and unmarshal these public and private keys. Further it is possible to sign and verify messages. And lastly it
supports converting these private/public to/from protocol buffer format.

The library is intended as an internal building block for libp2p-based systems, not as a general-purpose cryptography framework.

## Supported Key Types

The following public/private key types are supported:

- ECDSA
- Ed25519
- Secp256k1
- RSA

## Error Handling

All public APIs use the [`result-monad`](https://github.com/erwin-kok/result-monad) for explicit error handling.

Methods return `Result<T>` rather than throwing exceptions. Callers are expected to handle failures explicitly or propagate them as needed.

Example:

```kotlin
val (privateKey, publicKey) = CryptoUtil.generateKeyPair(KeyType.ECDSA)
    .getOrElse {
        log.error { "Could not generate new key pair. ${errorMessage(it)}" }
        return Err(it)
    }
```

For callers that prefer exception-based control flow, results can be converted explicitly:

```kotlin
val (privateKey, publicKey) = CryptoUtil.generateKeyPair(KeyType.ECDSA).getOrThrow()
```

This will return the key pair when no error occurred, and throws an `Error` exception when an error occurred.

## Key Generation

Generating a new key pair is straight forward:

```kotlin
val (privateKey, publicKey) = CryptoUtil.generateKeyPair(KeyType.ECDSA)
    .onFailure {
        // Could not generate key
    }
// Use keys
```

The key-type can be either:

- KeyType.RSA
- KeyType.ED25519
- KeyType.SECP256K1
- KeyType.ECDSA

## Signing and Verifying messages

Messages are signed using a private key:

```kotlin
val message = "Hello, World".toByteArray()
val signature = privateKey.sign(message)
    .onFailure {
        // Could not sign message
    }
```

Verification is performed using the corresponding public key:

```kotlin
val message = "Hello, World".toByteArray()
val verified = publicKey.verify(message, signature)
    .onFailure {
        // Could not verify message
    }
```

The verified value indicates whether the signature matches the message and public key.

## Marshalling and Unmarshalling

Private keys can be serialized and deserialized:

```kotlin
val privBytes = CryptoUtil.marshalPrivateKey(privateKey)
    .onFailure {
        // Could not marshal private key
    }
// Use privBytes (a ByteArray) to store the privateKey
val privateKey2 = CryptoUtil.unmarshalPrivateKey(privBytes)
    .onFailure {
        // Could not unmarshal private key
    }
// Here `privateKey` and `privateKey2` should be equal
```

Likewise for a publicKey:

```kotlin
val pubBytes = CryptoUtil.marshalPublicKey(publicKey)
    .onFailure {
        // Could not marshal public key
    }
// Use pubBytes (a ByteArray) to store or send the publicKey
val publicKey2 = CryptoUtil.unmarshalPublicKey(pubBytes)
    .onFailure {
        // Could not unmarshal public key
    }
// Here `publicKey` and `publicKey2` should be equal
```

The marshalled representations are suitable for storage or transmission and are compatible with libp2p protocol expectations.

## Scope and Non-Goals

This project intentionally does not aim to:

- Provide a comprehensive cryptography API
- Abstract away cryptographic concepts
- Introduce new cryptographic primitives

It focuses on correctness, interoperability, and explicit APIs aligned with libp2p requirements.

## License

This project is licensed under the BSD-3-Clause license, see [`LICENSE`](LICENSE) file for more details. 
