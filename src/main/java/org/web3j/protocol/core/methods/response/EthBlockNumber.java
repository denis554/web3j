package org.web3j.protocol.core.methods.response;

import java.math.BigInteger;

import org.web3j.protocol.utils.Codec;
import org.web3j.protocol.core.Response;

/**
 * eth_blockNumber
 */
public class EthBlockNumber extends Response<String> {
    public BigInteger getBlockNumber() {
        return Codec.decodeQuantity(getResult());
    }
}
