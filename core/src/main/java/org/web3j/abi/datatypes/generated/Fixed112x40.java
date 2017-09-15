package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed112x40 extends Fixed {
    public static final Fixed112x40 DEFAULT = new Fixed112x40(BigInteger.ZERO);

    public Fixed112x40(BigInteger value) {
        super(112, 40, value);
    }

    public Fixed112x40(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(112, 40, m, n);
    }
}
