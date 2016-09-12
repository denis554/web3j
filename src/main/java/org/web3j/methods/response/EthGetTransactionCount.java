package org.web3j.methods.response;

import java.math.BigInteger;

import org.web3j.protocol.Utils;
import org.web3j.protocol.jsonrpc20.Response;

/**
 * eth_getTransactionCount
 */
public class EthGetTransactionCount extends Response<String> {
    public BigInteger getTransactionCount() {
        return Utils.decodeQuantity(getResult());
    }
}
