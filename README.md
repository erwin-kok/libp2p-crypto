# libp2p-crypto

[![ci](https://github.com/erwin-kok/libp2p-crypto/actions/workflows/ci.yaml/badge.svg)](https://github.com/erwin-kok/libp2p-crypto/actions/workflows/ci.yaml)
[![License](https://img.shields.io/github/license/erwin-kok/libp2p-crypto.svg)](https://github.com/erwin-kok/libp2p-crypto/blob/master/LICENSE)

## Introduction

This project contains various cryptographic utilities used by libp2p. It can generate key pairs (private/public keys), 
marshal and unmarshal these public and private keys. Further it is possible to sign and verify messages. And lastly it
supports converting these private/public to/from protocol buffer format. 

Four cryptographic key types are currently supported:
- ecdsa
- ed25519
- secp256k1
- rsa

## Using the Result Monad

This project is using the [result-monad](https://github.com/erwin-kok/result-monad)

This means that all methods of `CryptoUtil` return a `Result<...>`. The caller can check whether an error was generated, 
or it can use the value. For example:

```kotlin
val (privateKey, publicKey) = CryptoUtil.generateKeyPair(KeyType.ECDSA)
    .getOrElse {
        log.error { "Could not generate new key pair. ${errorMessage(it)}" }
        return Err(it)
    }
```

In the examples below `OnFailure` is used as a convenience, but other methods can be used as well.

If you would like to throw the Error instead, do:

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

With a private key, you can sign a message:

```kotlin
val message = "Hello, World".toByteArray()
val signature = privateKey.sign(message)
    .onFailure {
        // Could not sign message
    }
```

To verify a message using a provided signature:
```kotlin
val message = "Hello, World".toByteArray()
val verified = publicKey.verify(message, signature)
    .onFailure {
        // Could not verify message
    }
```

`verified` is a boolean indicating that the message was correct with respect to the given signature.
Obviously, you can only verify a message using a publicKey that matches the privateKey that signed this message.

## Marshalling and Unmarshalling
To marshal a privateKey, do:
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

## Contributing

Bug reports and pull requests are welcome on [GitHub](https://github.com/erwin-kok/libp2p-crypto).

## Contact

If you want to contact me, please write an e-mail to: [github@erwinkok.org](mailto:github@erwinkok.org)

## License

This project is licensed under the BSD-3-Clause license, see [`LICENSE`](LICENSE) file for more details. 
