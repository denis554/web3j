package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed96x96 extends Fixed {
    public static final Fixed96x96 DEFAULT = new Fixed96x96(BigInteger.ZERO);

    public Fixed96x96(BigInteger value) {
        super(96, 96, value);
    }

    public Fixed96x96(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(96, 96, m, n);
    }
}
