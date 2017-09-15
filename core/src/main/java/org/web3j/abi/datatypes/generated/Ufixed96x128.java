package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Ufixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Ufixed96x128 extends Ufixed {
    public static final Ufixed96x128 DEFAULT = new Ufixed96x128(BigInteger.ZERO);

    public Ufixed96x128(BigInteger value) {
        super(96, 128, value);
    }

    public Ufixed96x128(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(96, 128, m, n);
    }
}
