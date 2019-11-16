package one.harmony.sharding;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.http.HttpService;

import one.harmony.common.RequestTester;
import one.harmony.rpc.RPC;

public class ShardingStructureRequestTest extends RequestTester {

	private RPC rpc;

	@Override
	protected void initWeb3Client(HttpService httpService) {
		rpc = new RPC(httpService);
	}

	@Test
	public void testShardingStructureRequest() throws Exception {
		rpc.getShardingStructure().send();

		verifyResult("{\"jsonrpc\":\"2.0\",\"method\":\"hmy_getShardingStructure\",\"params\":[],\"id\":0}");
	}

}
