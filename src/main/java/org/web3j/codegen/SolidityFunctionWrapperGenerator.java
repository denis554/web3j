package org.web3j.codegen;

import javax.lang.model.element.Modifier;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.*;

import org.web3j.abi.Contract;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.AbiDefinition;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static org.web3j.utils.Collection.tail;

/**
 * Java wrapper source code generator for Solidity ABI format.
 */
public class SolidityFunctionWrapperGenerator extends Generator {

    private static final String BINARY = "BINARY";
    private static final String WEB3J = "web3j";
    private static final String CREDENTIALS = "credentials";
    private static final String INITIAL_VALUE = "initialValue";
    private static final String CONTRACT_ADDRESS = "contractAddress";

    private static final String CODEGEN_WARNING = "<p>Auto generated code.<br>\n" +
            "<strong>Do not modifiy!</strong><br>\n" +
            "Please use {@link " + SolidityFunctionWrapperGenerator.class.getName() +
            "} to update.</p>\n";

    private static final String USAGE = "solidity generate " +
            "<input binary file>.bin <input abi file>.abi " +
            "[-p|--package <base package name>] " +
            "-o|--output <destination base directory>";

    private String binaryFileLocation;
    private String absFileLocation;
    private Path destinationDirLocation;
    private String basePackageName;

    private SolidityFunctionWrapperGenerator(
            String binaryFileLocation,
            String absFileLocation,
            String destinationDirLocation,
            String basePackageName) {

        this.binaryFileLocation = binaryFileLocation;
        this.absFileLocation = absFileLocation;
        this.destinationDirLocation = Paths.get(destinationDirLocation);
        this.basePackageName = basePackageName;
    }

    public static void run(String[] args) throws Exception {
        if (args.length < 1 || !args[0].equals("generate")) {
            exitError(USAGE);
        } else {
            main(tail(args));
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 6) {
            exitError(USAGE);
        }

        Optional<String> binaryFileLocation = parsePositionalArg(args, 0);
        Optional<String> absFileLocation = parsePositionalArg(args, 1);
        Optional<String> destinationDirLocation = parseParameterArgument(args, "-o", "--outputDir");
        Optional<String> basePackageName = parseParameterArgument(args, "-p", "--package");

        if (!binaryFileLocation.isPresent()
                || !absFileLocation.isPresent()
                || !destinationDirLocation.isPresent()
                || !basePackageName.isPresent()) {
            exitError(USAGE);
        }

        new SolidityFunctionWrapperGenerator(
                binaryFileLocation.get(),
                absFileLocation.get(),
                destinationDirLocation.get(),
                basePackageName.get())
        .generate();
    }

    private static Optional<String> parsePositionalArg(String[] args, int idx) {
        if (args != null && args.length > idx) {
            return Optional.of(args[idx]);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<String> parseParameterArgument(String[] args, String... parameters) {
        for (String parameter:parameters) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals(parameter)
                        && i + 1 < args.length) {
                    String parameterValue = args[i+1];
                    if (!parameterValue.startsWith("-")) {
                        return Optional.of(parameterValue);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static void exitError(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private void generate() throws IOException, ClassNotFoundException {

        File binaryFile = new File(binaryFileLocation);
        if (!binaryFile.exists()) {
            exitError("Invalid input binary file specified");
        }

        byte[] bytes = Files.readAllBytes(Paths.get(binaryFile.toURI()));
        String binary = new String(bytes);

        File absFile = new File(absFileLocation);
        if (!absFile.exists() || !absFile.canRead()) {
            exitError("Invalid input ABI file specified");
        }
        String fileName = absFile.getName();
        String contractName = getFileNameNoExtension(fileName);

        List<AbiDefinition> functionDefinitions = loadContractDefinition(absFile);

        if (functionDefinitions.isEmpty()) {
            exitError("Unable to parse input ABI file");
        } else {
            generateSolidityWrappers(binary, contractName, functionDefinitions, basePackageName);
        }
    }

    private static List<AbiDefinition> loadContractDefinition(File absFile)
            throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        AbiDefinition[] abiDefinition = objectMapper.readValue(absFile, AbiDefinition[].class);
        return Arrays.asList(abiDefinition);
    }

    static String getFileNameNoExtension(String fileName) {
        String[] splitName = fileName.split("\\.(?=[^\\.]*$)");
        return splitName[0];
    }

    private void generateSolidityWrappers(
            String binary, String contractName, List<AbiDefinition> functionDefinitions,
            String basePackageName) throws IOException, ClassNotFoundException {

        String className = contractName.substring(0, 1).toUpperCase() + contractName.substring(1);

        TypeSpec.Builder classBuilder = createClassBuilder(className, binary);
        classBuilder.addMethod(buildConstructor());
        classBuilder.addMethods(buildFunctionDefinitions(className, functionDefinitions));
        classBuilder.addMethod(buildLoad(className));

        System.out.printf("Generating " + basePackageName + "." + className + " ... ");
        JavaFile javaFile = JavaFile.builder(basePackageName, classBuilder.build())
                .indent("    ")  // default indentation is two spaces
                .build();
        System.out.println("complete");

        javaFile.writeTo(destinationDirLocation);
        System.out.println("File written to " + destinationDirLocation.toString() + "\n");
    }

    private TypeSpec.Builder createClassBuilder(String className, String binary) {
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc(CODEGEN_WARNING)
                .superclass(Contract.class)
                .addField(createBinaryDefinition(binary));
    }

    private FieldSpec createBinaryDefinition(String binary) {
        return FieldSpec.builder(String.class, BINARY)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", binary)
                .build();
    }

    private static List<MethodSpec> buildFunctionDefinitions(
            String className,
            List<AbiDefinition> functionDefinitions) throws ClassNotFoundException {

        List<MethodSpec> methodSpecs = new ArrayList<>();
        boolean constructor = false;

        for (AbiDefinition functionDefinition:functionDefinitions) {
            switch (functionDefinition.getType()) {
                case "function":
                    methodSpecs.add(buildFunction(functionDefinition));
                    break;
                case "event":
                    methodSpecs.add(buildEventFunction(functionDefinition));
                    break;
                case "constructor":
                    constructor = true;
                    methodSpecs.add(buildDeploy(className, functionDefinition));
                    break;
            }
        }

        // constructor will not be specified in ABI file if its empty
        if (!constructor) {
            methodSpecs.add(buildDeploy(className));
        }

        return methodSpecs;
    }

    private static MethodSpec buildConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(String.class, CONTRACT_ADDRESS)
                .addParameter(Web3j.class, WEB3J)
                .addParameter(Credentials.class, CREDENTIALS)
                .addStatement("super($N, $N, $N)", CONTRACT_ADDRESS, WEB3J, CREDENTIALS)
                .build();
    }

    private static MethodSpec buildDeploy(
            String className, AbiDefinition functionDefinition) {

        MethodSpec.Builder methodBuilder = getDeployMethodSpec(className);
        String inputParams = addParameters(methodBuilder, functionDefinition.getInputs());

        if (!inputParams.isEmpty()) {
            methodBuilder.addStatement("$T encodedConstructor = $T.encodeConstructor(" +
                            "$T.asList($L)" +
                            ")",
                    String.class, FunctionEncoder.class, Arrays.class, inputParams);
            methodBuilder.addStatement("return deployAsync($L.class, $L, $L, $L, encodedConstructor, $L)",
                    className, WEB3J, CREDENTIALS, BINARY, INITIAL_VALUE);
        } else {
            methodBuilder.addStatement("return deployAsync($L.class, $L, $L, $L, \"\", $L)",
                    className, WEB3J, CREDENTIALS, BINARY, INITIAL_VALUE);
        }
        return methodBuilder.build();
    }

    private static MethodSpec buildDeploy(String className) {
        MethodSpec.Builder methodBuilder = getDeployMethodSpec(className);
        methodBuilder.addStatement("return deployAsync($L.class, $L, $L, $L, \"\", $L)",
                className, WEB3J, CREDENTIALS, BINARY, INITIAL_VALUE);

        return methodBuilder.build();
    }

    private static MethodSpec.Builder getDeployMethodSpec(String className) {
        return MethodSpec.methodBuilder("deploy")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(Future.class), TypeVariableName.get(className, Type.class)))
                .addParameter(Web3j.class, WEB3J)
                .addParameter(Credentials.class, CREDENTIALS)
                .addParameter(BigInteger.class, INITIAL_VALUE);
    }

    private static MethodSpec buildLoad(String className) {
        return MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeVariableName.get(className, Type.class))
                .addParameter(String.class, CONTRACT_ADDRESS)
                .addParameter(Web3j.class, WEB3J)
                .addParameter(Credentials.class, CREDENTIALS)
                .addStatement("return new $L($L, $L, $L)",
                        className, CONTRACT_ADDRESS, WEB3J, CREDENTIALS)
                .build();
    }

    static String addParameters(
            MethodSpec.Builder methodBuilder, List<AbiDefinition.NamedType> namedTypes) {

        List<ParameterSpec> inputParameterTypes = buildParameterTypes(namedTypes);
        methodBuilder.addParameters(inputParameterTypes);

        return inputParameterTypes.stream()
                .map(p -> p.name)
                .collect(Collectors.joining(", "));
    }

    static List<ParameterSpec> buildParameterTypes(List<AbiDefinition.NamedType> namedTypes) {
        List<ParameterSpec> result = new ArrayList<>(namedTypes.size());
        for (int i = 0; i < namedTypes.size(); i++) {
            AbiDefinition.NamedType namedType = namedTypes.get(i);

            String name = createValidParamName(namedType.getName(), i);
            String type = namedTypes.get(i).getType();

            result.add(ParameterSpec.builder(buildTypeName(type), name).build());
        }
        return result;
    }

    /**
     * Public Solidity arrays and maps require an unnamed input parameter - multiple if they
     * require a struct type
     *
     * @param name
     * @param idx
     * @return non-empty parameter name
     */
    static String createValidParamName(String name, int idx) {
        if (name.equals("")) {
            return "param" + idx;
        } else {
            return name;
        }
    }

    static List<TypeName> buildTypeNames(List<AbiDefinition.NamedType> namedTypes) {
        List<TypeName> result = new ArrayList<>(namedTypes.size());
        for (AbiDefinition.NamedType namedType : namedTypes) {
            result.add(buildTypeName(namedType.getType()));
        }
        return result;
    }

    private static MethodSpec buildFunction(
            AbiDefinition functionDefinition) throws ClassNotFoundException {
        String functionName = functionDefinition.getName();

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(functionName)
                        .addModifiers(Modifier.PUBLIC);

        String inputParams = addParameters(methodBuilder, functionDefinition.getInputs());

        List<TypeName> outputParameterTypes = buildTypeNames(functionDefinition.getOutputs());
        if (functionDefinition.isConstant()) {
            methodBuilder = buildConstantFunction(
                    functionDefinition, methodBuilder, outputParameterTypes, inputParams);
        } else {
            methodBuilder = buildTransactionFunction(
                    functionDefinition, methodBuilder, inputParams);
        }

        return methodBuilder.build();
    }

    private static MethodSpec.Builder  buildConstantFunction(
            AbiDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            List<TypeName> outputParameterTypes,
            String inputParams) throws ClassNotFoundException {

        String functionName = functionDefinition.getName();
        TypeVariableName typeVariableName = TypeVariableName.get("T", Type.class);

        if (outputParameterTypes.isEmpty()) {
            throw new RuntimeException("Only transactional methods should have void return types");
        } else if (outputParameterTypes.size() == 1) {
            methodBuilder.returns(ParameterizedTypeName.get(
                    ClassName.get(Future.class), outputParameterTypes.get(0)));

            methodBuilder.addStatement("$T function = " +
                            "new $T<>($S, \n$T.asList($L), \n$T.asList(new $T<$T>() {}))",
                    Function.class, Function.class, functionName,
                    Arrays.class, inputParams, Arrays.class, TypeReference.class,
                    outputParameterTypes.get(0));
            methodBuilder.addStatement("return executeCallSingleValueReturnAsync(function)");

        } else {
            methodBuilder.addTypeVariable(typeVariableName);
            methodBuilder.returns(ParameterizedTypeName.get(
                            ClassName.get(Future.class),
                            ParameterizedTypeName.get(
                                    ClassName.get(List.class), typeVariableName)));

            buildVariableLengthReturnFunctionConstructor(
                    methodBuilder, functionName, inputParams, outputParameterTypes);

            methodBuilder.addStatement("return executeCallMultipleValueReturnAsync(function)");
        }

        return methodBuilder;
    }

    private static MethodSpec.Builder buildTransactionFunction(
            AbiDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            String inputParams) throws ClassNotFoundException {

        String functionName = functionDefinition.getName();

        methodBuilder.returns(ParameterizedTypeName.get(Future.class, TransactionReceipt.class));

        methodBuilder.addStatement("$T function = new $T<>($S, $T.asList($L), $T.emptyList())",
                Function.class, Function.class, functionName,
                Arrays.class, inputParams, Collections.class);
        methodBuilder.addStatement("return executeTransactionAsync(function)");

        return methodBuilder;
    }

    private static MethodSpec buildEventFunction(
            AbiDefinition functionDefinition) throws ClassNotFoundException {

        String functionName = functionDefinition.getName();
        String generatedFunctionName = "process" +
                functionName.substring(0, 1).toUpperCase() +
                functionName.substring(1) + "Event";

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(generatedFunctionName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TransactionReceipt.class, "transactionReceipt")
                .returns(EventValues.class);

        List<AbiDefinition.NamedType> inputs = functionDefinition.getInputs();

        List<TypeName> indexedParameters = new ArrayList<>();
        List<TypeName> nonIndexedParameters = new ArrayList<>();

        inputs.forEach(namedType -> {
            if (namedType.isIndexed()) {
                indexedParameters.add(buildTypeName(namedType.getType()));
            } else {
                nonIndexedParameters.add(buildTypeName(namedType.getType()));
            }
        });

        buildVariableLengthEventConstructor(
                methodBuilder, functionName, indexedParameters, nonIndexedParameters);

        return methodBuilder
                .addStatement("return extractEventParameters(event, transactionReceipt)")
                .build();
    }

    static TypeName buildTypeName(String type) {
        if (type.endsWith("]")) {
            String[] splitType = type.split("\\[");
            Class<?> baseType = AbiTypes.getType(splitType[0]);

            TypeName typeName;
            if (splitType[1].length() == 1) {
                typeName = ParameterizedTypeName.get(DynamicArray.class, baseType);
            } else {
                // Unfortunately we can't encode it's length as a type
                typeName = ParameterizedTypeName.get(StaticArray.class, baseType);
            }
            return typeName;
        } else {
            Class<?> cls = AbiTypes.getType(type);
            return ClassName.get(cls);
        }
    }

    private static void buildVariableLengthReturnFunctionConstructor(
            MethodSpec.Builder methodBuilder, String functionName, String inputParameters,
            List<TypeName> outputParameterTypes) throws ClassNotFoundException {

        List<Object> objects = new ArrayList<>();
        objects.add(Function.class);
        objects.add(Function.class);
        objects.add(functionName);

        objects.add(Arrays.class);
        objects.add(inputParameters);

        objects.add(Arrays.class);
        for (TypeName outputParameterType: outputParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(outputParameterType);
        }

        String asListParams = outputParameterTypes.stream().map(p -> "new $T<$T>() {}")
                .collect(Collectors.joining(", "));

        methodBuilder.addStatement("$T function = new $T<>($S, \n$T.asList($L), \n$T.asList(" +
                        asListParams + "))", objects.toArray());
    }

    private static void buildVariableLengthEventConstructor(
            MethodSpec.Builder methodBuilder, String eventName, List<TypeName> indexedParameterTypes,
            List<TypeName> nonIndexedParameterTypes) throws ClassNotFoundException {

        List<Object> objects = new ArrayList<>();
        objects.add(Event.class);
        objects.add(Event.class);
        objects.add(eventName);

        objects.add(Arrays.class);
        for (TypeName indexedParameterType: indexedParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(indexedParameterType);
        }

        objects.add(Arrays.class);
        for (TypeName indexedParameterType: nonIndexedParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(indexedParameterType);
        }

        String indexedAsListParams = indexedParameterTypes.stream().map(p -> "new $T<$T>() {}")
                .collect(Collectors.joining(", "));
        String nonIndexedAsListParams = nonIndexedParameterTypes.stream().map(p -> "new $T<$T>() {}")
                .collect(Collectors.joining(", "));

        methodBuilder.addStatement("$T event = new $T($S, \n" +
                "$T.asList(" + indexedAsListParams + "),\n" +
                "$T.asList(" + nonIndexedAsListParams + "))", objects.toArray());
    }
}
