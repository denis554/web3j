package org.web3j.abi;

import java.math.BigInteger;
import java.util.List;

import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Hash;
import org.web3j.crypto.HexUtils;

/**
 * <p>Ethereum Contract Application Binary Interface (ABI) encoding for functions.
 * Further details are available
 * <a href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 * </p>
 */
public class FunctionEncoder {

    private FunctionEncoder() { }

    public static String encode(Function function) {
        List<Type> parameters = function.getParameters();
        String expectedSignature = function.getFullSignature();

        String methodSignature = buildMethodSignature(function.getName(), parameters);
        if (!isValidSignature(expectedSignature, methodSignature)) {
            throw new UnsupportedOperationException(
                    "Method signature: " + methodSignature +
                            " does not match expected: " + expectedSignature);
        }
        String methodId = buildMethodId(methodSignature);

        StringBuilder result = new StringBuilder();
        result.append(methodId);

        int dynamicDataOffset = getLength(parameters) * 32;
        StringBuilder dynamicData = new StringBuilder();

        for (Type parameter:parameters) {
            String encodedValue = TypeEncoder.encode(parameter);

            if (TypeEncoder.isDynamic(parameter)) {
                String encodedDataOffset = TypeEncoder.encodeNumeric(
                        new Uint(BigInteger.valueOf(dynamicDataOffset)));
                result.append(encodedDataOffset);
                dynamicData.append(encodedValue);
                dynamicDataOffset += encodedValue.length() >> 1;
            } else {
                result.append(encodedValue);
            }
        }
        result.append(dynamicData);

        return result.toString();
    }

    private static int getLength(List<Type> parameters) {
        int count = 0;
        for (Type type:parameters) {
            if (type instanceof StaticArray) {
                count += ((StaticArray) type).getValue().size();
            } else {
                count++;
            }
        }
        return count;
    }


    static String buildMethodSignature(String methodName, List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");
        String params = parameters.stream()
                .map(Type::getTypeAsString)
                .reduce((acc, s) -> acc + "," + s)
                .orElse("");
        result.append(params);
        result.append(")");
        return result.toString();
    }

    private static boolean isValidSignature(String signature, String methodSignature) {
        return signature.equals(methodSignature);
    }

    static String buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = Hash.sha3(input);
        return HexUtils.toHexString(hash).substring(0, 10);
    }
}
