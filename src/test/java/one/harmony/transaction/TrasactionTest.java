package one.harmony.transaction;

import org.junit.jupiter.api.Test;

import one.harmony.rpc.RPC;
import one.harmony.rpc.HmyResponse;

public class TrasactionTest {

	@Test
	public void testTransactionCount() throws Exception {
		String hexAddr = "a65dbc745c9d2fff97bb4f614c557f022275d3f9";
		HmyResponse response = new RPC().getTransactionCount(hexAddr).send();
		System.out.println();
	}
}
