package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Ufixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Ufixed56x200 extends Ufixed {
    public static final Ufixed56x200 DEFAULT = new Ufixed56x200(BigInteger.ZERO);

    public Ufixed56x200(BigInteger value) {
        super(56, 200, value);
    }

    public Ufixed56x200(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(56, 200, m, n);
    }
}
