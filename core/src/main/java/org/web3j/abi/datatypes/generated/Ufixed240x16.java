package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Ufixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Ufixed240x16 extends Ufixed {
    public static final Ufixed240x16 DEFAULT = new Ufixed240x16(BigInteger.ZERO);

    public Ufixed240x16(BigInteger value) {
        super(240, 16, value);
    }

    public Ufixed240x16(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(240, 16, m, n);
    }
}
