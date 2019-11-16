package one.harmony.sharding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import one.harmony.common.ResponseTester;
import one.harmony.rpc.ShardingStructure;

public class ShardingStructureResponseTest extends ResponseTester {

	@Test
	public void testShardingStructureResponse() throws Exception {
		String expected = "{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":[{\"current\":true,\"http\":\"http://127.0.0.1:9500\",\"shardID\":0,\"ws\":\"ws://127.0.0.1:9800\"}, {\"current\":false,\"http\":\"http://127.0.0.1:9501\",\"shardID\":1,\"ws\":\"ws://127.0.0.1:9801\"}]}";
		buildResponse(expected);
		ShardingStructure response = deserialiseResponse(ShardingStructure.class);
		assertEquals(response.getResult().toString(),
				"[{\"current\":true,\"http\":\"http://127.0.0.1:9500\",\"shardID\":0,\"ws\":\"ws://127.0.0.1:9800\"}, {\"current\":false,\"http\":\"http://127.0.0.1:9501\",\"shardID\":1,\"ws\":\"ws://127.0.0.1:9801\"}]");
	}
}
