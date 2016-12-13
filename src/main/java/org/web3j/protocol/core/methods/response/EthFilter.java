package org.web3j.protocol.core.methods.response;

import java.math.BigInteger;

import org.web3j.utils.Numeric;
import org.web3j.protocol.core.Response;

/**
 * eth_newFilter
 */
public class EthFilter extends Response<String> {
    public BigInteger getFilterId() {
        return Numeric.decodeQuantity(getResult());
    }
}
