package org.web3j.protocol.parity;

import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.methods.response.*;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * JSON-RPC 2.0 Integration Tests.
 */
public class ParityIT {

    private static String PASSWORD = "1n5ecur3P@55w0rd";
    private Parity parity;


    public ParityIT() {
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    }

    @Before
    public void setUp() {
        this.parity = Parity.build(new HttpService());
    }

    @Test
    public void testPersonalListAccounts() throws Exception {
        PersonalListAccounts personalListAccounts = parity.personalListAccounts().send();
        assertNotNull(personalListAccounts.getAccountIds());
    }

    @Test
    public void testPersonalNewAccount() throws Exception {
        NewAccountIdentifier newAccountIdentifier = createAccount();
        assertFalse(newAccountIdentifier.getAccountId().isEmpty());
    }

    @Test
    public void testPersonalUnlockAccount() throws Exception {
        NewAccountIdentifier newAccountIdentifier = createAccount();
        PersonalUnlockAccount personalUnlockAccount = parity.personalUnlockAccount(
                newAccountIdentifier.getAccountId(), PASSWORD).send();
        assertTrue(personalUnlockAccount.accountUnlocked());
    }

    @Test
    public void testPersonalSign() throws Exception {
        PersonalListAccounts personalListAccounts = parity.personalListAccounts().send();
        assertNotNull(personalListAccounts.getAccountIds());

        PersonalSign personalSign = parity.personalSign("0xdeadbeaf",personalListAccounts.getAccountIds().get(0),"123").send();
        // address : 0xadfc0262bbed8c1f4bd24a4a763ac616803a8c54
        assertNotNull(personalSign.getSignedMessage());
        // result : 0x80ab45a65bd5acce92eac60b52235a34eee647c8dbef8e62108be90a4ac9a22222f87dd8934fc71545cf2ea1b71d8b62146e6d741ac6ee12fd1d1d740adca9021b
    }

    @Test
    public void testPersonalecRecover() throws Exception {
        PersonalEcRecover personalEcRecover = parity.personalEcRecover("0xdeadbeaf","0x80ab45a65bd5acce92eac60b52235a34eee647c8dbef8e62108be90a4ac9a22222f87dd8934fc71545cf2ea1b71d8b62146e6d741ac6ee12fd1d1d740adca9021b").send();
        assertEquals("0xadfc0262bbed8c1f4bd24a4a763ac616803a8c54",personalEcRecover.getRecoverAccountId());
    }

    private NewAccountIdentifier createAccount() throws Exception {
        return parity.personalNewAccount(PASSWORD).send();
    }
}
