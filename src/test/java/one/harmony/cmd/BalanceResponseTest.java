package one.harmony.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import one.harmony.common.ResponseTester;
import one.harmony.rpc.HmyResponse;

public class BalanceResponseTest extends ResponseTester {

	@Test
	public void testBalanceShard0Response() throws Exception {
		String expected = "{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0x230e5821e9bfaf1f285\"}";
		buildResponse(expected);
		HmyResponse response = deserialiseResponse(HmyResponse.class);
		assertEquals(response.getResult(), ("0x230e5821e9bfaf1f285"));
	}
}
