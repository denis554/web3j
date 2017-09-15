package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Ufixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Ufixed120x16 extends Ufixed {
    public static final Ufixed120x16 DEFAULT = new Ufixed120x16(BigInteger.ZERO);

    public Ufixed120x16(BigInteger value) {
        super(120, 16, value);
    }

    public Ufixed120x16(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(120, 16, m, n);
    }
}
