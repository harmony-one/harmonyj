package one.harmony.cmd;

import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.junit.jupiter.api.Test;

import one.harmony.transaction.ChainID;
import one.harmony.transaction.Handler;

public class CounterContractExample {
	private static final int LOCAL_NET = 2;
	private static String contractAddress = "0x9030054532eb96efbcb13aff1db9add36363e724";

	public static void testImportPrivateKey() throws Exception {
		Keys.cleanKeyStore();
		String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String accountName = "a1";
		Keys.importPrivateKey(key, accountName);
	}

	public static String deploy(Handler handler, ContractGasProvider contractGasProvider) throws Exception {
		Counter_sol_Counter greeter = Counter_sol_Counter.deploy(handler, contractGasProvider).send();
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
		MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("336038")); //

		contractAddress = deploy(handler, contractGasProvider);
		System.out.println("Contract deploy at " + contractAddress);

		Counter_sol_Counter contract = Counter_sol_Counter.load(contractAddress, contractGasProvider);
		contract.setHandler(handler);
		System.out.println("Value stored in remote smart contract: " + contract.getCount().send());
		TransactionReceipt transactionReceipt = contract.incrementCounter().send();
		System.out.println(transactionReceipt);
		System.out.println("Value stored in remote smart contract: " + contract.getCount().send());
	}
}
