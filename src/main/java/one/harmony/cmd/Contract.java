/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package one.harmony.cmd;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import one.harmony.rpc.HmyResponse;
import one.harmony.transaction.Handler;

public abstract class Contract {

	public static final String BIN_NOT_PROVIDED = "Bin file was not provided";
	public static final String FUNC_DEPLOY = "deploy";

	protected final String contractBinary;
	protected String contractAddress;
	protected ContractGasProvider gasProvider;
	protected TransactionReceipt transactionReceipt;
	protected Map<String, String> deployedAddresses;
	protected DefaultBlockParameter defaultBlockParameter = DefaultBlockParameterName.LATEST;

	private Handler handler;

	protected Contract(String contractBinary, String contractAddress, Handler handler,
			ContractGasProvider gasProvider) {
		this.handler = handler;
		if (contractAddress == null) {
			this.contractAddress = null;
		} else {
			one.harmony.account.Address addr = new one.harmony.account.Address(contractAddress);
			this.contractAddress = addr.getHexAddr();
		}
		this.contractBinary = contractBinary;
		this.gasProvider = gasProvider;
	}

	protected Contract(String contractBinary, String contractAddress, ContractGasProvider gasProvider) {
		if (contractAddress == null) {
			this.contractAddress = null;
		} else {
			one.harmony.account.Address addr = new one.harmony.account.Address(contractAddress);
			this.contractAddress = addr.getHexAddr();
		}
		this.contractBinary = contractBinary;
		this.gasProvider = gasProvider;
	}

	protected Contract(String contractBinary, String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
		if (contractAddress == null) {
			this.contractAddress = null;
		} else {
			one.harmony.account.Address addr = new one.harmony.account.Address(contractAddress);
			this.contractAddress = addr.getHexAddr();
		}
		this.contractBinary = contractBinary;
		this.gasProvider = new StaticGasProvider(gasPrice, gasLimit);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setContractAddress(String contractAddress) {
		if (contractAddress == null) {
			this.contractAddress = null;
		} else {
			one.harmony.account.Address addr = new one.harmony.account.Address(contractAddress);
			this.contractAddress = addr.getHexAddr();
		}
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setTransactionReceipt(TransactionReceipt transactionReceipt) {
		this.transactionReceipt = transactionReceipt;
	}

	public String getContractBinary() {
		return contractBinary;
	}

	public void setGasProvider(ContractGasProvider gasProvider) {
		this.gasProvider = gasProvider;
	}

	/**
	 * Check that the contract deployed at the address associated with this smart
	 * contract wrapper is in fact the contract you believe it is.
	 *
	 * <p>
	 * This method uses the <a href=
	 * "https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getcode">eth_getCode</a>
	 * method to get the contract byte code and validates it against the byte code
	 * stored in this smart contract wrapper.
	 *
	 * @return true if the contract is valid
	 * @throws IOException if unable to connect to web3j node
	 */
	public boolean isValid() throws IOException {
		if (contractBinary.equals(BIN_NOT_PROVIDED)) {
			throw new UnsupportedOperationException("Contract binary not present in contract wrapper, "
					+ "please generate your wrapper using -abiFile=<file>");
		}

		if (contractAddress.equals("")) {
			throw new UnsupportedOperationException(
					"Contract binary not present, you will need to regenerate your smart "
							+ "contract wrapper with web3j v2.2.0+");
		}

		HmyResponse response = this.handler.getCode(contractAddress, DefaultBlockParameterName.LATEST);
		if (response.hasError()) {
			return false;
		}

		String code = Numeric.cleanHexPrefix(response.getClientResponse());
		int metadataIndex = code.indexOf("a165627a7a72305820");
		if (metadataIndex != -1) {
			code = code.substring(0, metadataIndex);
		}
		// There may be multiple contracts in the Solidity bytecode, hence we only check
		// for a
		// match with a subset
		return !code.isEmpty() && contractBinary.contains(code);
	}

	/**
	 * If this Contract instance was created at deployment, the TransactionReceipt
	 * associated with the initial creation will be provided, e.g. via a
	 * <em>deploy</em> method. This will not persist for Contracts instances
	 * constructed via a <em>load</em> method.
	 *
	 * @return the TransactionReceipt generated at contract deployment
	 */
	public Optional<TransactionReceipt> getTransactionReceipt() {
		return Optional.ofNullable(transactionReceipt);
	}

	/**
	 * Sets the default block parameter. This use useful if one wants to query
	 * historical state of a contract.
	 *
	 * @param defaultBlockParameter the default block parameter
	 */
	public void setDefaultBlockParameter(DefaultBlockParameter defaultBlockParameter) {
		this.defaultBlockParameter = defaultBlockParameter;
	}

	/**
	 * Execute constant function call - i.e. a call that does not change state of
	 * the contract
	 *
	 * @param function to call
	 * @return {@link List} of values returned by function call
	 */
	private List<Type> executeCall(Function function) throws Exception {
		String encodedFunction = FunctionEncoder.encode(function);

		String value = this.handler.call(contractAddress, encodedFunction, defaultBlockParameter,
				this.gasProvider.getGasLimit(encodedFunction));

		return FunctionReturnDecoder.decode(value, function.getOutputParameters());
	}

	@SuppressWarnings("unchecked")
	protected <T extends Type> T executeCallSingleValueReturn(Function function) throws Exception {
		List<Type> values = executeCall(function);
		if (!values.isEmpty()) {
			return (T) values.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends Type, R> R executeCallSingleValueReturn(Function function, Class<R> returnType)
			throws Exception {
		T result = executeCallSingleValueReturn(function);
		if (result == null) {
			throw new ContractCallException("Empty value (0x) returned from contract");
		}

		Object value = result.getValue();
		if (returnType.isAssignableFrom(value.getClass())) {
			return (R) value;
		} else if (result.getClass().equals(Address.class) && returnType.equals(String.class)) {
			return (R) result.toString(); // cast isn't necessary
		} else {
			throw new ContractCallException(
					"Unable to convert response: " + value + " to expected type: " + returnType.getSimpleName());
		}
	}

	protected List<Type> executeCallMultipleValueReturn(Function function) throws Exception {
		return executeCall(function);
	}

	protected TransactionReceipt executeTransaction(Function function) throws Exception, TransactionException {
		return executeTransaction(function, BigInteger.ZERO);
	}

	private TransactionReceipt executeTransaction(Function function, BigInteger weiValue)
			throws Exception, TransactionException {
		return executeTransaction(FunctionEncoder.encode(function), weiValue, function.getName());
	}

	TransactionReceipt executeTransaction(String data, BigInteger weiValue, String funcName)
			throws TransactionException, Exception {
		return executeTransaction(data, weiValue, funcName, false);
	}

	/**
	 * Given the duration required to execute a transaction.
	 *
	 * @param data     to send in transaction
	 * @param weiValue in Wei to send in transaction
	 * @return {@link Optional} containing our transaction receipt
	 * @throws IOException          if the call to the node fails
	 * @throws TransactionException if the transaction was not mined while waiting
	 */
	TransactionReceipt executeTransaction(String data, BigInteger weiValue, String funcName, boolean constructor)
			throws TransactionException, Exception {
		TransactionReceipt receipt = this.handler.send(contractAddress, data, weiValue,
				gasProvider.getGasPrice(funcName), gasProvider.getGasLimit(funcName), constructor);
		if (!receipt.isStatusOK()) {
			throw new TransactionException(String.format(
					"Transaction has failed with status: %s. " + "Gas used: %s. (not-enough gas?)", receipt.getStatus(),
					receipt.getGasUsedRaw() != null ? receipt.getGasUsed().toString() : "unknown"));
		}
		return receipt;
	}

	protected <T extends Type> RemoteFunctionCall<T> executeRemoteCallSingleValueReturn(Function function) {
		return new RemoteFunctionCall<>(function, () -> executeCallSingleValueReturn(function));
	}

	protected <T> RemoteFunctionCall<T> executeRemoteCallSingleValueReturn(Function function, Class<T> returnType) {
		return new RemoteFunctionCall<>(function, () -> executeCallSingleValueReturn(function, returnType));
	}

	protected RemoteFunctionCall<List<Type>> executeRemoteCallMultipleValueReturn(Function function) {
		return new RemoteFunctionCall<>(function, () -> executeCallMultipleValueReturn(function));
	}

	protected RemoteFunctionCall<TransactionReceipt> executeRemoteCallTransaction(Function function) {
		return new RemoteFunctionCall<>(function, () -> executeTransaction(function));
	}

	protected RemoteFunctionCall<TransactionReceipt> executeRemoteCallTransaction(Function function,
			BigInteger weiValue) {
		return new RemoteFunctionCall<>(function, () -> executeTransaction(function, weiValue));
	}

	private static <T extends Contract> T create(T contract, String binary, String encodedConstructor, BigInteger value)
			throws Exception, TransactionException {
		TransactionReceipt transactionReceipt = contract.executeTransaction(binary + encodedConstructor, value,
				FUNC_DEPLOY, true);

		String contractAddress = transactionReceipt.getContractAddress();
		if (contractAddress == null) {
			throw new RuntimeException("Empty contract address returned");
		}
		contract.setContractAddress(contractAddress);
		contract.setTransactionReceipt(transactionReceipt);

		return contract;
	}

	protected static <T extends Contract> T deploy(Class<T> type, Handler handler,
			ContractGasProvider contractGasProvider, String binary, String encodedConstructor, BigInteger value)
			throws RuntimeException, TransactionException {

		try {
			Constructor<T> constructor = type.getDeclaredConstructor(String.class, Handler.class,
					ContractGasProvider.class);
			constructor.setAccessible(true);

			// we want to use null here to ensure that "to" parameter on message is not
			// populated
			T contract = constructor.newInstance(null, handler, contractGasProvider);

			return create(contract, binary, encodedConstructor, value);
		} catch (TransactionException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static <T extends Contract> T deploy(Class<T> type, Handler handler, BigInteger gasPrice,
			BigInteger gasLimit, String binary, String encodedConstructor, BigInteger value)
			throws RuntimeException, TransactionException {
		return deploy(type, handler, new StaticGasProvider(gasPrice, gasLimit), binary, encodedConstructor, value);
	}

	public static <T extends Contract> RemoteCall<T> deployRemoteCall(Class<T> type, Handler handler,
			ContractGasProvider contractGasProvider, String binary, String encodedConstructor, BigInteger value) {
		return new RemoteCall<>(() -> deploy(type, handler, contractGasProvider, binary, encodedConstructor, value));
	}

	public static <T extends Contract> RemoteCall<T> deployRemoteCall(Class<T> type, Handler handler,
			BigInteger gasPrice, BigInteger gasLimit, String binary, String encodedConstructor, BigInteger value) {
		return new RemoteCall<>(() -> deploy(type, handler, gasPrice, gasLimit, binary, encodedConstructor, value));
	}

	public static <T extends Contract> RemoteCall<T> deployRemoteCall(Class<T> type, Handler handler,
			BigInteger gasPrice, BigInteger gasLimit, String binary, String encodedConstructor) {
		return deployRemoteCall(type, handler, gasPrice, gasLimit, binary, encodedConstructor, BigInteger.ZERO);
	}

	public static <T extends Contract> RemoteCall<T> deployRemoteCall(Class<T> type, Handler handler,
			ContractGasProvider contractGasProvider, String binary, String encodedConstructor) {
		return new RemoteCall<>(
				() -> deploy(type, handler, contractGasProvider, binary, encodedConstructor, BigInteger.ZERO));
	}

	protected TransactionReceipt send(String to, String data, BigInteger value, BigInteger gasPrice,
			BigInteger gasLimit) throws Exception, TransactionException {
		return this.handler.send(to, data, value, gasPrice, gasLimit, false);
	}

	public static EventValues staticExtractEventParameters(Event event, Log log) {
		final List<String> topics = log.getTopics();
		String encodedEventSignature = EventEncoder.encode(event);
		if (topics == null || topics.size() == 0 || !topics.get(0).equals(encodedEventSignature)) {
			return null;
		}

		List<Type> indexedValues = new ArrayList<>();
		List<Type> nonIndexedValues = FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());

		List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
		for (int i = 0; i < indexedParameters.size(); i++) {
			Type value = FunctionReturnDecoder.decodeIndexedValue(topics.get(i + 1), indexedParameters.get(i));
			indexedValues.add(value);
		}
		return new EventValues(indexedValues, nonIndexedValues);
	}

	protected EventValues extractEventParameters(Event event, Log log) {
		return staticExtractEventParameters(event, log);
	}

	protected List<EventValues> extractEventParameters(Event event, TransactionReceipt transactionReceipt) {
		return transactionReceipt.getLogs().stream().map(log -> extractEventParameters(event, log))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected EventValuesWithLog extractEventParametersWithLog(Event event, Log log) {
		return staticExtractEventParametersWithLog(event, log);
	}

	protected static EventValuesWithLog staticExtractEventParametersWithLog(Event event, Log log) {
		final EventValues eventValues = staticExtractEventParameters(event, log);
		return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
	}

	protected List<EventValuesWithLog> extractEventParametersWithLog(Event event,
			TransactionReceipt transactionReceipt) {
		return transactionReceipt.getLogs().stream().map(log -> extractEventParametersWithLog(event, log))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Subclasses should implement this method to return pre-existing addresses for
	 * deployed contracts.
	 *
	 * @param networkId the network id, for example "1" for the main-net, "3" for
	 *                  ropsten, etc.
	 * @return the deployed address of the contract, if known, and null otherwise.
	 */
	protected String getStaticDeployedAddress(String networkId) {
		return null;
	}

	public final void setDeployedAddress(String networkId, String address) {
		if (deployedAddresses == null) {
			deployedAddresses = new HashMap<>();
		}
		deployedAddresses.put(networkId, address);
	}

	public final String getDeployedAddress(String networkId) {
		String addr = null;
		if (deployedAddresses != null) {
			addr = deployedAddresses.get(networkId);
		}
		return addr == null ? getStaticDeployedAddress(networkId) : addr;
	}

	/** Adds a log field to {@link EventValues}. */
	public static class EventValuesWithLog {
		private final EventValues eventValues;
		private final Log log;

		private EventValuesWithLog(EventValues eventValues, Log log) {
			this.eventValues = eventValues;
			this.log = log;
		}

		public List<Type> getIndexedValues() {
			return eventValues.getIndexedValues();
		}

		public List<Type> getNonIndexedValues() {
			return eventValues.getNonIndexedValues();
		}

		public Log getLog() {
			return log;
		}
	}

	@SuppressWarnings("unchecked")
	protected static <S extends Type, T> List<T> convertToNative(List<S> arr) {
		List<T> out = new ArrayList<>();
		for (final S s : arr) {
			out.add((T) s.getValue());
		}
		return out;
	}
}
