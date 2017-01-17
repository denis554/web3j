.. web3j documentation master file

web3j
=====

web3j is a lightweight, reactive, type safe Java and Android library for integrating with clients
(nodes) on the Ethereum network:

.. image:: /images/web3j_network.png

This allows you to work with the `Ethereum <https://www.ethereum.org/>`_ blockchain, without the
additional overhead of having to write your own integration code for the platform.


Features
========

- Complete implementation of Ethereum's `JSON-RPC <https://github.com/ethereum/wiki/wiki/JSON-RPC>`_
  client API over HTTP and IPC
- Ethereum wallet support
- Reactive-functional API for working with filters
- Auto-generation of Java smart contract wrappers to create, deploy, transact with and call smart
  contracts from native Java code
- Support for Parity's
  `Personal <https://github.com/ethcore/parity/wiki/JSONRPC-personal-module>`__, and Geth's
  `Personal <https://github.com/ethereum/go-ethereum/wiki/Management-APIs#personal>`__ client APIs
- Support for `Infura <https://infura.io/>`_, so you don't have to run an Ethereum client yourself
- Comprehensive integration tests demonstrating a number of the above scenarios
- Command line tools
- Android compatible
- Support for JP Morgan's Quorum via `web3j-quorum <https://github.com/web3j/quorum>`_


Dependencies
============

It has seven runtime dependencies:

- `RxJava <https://github.com/ReactiveX/RxJava>`_ for its reactive-functional API
- `Apache HTTP Client <https://hc.apache.org/httpcomponents-client-ga/index.html>`_
- `Jackson Core <https://github.com/FasterXML/jackson-core>`_ for fast JSON
  serialisation/deserialisation
- `Bouncy Castle <https://www.bouncycastle.org/>`_ and
  `Java Scrypt <https://github.com/wg/scrypt>`_ for crypto
- `JavaPoet <https://github.com/square/javapoet>`_ for generating smart contract wrappers
- `Jnr-unixsocket <https://github.com/jnr/jnr-unixsocket>`_ for \*nix IPC


Contents:
=========

.. toctree::
   :maxdepth: 2

   introduction.rst
   getting_started.rst
   transactions.rst
   smart_contracts.rst
   filters.rst
   rlp.rst
   abi.rst
   command_line.rst
   management_apis.rst
   infura.rst
   trouble.rst
   development.rst
   links.rst
   credits.rst
