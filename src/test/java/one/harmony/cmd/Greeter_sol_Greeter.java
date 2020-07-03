package one.harmony.cmd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import one.harmony.transaction.Handler;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.Log;
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
public class Greeter_sol_Greeter extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506040516106353803806106358339810180604052602081101561003357600080fd5b81019080805164010000000081111561004b57600080fd5b8201602081018481111561005e57600080fd5b815164010000000081118282018710171561007857600080fd5b5050600080546001600160a01b0319163317905580519093506100a492506001915060208401906100ab565b5050610146565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100ec57805160ff1916838001178555610119565b82800160010185558215610119579182015b828111156101195782518255916020019190600101906100fe565b50610125929150610129565b5090565b61014391905b80821115610125576000815560010161012f565b90565b6104e0806101556000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c806341c0e1b5146100465780634ac0d66e14610050578063cfae3217146100f6575b600080fd5b61004e610173565b005b61004e6004803603602081101561006657600080fd5b81019060208101813564010000000081111561008157600080fd5b82018360208201111561009357600080fd5b803590602001918460018302840111640100000000831117156100b557600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610196945050505050565b6100fe610386565b6040805160208082528351818301528351919283929083019185019080838360005b83811015610138578181015183820152602001610120565b50505050905090810190601f1680156101655780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6000546001600160a01b0316331415610194576000546001600160a01b0316ff5b565b806040518082805190602001908083835b602083106101c65780518252601f1990920191602091820191016101a7565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040518091039020600160405180828054600181600116156101000203166002900480156102505780601f1061022e576101008083540402835291820191610250565b820191906000526020600020905b81548152906001019060200180831161023c575b505060408051918290038220818352600180546002600019610100838516150201909116049284018390529094507f047dcd1aa8b77b0b943642129c767533eeacd700c7c1eab092b8ce05d2b2faf59350918691819060208201906060830190869080156102ff5780601f106102d4576101008083540402835291602001916102ff565b820191906000526020600020905b8154815290600101906020018083116102e257829003601f168201915b5050838103825284518152845160209182019186019080838360005b8381101561033357818101518382015260200161031b565b50505050905090810190601f1680156103605780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a3805161038290600190602084019061041c565b5050565b60018054604080516020601f600260001961010087891615020190951694909404938401819004810282018101909252828152606093909290918301828280156104115780601f106103e657610100808354040283529160200191610411565b820191906000526020600020905b8154815290600101906020018083116103f457829003601f168201915b505050505090505b90565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061045d57805160ff191683800117855561048a565b8280016001018555821561048a579182015b8281111561048a57825182559160200191906001019061046f565b5061049692915061049a565b5090565b61041991905b8082111561049657600081556001016104a056fea165627a7a7230582067dc94422cecd47a1b6d3664615b0f1c90024746084c131287c4b99d110f4b810029";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_NEWGREETING = "newGreeting";

    public static final String FUNC_GREET = "greet";

    public static final Event MODIFIED_EVENT = new Event("Modified", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected Greeter_sol_Greeter(String contractAddress, Handler handler, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, handler, contractGasProvider);
    }

    protected Greeter_sol_Greeter(String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, gasPrice, gasLimit);
    }

    protected Greeter_sol_Greeter(String contractAddress, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> newGreeting(String _greeting) {
        final Function function = new Function(
                FUNC_NEWGREETING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> greet() {
        final Function function = new Function(FUNC_GREET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public List<ModifiedEventResponse> getModifiedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MODIFIED_EVENT, transactionReceipt);
        ArrayList<ModifiedEventResponse> responses = new ArrayList<ModifiedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ModifiedEventResponse typedResponse = new ModifiedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldGreetingIdx = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newGreetingIdx = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.oldGreeting = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newGreeting = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static Greeter_sol_Greeter load(String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        return new Greeter_sol_Greeter(contractAddress, gasPrice, gasLimit);
    }

    public static Greeter_sol_Greeter load(String contractAddress, ContractGasProvider contractGasProvider) {
        return new Greeter_sol_Greeter(contractAddress, contractGasProvider);
    }

    public static RemoteCall<Greeter_sol_Greeter> deploy(Handler handler, ContractGasProvider contractGasProvider, String _greeting) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)));
        return deployRemoteCall(Greeter_sol_Greeter.class, handler, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Greeter_sol_Greeter> deploy(Handler handler, BigInteger gasPrice, BigInteger gasLimit, String _greeting) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)));
        return deployRemoteCall(Greeter_sol_Greeter.class, handler, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class ModifiedEventResponse {
        public Log log;

        public byte[] oldGreetingIdx;

        public byte[] newGreetingIdx;

        public String oldGreeting;

        public String newGreeting;
    }
}
