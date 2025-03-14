package one.harmony.cmd;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import one.harmony.account.Address;

public class TransferTest {
	private static final int LOCAL_NET = 2;

	public void testImportPrivateKey() throws Exception {
		String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String accountName = "a1";
		Keys.importPrivateKey(key, accountName);
	}

	@Test
	public void testTransfer() throws Exception {
		Keys.cleanKeyStore();
		testImportPrivateKey();

		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy"; // Harmony localnet addresses
		String to = "one18n8e7472pg5fqvcfcr5hg0npquha24wsxmjheg"; // Harmony localnet addresses
		double amount = 1;
		int fromShard = 0;
		int toShard = fromShard;
		String memo = " ";// "0x5061796d656e7420666f722078797a";
		// Create transfer object
		Transfer t = new Transfer(from, to, String.valueOf(amount), fromShard, toShard, memo);

		double fromBalance0 = Double.valueOf(Balance.checkLocal(from));
		double toBalance0 = Double.valueOf(Balance.checkLocal(to));

		// Prepare transfer
		String passphrase = "harmony-one";
		t.prepare(passphrase); // offline, no need to connect to network
		// Execute transfer
		boolean dryRun = false;
		int waitToConfirmTime = 2000;
		String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime); // needs connection to network
		Keys.cleanKeyStore();

		assertTrue(Double.valueOf(Balance.checkLocal(from)) < fromBalance0 - amount);
		assertTrue(Double.valueOf(Balance.checkLocal(to)) == toBalance0 + amount);
	}

	@Test
	public void testTransferHexAddress() throws Exception {
		Keys.cleanKeyStore();
		testImportPrivateKey();

		Address fromAddr = new Address("one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy");
		Address toAddr = new Address("one18n8e7472pg5fqvcfcr5hg0npquha24wsxmjheg");
		String from = fromAddr.getHexAddr();
		String to = toAddr.getHexAddr();

		double amount = 1;
		int fromShard = 0;
		int toShard = fromShard;
		String memo = " ";// "0x5061796d656e7420666f722078797a";
		// Create transfer object
		Transfer t = new Transfer(from, to, String.valueOf(amount), fromShard, toShard, memo);

		double fromBalance0 = Double.valueOf(Balance.checkLocal(from));
		double toBalance0 = Double.valueOf(Balance.checkLocal(to));

		// Prepare transfer
		String passphrase = "harmony-one";
		t.prepare(passphrase); // offline, no need to connect to network
		// Execute transfer
		boolean dryRun = false;
		int waitToConfirmTime = 2000;
		String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime); // needs connection to network
		Keys.cleanKeyStore();

		double fromBalance1 = Double.valueOf(Balance.checkLocal(from));
		double toBalance1 = Double.valueOf(Balance.checkLocal(to));

		assertTrue(fromBalance1 < fromBalance0 - amount);
		assertTrue(toBalance1 == toBalance0 + amount);
	}

	@Test
	public void testLocalTransfer() throws Exception {
		Keys.cleanKeyStore();
		testImportPrivateKey();
		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy"; // Harmony localnet addresses
		String to = "one1pf75h0t4am90z8uv3y0dgunfqp4lj8wr3t5rsp"; // Harmony localnet addresses
		String amount = "1.002";
		int fromShard = 0;
		int toShard = 1;
		String memo = "0x5061796d656e7420666f722078797a";
		// Create transfer object
		Transfer t = new Transfer(from, to, amount, fromShard, toShard, memo);
		// Prepare transfer
		String passphrase = "harmony-one";
		String url = "http://127.0.0.1:9500/";
		t.prepare(passphrase, url); // offline, no need to connect to network
		// Execute transfer
		boolean dryRun = false;
		int waitToConfirmTime = 0;
		String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime); // needs connection to network
		Keys.cleanKeyStore();
	}

	@Test
	public void testTransferGasLimitSet() throws Exception {
		Keys.cleanKeyStore();
		testImportPrivateKey();
		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy"; // Harmony localnet addresses
		String to = "one1pf75h0t4am90z8uv3y0dgunfqp4lj8wr3t5rsp"; // Harmony localnet addresses
		String amount = "1.002";
		int fromShard = 0;
		int toShard = 1;
		String memo = "0x5061796d656e7420666f722078797a";
		// Create transfer object
		Transfer t = new Transfer(from, to, amount, fromShard, toShard, memo);
		// Prepare transfer
		String passphrase = "harmony-one";
		String url = "http://127.0.0.1:9500/";
		t.prepare(passphrase, url); // offline, no need to connect to network
		// Execute transfer
		boolean dryRun = false;
		int waitToConfirmTime = 0;
		t.SetGas(50000);
		String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime); // needs connection to network
		System.out.println(txHash);
		Keys.cleanKeyStore();
	}

}
