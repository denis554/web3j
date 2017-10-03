package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed64x48 extends Fixed {
    public static final Fixed64x48 DEFAULT = new Fixed64x48(BigInteger.ZERO);

    public Fixed64x48(BigInteger value) {
        super(64, 48, value);
    }

    public Fixed64x48(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(64, 48, m, n);
    }
}
