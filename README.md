# web3j 
## Web3 Java Ðapp API

web3j is a lightweight Java library for integrating with clients (nodes) on the Ethereum network.

[![Build Status](https://travis-ci.org/conor10/web3j.svg?branch=master)](https://travis-ci.org/conor10/web3j)
[![codecov.io](http://codecov.io/github/conor10/web3j/coverage.svg?branch=master)](http://codecov.io/github/conor10/web3j?branch=master)

[ JVM application ] + [ web3j ] <---> [ Ethereum node ]

It only has two runtime dependencies:

* Apache HTTP Client 
* Jackson Core for fast JSON serialisation/deserialisation


## Getting Started

Add the following dependency to your project:

### Maven

```
   <repositories>
     <repository>
       <id>oss.jfrog.org</id>
       <name>Repository from Bintray</name>
       <url>http://dl.bintray.com/conor10/maven</url>
     </repository>
   </repositories>

   <dependency>
     <groupId>org.web3j</groupId>
     <artifactId>core</artifactId>
     <version>1.0.0-RELEASE</version>
   </dependency>
```

### Gradle

```
repositories {
   maven {url "http://dl.bintray.com/conor10/maven"}
}
compile ("org.web3j:core:1.0.0-RELEASE")
```


Start up an Ethereum client if you don't already have one running, such as [Geth](https://github.com/ethereum/go-ethereum/wiki/geth):

geth --rpc --testnet


To send synchronous requests:


```java
Web3jService web3 = new Web3jService();  // defaults to http://localhost:8545/
Web3ClientVersion clientVersion = web3.web3ClientVersion().send();
clientVersion.getWeb3ClientVersion();
```

To send asynchronous requests using a Future:

```java
Web3jService web3 = new Web3jService();
Web3ClientVersion clientVersion = web3.web3ClientVersion().sendAsync().get();
clientVersion.getWeb3ClientVersion();
```


## Further Details

web3j is based on specifications from the [JSON-RPC](https://github.com/ethereum/wiki/wiki/JSON-RPC) and [web3.js](https://github.com/ethereum/web3.js) projects. 

It provides type safe access to all responses. Optional or null responses are wrapped in Java 8's [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) type.

Async requests are handled using Java 8's [CompletableFutures](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html).

Quantity data types are returned as [BigInteger](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html)'s. For simple results, you can obtain the quantity as a String via `org.web3j.protocol.jsonrpc20.Response.getResult()`.


## Tested clients

Geth
* 1.4.10-stable-5f55d95a (in progress)

More coming soon.

You can run the integration test class `org.web3j.protocol.jsonrpc20.ProtocolIT` verify other clients.


## Coming Soon

* Offline transaction signing
* IPC interface support
* WebSocket interface support


## Related projects

For a .NET implementation, check out [Nethereum](https://github.com/Nethereum/Nethereum).
 
For a pure Java implementations of Ethereum, check out the great work that [EthereumJ](https://github.com/ethereum/ethereumj) are doing.


## Thanks and Credits

* The [Nethereum](https://github.com/Nethereum/Nethereum) project for the inspiration
* [Othera](https://www.othera.com.au/) for the great things they are building on the platform
* The [Finhaus](http://finhaus.com.au/) guys for putting me onto Nethereum
* Everyone involved in the Ethererum project and surrounding ecosystem
