package org.web3j.protocol.parity;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.parity.methods.request.Wallet;
import org.web3j.protocol.parity.methods.response.*;
import org.web3j.utils.Numeric;

/**
 * JSON-RPC 2.0 factory implementation for Parity.
 */
public class JsonRpc2_0Parity extends JsonRpc2_0Web3j implements Parity {

    public JsonRpc2_0Parity(Web3jService web3jService) {
        super(web3jService);
    }

    @Override
    public Request<?, PersonalSignerEnabled> personalSignerEnabled() {
        return new Request<>(
                "personal_signerEnabled",
                Collections.<String>emptyList(),
                ID,
                web3jService,
                PersonalSignerEnabled.class);
    }

    @Override
    public Request<?, PersonalListAccounts> personalListAccounts() {
        return new Request<>(
                "personal_listAccounts",
                Collections.<String>emptyList(),
                ID,
                web3jService,
                PersonalListAccounts.class);
    }

    @Override
    public Request<?, NewAccountIdentifier> personalNewAccount(String password) {
        return new Request<>(
                "personal_newAccount",
                Arrays.asList(password),
                ID,
                web3jService,
                NewAccountIdentifier.class);
    }

    @Override
    public Request<?, NewAccountIdentifier> personalNewAccountFromPhrase(String phrase, String password) {
        return new Request<>(
                "personal_newAccountFromPhrase",
                Arrays.asList(phrase, password),
                ID,
                web3jService,
                NewAccountIdentifier.class);
    }

    @Override
    public Request<?, NewAccountIdentifier> personalNewAccountFromWallet(Wallet wallet, String password) {
        return new Request<>(
                "personal_newAccountFromWallet",
                Arrays.asList(wallet, password),
                ID,
                web3jService,
                NewAccountIdentifier.class);
    }

    @Override
    public Request<?, PersonalUnlockAccount> personalUnlockAccount(String accountId, String password, BigInteger duration) {
        String encodedDuration = null;  // send null if no duration is provided
        if (duration != null) {
            encodedDuration = Numeric.encodeQuantity(duration);
        }

        return new Request<>(
                "personal_unlockAccount",
                Arrays.asList(accountId, password, encodedDuration),
                ID,
                web3jService,
                PersonalUnlockAccount.class);
    }

    @Override
    public Request<?, PersonalUnlockAccount> personalUnlockAccount(String accountId, String password) {
        return personalUnlockAccount(accountId, password, null);
    }

    @Override
    public Request<?, EthSendTransaction> personalSignAndSendTransaction(
            Transaction transaction, String password) {
        return new Request<>(
                "personal_signAndSendTransaction",
                Arrays.asList(transaction, password),
                ID,
                web3jService,
                EthSendTransaction.class);
    }

    @Override
    public Request<?, VoidResponse> personalSetAccountName(String accountId, String newAccountName) {
        return new Request<>(
                "personal_setAccountName",
                Arrays.asList(accountId, newAccountName),
                ID,
                web3jService,
                VoidResponse.class);
    }

    @Override
    public Request<?, VoidResponse> personalSetAccountMeta(
            String accountId, Map<String, Object> metadata) {
        return new Request<>(
                "personal_setAccountMeta",
                Arrays.asList(accountId, metadata),
                ID,
                web3jService,
                VoidResponse.class);
    }

    @Override
    public Request<?, PersonalAccountsInfo> personalAccountsInfo() {
        return new Request<>(
                "personal_accountsInfo",
                Collections.<String>emptyList(),
                ID,
                web3jService,
                PersonalAccountsInfo.class);
    }

    @Override
    public Request<?, PersonalRequestsToConfirm> personalRequestsToConfirm() {
        return new Request<>(
                "personal_requestsToConfirm",
                Collections.<String>emptyList(),
                ID,
                web3jService,
                PersonalRequestsToConfirm.class);
    }

    @Override
    public Request<?, EthSendTransaction> personalConfirmRequest(String requestId, Transaction transaction, String password) {
        return new Request<>(
                "personal_confirmRequest",
                Arrays.asList(requestId, transaction, password),
                ID,
                web3jService,
                EthSendTransaction.class);
    }

    @Override
    public Request<?, PersonalRejectRequest> personalRejectRequest(String requestId) {
        return new Request<>(
                "personal_rejectRequest",
                Arrays.asList(requestId),
                ID,
                web3jService,
                PersonalRejectRequest.class);
    }
}
