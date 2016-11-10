package org.web3j.console;

import java.io.File;
import java.io.IOException;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import static org.web3j.utils.Console.exitError;

/**
 * Simple class for creating a wallet file.
 */
public class WalletUpdater extends WalletManager {

    public static void main(String[] args) {
        if (args.length != 1) {
            exitError("You must provide an existing wallet file");
        } else {
            new WalletUpdater().run(args[0]);
        }
    }

    private void run(String walletFileLocation) {
        File walletFile = new File(walletFileLocation);
        Credentials credentials = getCredentials(walletFile);

        console.printf("Wallet for address " + credentials.getAddress() + " loaded\n");

        String newPassword = getPassword("Please enter a new wallet file password: ");

        String destinationDir = getDestinationDir();
        File destination = createDir(destinationDir);

        try {
            String walletFileName = WalletUtils.generateWalletFile(
                    newPassword, credentials.getEcKeyPair(), destination);
            console.printf("New wallet file " + walletFileName +
                    " successfully created in: " + destinationDir + "\n");
        } catch (CipherException|IOException e) {
            exitError(e);
        }

        String delete = console.readLine(
                "Would you like to delete your existing wallet file (Y/N)? [N]: ");
        if (delete.toUpperCase().equals("Y")) {
            if (!walletFile.delete()) {
                exitError("Unable to remove wallet file\n");
            } else {
                console.printf("Deleted previous wallet file: %s\n", walletFile.getName());
            }
        }
    }
}
