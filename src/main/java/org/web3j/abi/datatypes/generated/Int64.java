package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;

import org.web3j.abi.datatypes.Int;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modifiy!</strong><br>
 * Please use {@link org.web3j.codegen.AbiTypesGenerator} to update.</p>
 */
public class Int64 extends Int {
  public static final Int64 DEFAULT = new Int64(BigInteger.ZERO);

  public Int64(BigInteger value) {
    super(64, value);
  }
}
