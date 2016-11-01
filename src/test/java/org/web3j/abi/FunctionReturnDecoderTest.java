package org.web3j.abi;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FunctionReturnDecoderTest {

    @Test
    public void testSimpleFunctionDecode() {
        Function function = new Function<>(
                "test",
                Collections.<Type>emptyList(),
                Collections.singletonList(new TypeReference<Uint>(){})
        );

        assertThat(FunctionReturnDecoder.decode(
                "0x0000000000000000000000000000000000000000000000000000000000000037",
                function.getOutputParameters()),
                equalTo(Collections.singletonList(new Uint(BigInteger.valueOf(55)))));
    }

    @Test
    public void testSimpleFunctionStringResultDecode() {
        Function function = new Function("simple",
                Arrays.asList(),
                Collections.singletonList(new TypeReference<Utf8String>() {
                }));

        List<Utf8String> utf8Strings = FunctionReturnDecoder.decode(

                "0x0000000000000000000000000000000000000000000000000000000000000020" +
                        "000000000000000000000000000000000000000000000000000000000000000d" +
                        "6f6e65206d6f72652074696d6500000000000000000000000000000000000000",
                function.getOutputParameters());

        assertThat(utf8Strings.get(0).getValue(), is("one more time"));
    }

    @Test
    public void testFunctionEmptyStringResultDecode() {
        Function function = new Function("test",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Utf8String>() {
                }));

        List<Utf8String> utf8Strings = FunctionReturnDecoder.decode(
                "0x0000000000000000000000000000000000000000000000000000000000000020" +
                        "0000000000000000000000000000000000000000000000000000000000000000",
                function.getOutputParameters());

        assertThat(utf8Strings.get(0).getValue(), is(""));
    }

    @Test
    public void testMultipleResultFunctionDecode() {
        Function function = new Function<>(
                "test",
                Collections.<Type>emptyList(),
                Arrays.asList(new TypeReference<Uint>() { }, new TypeReference<Uint>() { })
        );

        assertThat(FunctionReturnDecoder.decode(
                "0x0000000000000000000000000000000000000000000000000000000000000037" +
                "0000000000000000000000000000000000000000000000000000000000000007",
                function.getOutputParameters()),
                equalTo(Arrays.asList(new Uint(BigInteger.valueOf(55)),
                        new Uint(BigInteger.valueOf(7)))));
    }

    @Test
    public void testVoidResultFunctionDecode() {
        Function function = new Function<>(
                "test",
                Collections.emptyList(),
                Collections.emptyList());

        assertThat(FunctionReturnDecoder.decode("0x", function.getOutputParameters()),
                is(Collections.emptyList()));
    }

    @Test
    public void testEmptyResultFunctionDecode() {
        Function function = new Function<>(
                "test",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint>() { }));

        assertThat(FunctionReturnDecoder.decode("0x", function.getOutputParameters()),
                is(Collections.emptyList()));
    }

    @Test
    public void testDecodeIndexedUint256Value() {
        Uint256 value = new Uint256(BigInteger.TEN);
        String encoded = TypeEncoder.encodeNumeric(value);

        assertThat(FunctionReturnDecoder.decodeIndexedValue(
                encoded,
                new TypeReference<Uint256>() {}),
                equalTo(value));
    }

    @Test
    public void testDecodeIndexedStringValue() {
        Utf8String string = new Utf8String("some text");
        String encoded = TypeEncoder.encodeString(string);
        String hash = Hash.sha3(encoded);

        assertThat(FunctionReturnDecoder.decodeIndexedValue(
                hash,
                new TypeReference<Utf8String>() {}),
                equalTo(new Bytes32(Numeric.hexStringToByteArray(hash))));
    }

    @Test
    public void testDecodeIndexedDynamicBytesValue() {
        DynamicBytes bytes = new DynamicBytes(new byte[]{ 1, 2, 3, 4, 5});
        String encoded = TypeEncoder.encodeDynamicBytes(bytes);
        String hash = Hash.sha3(encoded);

        assertThat(FunctionReturnDecoder.decodeIndexedValue(
                hash,
                new TypeReference<DynamicBytes>() {}),
                equalTo(new Bytes32(Numeric.hexStringToByteArray(hash))));
    }

    @Test
    public void testDecodeIndexedDynamicArrayValue() {
        DynamicArray<Uint256> array = new DynamicArray<>(new Uint256(BigInteger.TEN));
        String encoded = TypeEncoder.encodeDynamicArray(array);
        String hash = Hash.sha3(encoded);

        assertThat(FunctionReturnDecoder.decodeIndexedValue(
                hash,
                new TypeReference<DynamicArray>() {}),
                equalTo(new Bytes32(Numeric.hexStringToByteArray(hash))));
    }
}
