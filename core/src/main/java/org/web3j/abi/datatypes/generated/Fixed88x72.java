package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed88x72 extends Fixed {
    public static final Fixed88x72 DEFAULT = new Fixed88x72(BigInteger.ZERO);

    public Fixed88x72(BigInteger value) {
        super(88, 72, value);
    }

    public Fixed88x72(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(88, 72, m, n);
    }
}
