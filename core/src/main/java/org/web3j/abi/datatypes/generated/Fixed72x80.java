package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed72x80 extends Fixed {
    public static final Fixed72x80 DEFAULT = new Fixed72x80(BigInteger.ZERO);

    public Fixed72x80(BigInteger value) {
        super(72, 80, value);
    }

    public Fixed72x80(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(72, 80, m, n);
    }
}
