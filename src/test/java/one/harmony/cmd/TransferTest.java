package one.harmony.cmd;

import org.junit.jupiter.api.Test;

public class TransferTest {

	private static final int LOCAL_NET = 2;

	@Test
	public void testTransfer() throws Exception {
		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy"; // Harmony localnet addresses
		String to = "one1pf75h0t4am90z8uv3y0dgunfqp4lj8wr3t5rsp"; // Harmony localnet addresses
		double amount = 40;
		int fromShard = 0;
		int toShard = 1;
		boolean dryRun = false;
		int waitToConfirmTime = 0;
		String passphrase = "harmony-one";
		new Transfer(from, to, amount, fromShard, toShard).execute(LOCAL_NET, passphrase, dryRun, waitToConfirmTime);
	}

}
