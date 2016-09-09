package org.web3j.methods.response;

import java.math.BigInteger;

import org.web3j.protocol.Utils;
import org.web3j.protocol.jsonrpc20.Response;

/**
 * eth_gasPrice
 */
public class EthGasPrice extends Response<String> {
    public BigInteger getGasPrice() {
        return Utils.decodeQuantity(getResult());
    }
}
