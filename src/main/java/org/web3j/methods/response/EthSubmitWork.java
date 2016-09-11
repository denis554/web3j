package org.web3j.methods.response;

import org.web3j.protocol.jsonrpc20.Response;

import java.util.List;

/**
 * eth_submitWork
 */
public class EthSubmitWork extends Response<Boolean> {

    public boolean solutionValid() {
        return getResult();
    }
}
