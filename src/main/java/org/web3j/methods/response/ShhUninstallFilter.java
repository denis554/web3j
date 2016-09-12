package org.web3j.methods.response;

import org.web3j.protocol.jsonrpc20.Response;

/**
 * shh_uninstallFilter
 */
public class ShhUninstallFilter extends Response<Boolean> {

    public boolean isUninstalled() {
        return getResult();
    }
}
