package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed56x112 extends Fixed {
    public static final Fixed56x112 DEFAULT = new Fixed56x112(BigInteger.ZERO);

    public Fixed56x112(BigInteger value) {
        super(56, 112, value);
    }

    public Fixed56x112(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(56, 112, m, n);
    }
}
