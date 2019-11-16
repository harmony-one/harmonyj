package one.harmony.cmd;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.http.HttpService;

import one.harmony.common.RequestTester;
import one.harmony.rpc.RPC;

public class BlockchainRequestTest extends RequestTester {

	private RPC rpc;

	@Override
	protected void initWeb3Client(HttpService httpService) {
		rpc = new RPC(httpService);
	}

	@Test
	public void testProtocolVersionRequest() throws Exception {
		rpc.getProtocolVersion().send();

		verifyResult("{\"jsonrpc\":\"2.0\",\"method\":\"hmy_protocolVersion\",\"params\":[\"latest\"],\"id\":0}");
	}

}
