package org.web3j.protocol;

import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.utils.Async;

/**
 * Base service implementation
 */
public abstract class Service implements Web3jService{

    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    @Override
    public <T extends Response> CompletableFuture<T> sendAsync(
            Request jsonRpc20Request, Class<T> responseType) {
        return Async.run(() -> send(jsonRpc20Request, responseType));
    }
}
