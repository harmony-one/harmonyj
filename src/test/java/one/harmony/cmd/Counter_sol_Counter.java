package one.harmony.cmd;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import one.harmony.transaction.Handler;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the one.harmony.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/harmony-one/harmonyj/tree/master/src/main/java/one/harmony/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class Counter_sol_Counter extends Contract {
    private static final String BINARY = "60806040526000805534801561001457600080fd5b5060b0806100236000396000f3fe6080604052348015600f57600080fd5b5060043610603c5760003560e01c80635b34b966146041578063a87d942c146049578063f5c5ad83146061575b600080fd5b60476067565b005b604f6072565b60408051918252519081900360200190f35b60476078565b600080546001019055565b60005490565b6000805460001901905556fea165627a7a7230582045664cad9b33cca13d85b3eb4941350cace5a24cf27a3f4d3621446578d3d9810029";

    public static final String FUNC_INCREMENTCOUNTER = "incrementCounter";

    public static final String FUNC_GETCOUNT = "getCount";

    public static final String FUNC_DECREMENTCOUNTER = "decrementCounter";

    protected Counter_sol_Counter(String contractAddress, Handler handler, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, handler, contractGasProvider);
    }

    protected Counter_sol_Counter(String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, gasPrice, gasLimit);
    }

    protected Counter_sol_Counter(String contractAddress, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> incrementCounter() {
        final Function function = new Function(
                FUNC_INCREMENTCOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getCount() {
        final Function function = new Function(FUNC_GETCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> decrementCounter() {
        final Function function = new Function(
                FUNC_DECREMENTCOUNTER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Counter_sol_Counter load(String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter_sol_Counter(contractAddress, gasPrice, gasLimit);
    }

    public static Counter_sol_Counter load(String contractAddress, ContractGasProvider contractGasProvider) {
        return new Counter_sol_Counter(contractAddress, contractGasProvider);
    }

    public static RemoteCall<Counter_sol_Counter> deploy(Handler handler, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Counter_sol_Counter.class, handler, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Counter_sol_Counter> deploy(Handler handler, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Counter_sol_Counter.class, handler, gasPrice, gasLimit, BINARY, "");
    }
}
