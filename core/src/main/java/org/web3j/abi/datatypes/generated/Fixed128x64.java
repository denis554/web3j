package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed128x64 extends Fixed {
    public static final Fixed128x64 DEFAULT = new Fixed128x64(BigInteger.ZERO);

    public Fixed128x64(BigInteger value) {
        super(128, 64, value);
    }

    public Fixed128x64(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(128, 64, m, n);
    }
}
