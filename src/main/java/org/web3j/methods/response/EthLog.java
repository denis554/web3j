package org.web3j.methods.response;

import org.web3j.protocol.jsonrpc20.Response;

import java.util.List;

/**
 * <p>Log object returned by:
 * <ul>
 *     <li>eth_getFilterChanges</li>
 *     <li>eth_getFilterLogs</li>
 *     <li>eth_getLogs</li>
 * </ul>
 * </p>
 *
 * <p>See
 * <a href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getfilterchanges">docs</a>
 * for further details.</p>
 */
public class EthLog extends Response<List<Log>> {

    public List<Log> getLogs() {
        return getResult();
    }
}
