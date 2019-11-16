package one.harmony.cmd;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.http.HttpService;

import one.harmony.common.RequestTester;
import one.harmony.rpc.RPC;

public class BalanceRequestTest extends RequestTester {

	private RPC rpc;

	@Override
	protected void initWeb3Client(HttpService httpService) {
		rpc = new RPC(httpService);
	}

	@Test
	public void testBalanceShard0Request() throws Exception {
		String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		rpc.getBalance(oneAddress).send();

		verifyResult(
				"{\"jsonrpc\":\"2.0\",\"method\":\"hmy_getBalance\",\"params\":[\"one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy\",\"latest\"],\"id\":0}");
	}

}
