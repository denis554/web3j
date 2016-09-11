package org.web3j.methods.response;

import org.web3j.protocol.jsonrpc20.Response;

/**
 * shh_addToGroup
 */
public class SshAddToGroup extends Response<Boolean> {

    public boolean addedToGroup() {
        return getResult();
    }
}
