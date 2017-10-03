package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Ufixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Ufixed112x64 extends Ufixed {
    public static final Ufixed112x64 DEFAULT = new Ufixed112x64(BigInteger.ZERO);

    public Ufixed112x64(BigInteger value) {
        super(112, 64, value);
    }

    public Ufixed112x64(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(112, 64, m, n);
    }
}
