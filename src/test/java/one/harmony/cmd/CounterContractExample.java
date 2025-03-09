package one.harmony.cmd;

import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import one.harmony.account.Address;
import one.harmony.transaction.ChainID;
import one.harmony.transaction.Handler;

public class CounterContractExample {
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

		String contractAddress = deploy(handler, contractGasProvider);
		System.out.println("Contract deploy at " + contractAddress);

		Counter_sol_Counter contract = Counter_sol_Counter.load(contractAddress, contractGasProvider);
		contract.setHandler(handler);
		System.out.println("Value stored in remote smart contract: " + contract.getCount().send());
		TransactionReceipt transactionReceipt = contract.incrementCounter().send();
		System.out.println(transactionReceipt);

		BigInteger count = contract.getCount().send();
		System.out.println("Value stored in remote smart contract: " + count);

		Address addr = new Address(contractAddress);
		Counter_sol_Counter contractHexAddress = Counter_sol_Counter.load(addr.getHexAddr(), contractGasProvider);
		contractHexAddress.setHandler(handler);
		contractHexAddress.incrementCounter().send();

		Counter_sol_Counter contractOneAddress = Counter_sol_Counter.load(addr.getOneAddr(), contractGasProvider);
		contractOneAddress.setHandler(handler);
		contractOneAddress.incrementCounter().send();
		
		assertEquals(contractHexAddress.getCount().send(), count.add(new BigInteger("2")));
		assertEquals(contractOneAddress.getCount().send(), count.add(new BigInteger("2")));
	}

	@Test
	public void testDeploy() throws Exception {
		main(null);
	}
}
