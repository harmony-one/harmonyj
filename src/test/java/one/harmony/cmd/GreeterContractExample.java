package one.harmony.cmd;

import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import one.harmony.transaction.ChainID;
import one.harmony.transaction.Handler;

public class GreeterContractExample {
	private static final int LOCAL_NET = 2;
	private static String contractAddress = "0x38748bf499f05b6468b45b229f3766f5454dbf10";

	public static void testImportPrivateKey() throws Exception {
		Keys.cleanKeyStore();
		String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String accountName = "a1";
		Keys.importPrivateKey(key, accountName);
	}

	public static String deploy(Handler handler, ContractGasProvider contractGasProvider) throws Exception {
		Greeter_sol_Greeter greeter = Greeter_sol_Greeter.deploy(handler, contractGasProvider, "test").send();
		String contractAddress = greeter.getContractAddress();
		return contractAddress;
	}

	public static void main(String[] args) throws Exception {
		testImportPrivateKey();

		class MyGasProvider extends StaticGasProvider {

			public MyGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
				super(gasPrice, gasLimit);
				// TODO Auto-generated constructor stub
			}

		}

		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		String passphrase = "harmony-one";
		String node = "http://127.0.0.1:9500/";
		Handler handler = new Handler(from, passphrase, node, ChainID.LOCAL);
		MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("6721900"));

		contractAddress = deploy(handler, contractGasProvider);
		System.out.println("Contract deploy at " + contractAddress);

		Greeter_sol_Greeter contract = Greeter_sol_Greeter.load(contractAddress, contractGasProvider);
		contract.setHandler(handler);
		System.out.println("Value stored in remote smart contract: " + contract.greet().send());
		TransactionReceipt transactionReceipt = contract.newGreeting("Well hello again").send();
		for (Greeter_sol_Greeter.ModifiedEventResponse event : contract.getModifiedEvents(transactionReceipt)) {
			System.out.println(
					"Modify event fired, previous value: " + event.oldGreeting + ", new value: " + event.newGreeting);
			System.out.println("Indexed event previous value: " + Numeric.toHexString(event.oldGreetingIdx)
					+ ", new value: " + Numeric.toHexString(event.newGreetingIdx));
		}
		System.out.println("Value stored in remote smart contract: " + contract.greet().send());
	}
}
