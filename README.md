# Harmony Java SDK

HarmonyJ is a light-weight Java library for interacting with [Harmony](https://harmony.one) blockchain. For high-level information about Harmony and its goals, visit [harmony.one](https://harmony.one). 
The [harmony white paper](https://harmony.one/pdf/whitepaper.pdf) provides a complete conceptual overview.

## Features
* Partial implementation of Harmony's JSON-RPC client API over HTTP with features
* Local key management	
* Accounts and getting balance
* Creating, signing, and sending transactions

Dependencies
* [web3j](https://github.com/web3j/web3j/)
* [bitcoinj](https://github.com/bitcoinj/bitcoinj)
* Java 8 and Gradle 5.6.4

## Getting started

To get started, it is best to have the latest JDK and Gradle installed.

### Building from the command line

To perform a full build (including JavaDocs and unit/integration tests) use JDK 8+

```
gradle clean build
```

To perform a full build without unit/integration tests use:

```
gradle clean assemble
```

## Examples