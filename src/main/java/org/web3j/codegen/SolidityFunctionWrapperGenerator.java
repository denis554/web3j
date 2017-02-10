package org.web3j.codegen;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import javax.lang.model.element.Modifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import rx.functions.Func1;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.AbiDefinition;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Collection;
import org.web3j.utils.Files;
import org.web3j.utils.Strings;
import org.web3j.utils.Version;

import static org.web3j.utils.Collection.tail;

/**
 * Java wrapper source code generator for Solidity ABI format.
 */
public class SolidityFunctionWrapperGenerator {

    private static final String BINARY = "BINARY";
    private static final String WEB3J = "web3j";
    private static final String CREDENTIALS = "credentials";
    private static final String TRANSACTION_MANAGER = "transactionManager";
    private static final String INITIAL_VALUE = "initialValue";
    private static final String CONTRACT_ADDRESS = "contractAddress";
    private static final String GAS_PRICE = "gasPrice";
    private static final String GAS_LIMIT = "gasLimit";

    private static final String CODEGEN_WARNING = "<p>Auto generated code.<br>\n" +
            "<strong>Do not modify!</strong><br>\n" +
            "Please use {@link " + SolidityFunctionWrapperGenerator.class.getName() +
            "} to update.\n";

    private static final String USAGE = "solidity generate " +
            "<input binary file>.bin <input abi file>.abi " +
            "[-p|--package <base package name>] " +
            "-o|--output <destination base directory>";

    private String binaryFileLocation;
    private String absFileLocation;
    private File destinationDirLocation;
    private String basePackageName;

    private SolidityFunctionWrapperGenerator(
            String binaryFileLocation,
            String absFileLocation,
            String destinationDirLocation,
            String basePackageName) {

        this.binaryFileLocation = binaryFileLocation;
        this.absFileLocation = absFileLocation;
        this.destinationDirLocation = new File(destinationDirLocation);
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

        String binaryFileLocation = parsePositionalArg(args, 0);
        String absFileLocation = parsePositionalArg(args, 1);
        String destinationDirLocation = parseParameterArgument(args, "-o", "--outputDir");
        String basePackageName = parseParameterArgument(args, "-p", "--package");

        if (binaryFileLocation.equals("")
                || absFileLocation.equals("")
                || destinationDirLocation.equals("")
                || basePackageName.equals("")) {
            exitError(USAGE);
        }

        new SolidityFunctionWrapperGenerator(
                binaryFileLocation,
                absFileLocation,
                destinationDirLocation,
                basePackageName)
                .generate();
    }

    private static String parsePositionalArg(String[] args, int idx) {
        if (args != null && args.length > idx) {
            return args[idx];
        } else {
            return "";
        }
    }

    private static String parseParameterArgument(String[] args, String... parameters) {
        for (String parameter : parameters) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals(parameter)
                        && i + 1 < args.length) {
                    String parameterValue = args[i + 1];
                    if (!parameterValue.startsWith("-")) {
                        return parameterValue;
                    }
                }
            }
        }
        return "";
    }

    private static void exitError(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private void generate() throws IOException, ClassNotFoundException {

        File binaryFile = new File(binaryFileLocation);
        if (!binaryFile.exists()) {
            exitError("Invalid input binary file specified: " + binaryFileLocation);
        }

        byte[] bytes = Files.readBytes(new File(binaryFile.toURI()));
        String binary = new String(bytes);

        File absFile = new File(absFileLocation);
        if (!absFile.exists() || !absFile.canRead()) {
            exitError("Invalid input ABI file specified: " + absFileLocation);
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
        String[] splitName = fileName.split("\\.(?=[^.]*$)");
        return splitName[0];
    }

    private void generateSolidityWrappers(
            String binary, String contractName, List<AbiDefinition> functionDefinitions,
            String basePackageName) throws IOException, ClassNotFoundException {

        String className = Strings.capitaliseFirstLetter(contractName);

        TypeSpec.Builder classBuilder = createClassBuilder(className, binary);
        classBuilder.addMethod(buildConstructor(Credentials.class, CREDENTIALS));
        classBuilder.addMethod(buildConstructor(
                TransactionManager.class, TRANSACTION_MANAGER));

        classBuilder.addMethods(buildFunctionDefinitions(className, classBuilder,
                functionDefinitions));

        classBuilder.addMethod(buildLoad(className, Credentials.class, CREDENTIALS));
        classBuilder.addMethod(buildLoad(className, TransactionManager.class, TRANSACTION_MANAGER));

        System.out.printf("Generating " + basePackageName + "." + className + " ... ");
        JavaFile javaFile = JavaFile.builder(basePackageName, classBuilder.build())
                .indent("    ")  // default indentation is two spaces
                .build();
        System.out.println("complete");

        javaFile.writeTo(destinationDirLocation);
        System.out.println("File written to " + destinationDirLocation.toString() + "\n");
    }

    private TypeSpec.Builder createClassBuilder(String className, String binary) {

        String javadoc = CODEGEN_WARNING + getWeb3jVersion();

        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc(javadoc)
                .superclass(Contract.class)
                .addField(createBinaryDefinition(binary));
    }

    private static String getWeb3jVersion() {
        String version;

        try {
            // This only works if run as part of the web3j command line tools which contains
            // a version.properties file
            version = Version.getVersion();
        } catch (IOException | NullPointerException e) {
            version = Version.DEFAULT;
        }
        return "\n<p>Generated with web3j version " + version + ".\n";
    }

    private FieldSpec createBinaryDefinition(String binary) {
        return FieldSpec.builder(String.class, BINARY)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", binary)
                .build();
    }

    private static List<MethodSpec> buildFunctionDefinitions(
            String className,
            TypeSpec.Builder classBuilder,
            List<AbiDefinition> functionDefinitions) throws ClassNotFoundException {

        List<MethodSpec> methodSpecs = new ArrayList<MethodSpec>();
        boolean constructor = false;

        for (AbiDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.getType().equals("function")) {
                methodSpecs.add(buildFunction(functionDefinition));

            } else if (functionDefinition.getType().equals("event")) {
                buildEventFunctions(functionDefinition, classBuilder);

            } else if (functionDefinition.getType().equals("constructor")) {
                constructor = true;
                methodSpecs.add(buildDeploy(
                        className, functionDefinition, Credentials.class, CREDENTIALS));
                methodSpecs.add(buildDeploy(
                        className, functionDefinition, TransactionManager.class,
                        TRANSACTION_MANAGER));
            }
        }

        // constructor will not be specified in ABI file if its empty
        if (!constructor) {
            MethodSpec.Builder credentialsMethodBuilder =
                    getDeployMethodSpec(className, Credentials.class, CREDENTIALS);
            methodSpecs.add(buildDeployAsyncNoParams(
                    credentialsMethodBuilder, className, CREDENTIALS));

            MethodSpec.Builder transactionManagerMethodBuilder =
                    getDeployMethodSpec(className, TransactionManager.class, TRANSACTION_MANAGER);
            methodSpecs.add(buildDeployAsyncNoParams(
                    transactionManagerMethodBuilder, className, TRANSACTION_MANAGER));
        }

        return methodSpecs;
    }

    private static MethodSpec buildConstructor(Class authType, String authName) {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(String.class, CONTRACT_ADDRESS)
                .addParameter(Web3j.class, WEB3J)
                .addParameter(authType, authName)
                .addParameter(BigInteger.class, GAS_PRICE)
                .addParameter(BigInteger.class, GAS_LIMIT)
                .addStatement("super($N, $N, $N, $N, $N)",
                        CONTRACT_ADDRESS, WEB3J, authName, GAS_PRICE, GAS_LIMIT)
                .build();
    }

    private static MethodSpec buildDeploy(
            String className, AbiDefinition functionDefinition,
            Class authType, String authName) {

        MethodSpec.Builder methodBuilder = getDeployMethodSpec(className, authType, authName);
        String inputParams = addParameters(methodBuilder, functionDefinition.getInputs());

        if (!inputParams.isEmpty()) {
            return buildDeployAsyncWithParams(methodBuilder, className, inputParams, authName);
        } else {
            return buildDeployAsyncNoParams(methodBuilder, className, authName);
        }
    }

    private static MethodSpec buildDeployAsyncWithParams(
            MethodSpec.Builder methodBuilder, String className, String inputParams,
            String authName) {

        methodBuilder.addStatement("$T encodedConstructor = $T.encodeConstructor(" +
                        "$T.<$T>asList($L)" +
                        ")",
                String.class, FunctionEncoder.class, Arrays.class, Type.class, inputParams);
        methodBuilder.addStatement(
                "return deployAsync($L.class, $L, $L, $L, $L, $L, encodedConstructor, $L)",
                className, WEB3J, authName, GAS_PRICE, GAS_LIMIT, BINARY, INITIAL_VALUE);
        return methodBuilder.build();
    }

    private static MethodSpec buildDeployAsyncNoParams(
            MethodSpec.Builder methodBuilder, String className,
            String authName) {
        methodBuilder.addStatement("return deployAsync($L.class, $L, $L, $L, $L, $L, \"\", $L)",
                className, WEB3J, authName, GAS_PRICE, GAS_LIMIT, BINARY, INITIAL_VALUE);
        return methodBuilder.build();
    }

    private static MethodSpec.Builder getDeployMethodSpec(
            String className, Class authType, String authName) {
        return MethodSpec.methodBuilder("deploy")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(Future.class), TypeVariableName.get(className, Type.class)))
                .addParameter(Web3j.class, WEB3J)
                .addParameter(authType, authName)
                .addParameter(BigInteger.class, GAS_PRICE)
                .addParameter(BigInteger.class, GAS_LIMIT)
                .addParameter(BigInteger.class, INITIAL_VALUE);
    }

    private static MethodSpec buildLoad(
            String className, Class authType, String authName) {
        return MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeVariableName.get(className, Type.class))
                .addParameter(String.class, CONTRACT_ADDRESS)
                .addParameter(Web3j.class, WEB3J)
                .addParameter(authType, authName)
                .addParameter(BigInteger.class, GAS_PRICE)
                .addParameter(BigInteger.class, GAS_LIMIT)
                .addStatement("return new $L($L, $L, $L, $L, $L)", className,
                        CONTRACT_ADDRESS, WEB3J, authName, GAS_PRICE, GAS_LIMIT)
                .build();
    }

    static String addParameters(
            MethodSpec.Builder methodBuilder, List<AbiDefinition.NamedType> namedTypes) {

        List<ParameterSpec> inputParameterTypes = buildParameterTypes(namedTypes);
        methodBuilder.addParameters(inputParameterTypes);

        return org.web3j.utils.Collection.join(
                inputParameterTypes,
                ", ",
                new Collection.Function<ParameterSpec, String>() {
                    @Override
                    public String apply(ParameterSpec parameterSpec) {
                        return parameterSpec.name;
                    }
                });
    }

    static List<ParameterSpec> buildParameterTypes(List<AbiDefinition.NamedType> namedTypes) {
        List<ParameterSpec> result = new ArrayList<ParameterSpec>(namedTypes.size());
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
        List<TypeName> result = new ArrayList<TypeName>(namedTypes.size());
        for (AbiDefinition.NamedType namedType : namedTypes) {
            result.add(buildTypeName(namedType.getType()));
        }
        return result;
    }

    static MethodSpec buildFunction(
            AbiDefinition functionDefinition) throws ClassNotFoundException {
        String functionName = functionDefinition.getName();

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(functionName)
                        .addModifiers(Modifier.PUBLIC);

        String inputParams = addParameters(methodBuilder, functionDefinition.getInputs());

        List<TypeName> outputParameterTypes = buildTypeNames(functionDefinition.getOutputs());
        if (functionDefinition.isConstant()) {
            buildConstantFunction(
                    functionDefinition, methodBuilder, outputParameterTypes, inputParams);
        } else {
            buildTransactionFunction(
                    functionDefinition, methodBuilder, inputParams);
        }

        return methodBuilder.build();
    }

    private static void buildConstantFunction(
            AbiDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            List<TypeName> outputParameterTypes,
            String inputParams) throws ClassNotFoundException {

        String functionName = functionDefinition.getName();

        if (outputParameterTypes.isEmpty()) {
            throw new RuntimeException("Only transactional methods should have void return types");
        } else if (outputParameterTypes.size() == 1) {
            methodBuilder.returns(ParameterizedTypeName.get(
                    ClassName.get(Future.class), outputParameterTypes.get(0)));

            TypeName typeName = outputParameterTypes.get(0);
            methodBuilder.addStatement("$T function = " +
                            "new $T($S, \n$T.<$T>asList($L), \n$T.<$T<?>>asList(new $T<$T>() {}))",
                    Function.class, Function.class, functionName,
                    Arrays.class, Type.class, inputParams,
                    Arrays.class, TypeReference.class,
                    TypeReference.class, typeName);
            methodBuilder.addStatement("return executeCallSingleValueReturnAsync(function)");

        } else {
            methodBuilder.returns(ParameterizedTypeName.get(
                    ClassName.get(Future.class),
                    ParameterizedTypeName.get(
                            ClassName.get(List.class), ClassName.get(Type.class))));

            buildVariableLengthReturnFunctionConstructor(
                    methodBuilder, functionName, inputParams, outputParameterTypes);

            methodBuilder.addStatement("return executeCallMultipleValueReturnAsync(function)");
        }
    }

    private static void buildTransactionFunction(
            AbiDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            String inputParams) throws ClassNotFoundException {

        String functionName = functionDefinition.getName();

        methodBuilder.returns(ParameterizedTypeName.get(Future.class, TransactionReceipt.class));

        methodBuilder.addStatement("$T function = new $T($S, $T.<$T>asList($L), $T" +
                        ".<$T<?>>emptyList())",
                Function.class, Function.class, functionName,
                Arrays.class, Type.class, inputParams, Collections.class,
                TypeReference.class);
        methodBuilder.addStatement("return executeTransactionAsync(function)");
    }

    static TypeSpec buildEventResponseObject(String className,
                                             List<NamedTypeName> indexedParameters,
                                             List<NamedTypeName> nonIndexedParameters) {

        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        for (NamedTypeName namedType : indexedParameters) {
            builder.addField(namedType.getTypeName(), namedType.getName(), Modifier.PUBLIC);
        }

        for (NamedTypeName namedType : nonIndexedParameters) {
            builder.addField(namedType.getTypeName(), namedType.getName(), Modifier.PUBLIC);
        }

        return builder.build();
    }

    static MethodSpec buildEventObservableFunction(String responseClassName,
                                                   String functionName,
                                                   List<NamedTypeName> indexedParameters,
                                                   List<NamedTypeName> nonIndexedParameters)
            throws ClassNotFoundException {

        String generatedFunctionName = functionName + "EventObservable";
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(rx
                .Observable.class), ClassName.get("", responseClassName));

        MethodSpec.Builder observableMethodBuilder = MethodSpec.methodBuilder(generatedFunctionName)
                .addModifiers(Modifier.PUBLIC)
                .returns(parameterizedTypeName);


        buildVariableLengthEventConstructor(
                observableMethodBuilder, functionName, indexedParameters, nonIndexedParameters);


        TypeSpec converter = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(Func1.class),
                        ClassName.get(Log.class),
                        ClassName.get("", responseClassName)))
                .addMethod(MethodSpec.methodBuilder("call")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Log.class, "log")
                        .returns(ClassName.get("", responseClassName))
                        .addStatement("$T eventValues = extractEventParameters(event, log)",
                                EventValues.class)
                        .addStatement("$1T typedResponse = new $1T()",
                                ClassName.get("", responseClassName))
                        .addCode(buildTypedResponse("typedResponse", indexedParameters,
                                nonIndexedParameters))
                        .addStatement("return typedResponse")
                        .build())
                .build();

        observableMethodBuilder.addStatement("$1T filter = new $1T($2T.EARLIEST,$2T.LATEST, " +
                "getContractAddress())", EthFilter.class, DefaultBlockParameterName.class)
                .addStatement("filter.addSingleTopic($T.encode(event))", EventEncoder.class)
                .addStatement("return web3j.ethLogObservable(filter).map($L)", converter);

        return observableMethodBuilder
                .build();
    }

    static MethodSpec buildEventTransactionReceiptFunction(String responseClassName, String
            functionName, List<NamedTypeName> indexedParameters, List<NamedTypeName>
                                                                   nonIndexedParameters) throws
            ClassNotFoundException {
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get
                (List.class), ClassName.get("", responseClassName));

        String generatedFunctionName = "get" + Strings.capitaliseFirstLetter(functionName) +
                "Events";
        MethodSpec.Builder transactionMethodBuilder = MethodSpec.methodBuilder
                (generatedFunctionName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TransactionReceipt.class, "transactionReceipt")
                .returns(parameterizedTypeName);

        buildVariableLengthEventConstructor(
                transactionMethodBuilder, functionName, indexedParameters, nonIndexedParameters);

        transactionMethodBuilder.addStatement("$T valueList = extractEventParameters(event," +
                "transactionReceipt)", ParameterizedTypeName.get(List.class, EventValues.class))
                .addStatement("$1T responses = new $1T(valueList.size())",
                        ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                                ClassName.get("", responseClassName)))
                .beginControlFlow("for($T eventValues : valueList)", EventValues.class)
                .addStatement("$1T typedResponse = new $1T()",
                        ClassName.get("", responseClassName))
                .addCode(buildTypedResponse("typedResponse", indexedParameters,
                        nonIndexedParameters))
                .addStatement("responses.add(typedResponse)")
                .endControlFlow();


        transactionMethodBuilder.addStatement("return responses");
        return transactionMethodBuilder.build();
    }

    static void buildEventFunctions(
            AbiDefinition functionDefinition,
            TypeSpec.Builder classBuilder) throws ClassNotFoundException {

        String functionName = Strings.lowercaseFirstLetter(functionDefinition.getName());
        List<AbiDefinition.NamedType> inputs = functionDefinition.getInputs();
        String responseClassName = Strings.capitaliseFirstLetter(functionName) + "EventResponse";

        List<NamedTypeName> indexedParameters = new ArrayList<>();
        List<NamedTypeName> nonIndexedParameters = new ArrayList<>();
        for (AbiDefinition.NamedType namedType : inputs) {

            if (namedType.isIndexed()) {
                indexedParameters.add(new NamedTypeName(namedType.getName(), buildTypeName
                        (namedType.getType())));
            } else {
                nonIndexedParameters.add(new NamedTypeName(namedType.getName(), buildTypeName
                        (namedType.getType())));
            }
        }

        classBuilder.addType(buildEventResponseObject(responseClassName, indexedParameters,
                nonIndexedParameters));

        classBuilder.addMethod(buildEventTransactionReceiptFunction(responseClassName,
                functionName, indexedParameters, nonIndexedParameters));
        classBuilder.addMethod(buildEventObservableFunction(responseClassName, functionName,
                indexedParameters, nonIndexedParameters));
    }

    static CodeBlock buildTypedResponse(String objectName,
                                        List<NamedTypeName> indexedParameters,
                                        List<NamedTypeName> nonIndexedParameters) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (int i = 0; i < indexedParameters.size(); i++) {
            builder.addStatement("$L.$L = ($T)eventValues.getIndexedValues().get($L)",
                    objectName,
                    indexedParameters.get(i).getName(),
                    indexedParameters.get(i).getTypeName(),
                    i);
        }

        for (int i = 0; i < nonIndexedParameters.size(); i++) {
            builder.addStatement("$L.$L = ($T)eventValues.getNonIndexedValues().get($L)",
                    objectName,
                    nonIndexedParameters.get(i).getName(),
                    nonIndexedParameters.get(i).getTypeName(),
                    i);
        }
        return builder.build();
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

        List<Object> objects = new ArrayList<Object>();
        objects.add(Function.class);
        objects.add(Function.class);
        objects.add(functionName);

        objects.add(Arrays.class);
        objects.add(Type.class);
        objects.add(inputParameters);

        objects.add(Arrays.class);
        objects.add(TypeReference.class);
        for (TypeName outputParameterType : outputParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(outputParameterType);
        }

        String asListParams = Collection.join(
                outputParameterTypes,
                ", ",
                new Collection.Function<TypeName, String>() {
                    @Override
                    public String apply(TypeName typeName) {
                        return "new $T<$T>() {}";
                    }
                });

        methodBuilder.addStatement("$T function = new $T($S, \n$T.<$T>asList($L), \n$T" +
                ".<$T<?>>asList(" +
                asListParams + "))", objects.toArray());
    }

    private static void buildVariableLengthEventConstructor(
            MethodSpec.Builder methodBuilder, String eventName, List<NamedTypeName>
            indexedParameterTypes,
            List<NamedTypeName> nonIndexedParameterTypes) throws ClassNotFoundException {

        List<Object> objects = new ArrayList<Object>();
        objects.add(Event.class);
        objects.add(Event.class);
        objects.add(eventName);

        objects.add(Arrays.class);
        objects.add(TypeReference.class);
        for (NamedTypeName indexedParameterType : indexedParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(indexedParameterType.getTypeName());
        }

        objects.add(Arrays.class);
        objects.add(TypeReference.class);
        for (NamedTypeName indexedParameterType : nonIndexedParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(indexedParameterType.getTypeName());
        }

        String indexedAsListParams = Collection.join(
                indexedParameterTypes,
                ", ",
                new Collection.Function<NamedTypeName, String>() {
                    @Override
                    public String apply(NamedTypeName typeName) {
                        return "new $T<$T>() {}";
                    }
                });

        String nonIndexedAsListParams = Collection.join(
                nonIndexedParameterTypes,
                ", ",
                new Collection.Function<NamedTypeName, String>() {
                    @Override
                    public String apply(NamedTypeName typeName) {
                        return "new $T<$T>() {}";
                    }
                });

        methodBuilder.addStatement("$T event = new $T($S, \n" +
                "$T.<$T<?>>asList(" + indexedAsListParams + "),\n" +
                "$T.<$T<?>>asList(" + nonIndexedAsListParams + "))", objects.toArray());
    }

    private static class NamedTypeName {
        private final TypeName typeName;
        private final String name;

        NamedTypeName(String name, TypeName typeName) {
            this.name = name;
            this.typeName = typeName;
        }

        public String getName() {
            return name;
        }

        public TypeName getTypeName() {
            return typeName;
        }
    }

}
