package org.web3j.protocol.core.methods.request;

/**
 * Filter implementation as per <a href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_newfilter">docs</a>
 */
public class ShhFilter extends Filter {
    private String toBlock;

    public ShhFilter(String toBlock) {
        super();
        this.toBlock = toBlock;
    }

    public String getToBlock() {
        return toBlock;
    }

    @Override
    Filter getThis() {
        return this;
    }
}
