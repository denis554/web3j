package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed88x128 extends Fixed {
    public static final Fixed88x128 DEFAULT = new Fixed88x128(BigInteger.ZERO);

    public Fixed88x128(BigInteger value) {
        super(88, 128, value);
    }

    public Fixed88x128(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(88, 128, m, n);
    }
}
