package one.harmony.cmd;

import org.junit.jupiter.api.Test;

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
		String to = "one1pf75h0t4am90z8uv3y0dgunfqp4lj8wr3t5rsp"; // Harmony localnet addresses
		String amount = "1";
		int fromShard = 0;
		int toShard = 1;
		String memo = " ";// "0x5061796d656e7420666f722078797a";
		// Create transfer object
		Transfer t = new Transfer(from, to, amount, fromShard, toShard, memo);
		// Prepare transfer
		String passphrase = "harmony-one";
		t.prepare(passphrase); // offline, no need to connect to network
		// Execute transfer
		boolean dryRun = false;
		int waitToConfirmTime = 0;
		String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime); // needs connection to network
		Keys.cleanKeyStore();
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

}
