package org.web3j.abi.datatypes;

import java.math.BigInteger;

import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

/**
 * Address type, which is equivalent to uint160.
 */
public class Address extends Uint {
    public static final String TYPE_NAME = "address";
    public static final int LENGTH = 160;

    public Address(BigInteger value) {
        super(TYPE_NAME, LENGTH, value);
    }

    public Address(String hexValue) {
        this(Numeric.toBigInt(hexValue));
    }

    @Override
    public String getTypeAsString() {
        return TYPE_NAME;
    }

    @Override
    public String toString() {
        return Numeric.toHexStringWithPrefixZeroPadded(value, Keys.ADDRESS_LENGTH_IN_HEX);
    }
}
