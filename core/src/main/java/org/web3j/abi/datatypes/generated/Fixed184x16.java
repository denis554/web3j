package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed184x16 extends Fixed {
    public static final Fixed184x16 DEFAULT = new Fixed184x16(BigInteger.ZERO);

    public Fixed184x16(BigInteger value) {
        super(184, 16, value);
    }

    public Fixed184x16(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(184, 16, m, n);
    }
}
