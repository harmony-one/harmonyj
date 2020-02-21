package one.harmony.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import one.harmony.account.HistoryParams;
import one.harmony.common.ResponseTester;
import one.harmony.rpc.HmyResponse;

public class BlochchainResponseTest extends ResponseTester {

	@Test
	public void testProtocolVersionResponse() throws Exception {
		String expected = "{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0x1\"}";
		buildResponse(expected);
		HmyResponse response = deserialiseResponse(HmyResponse.class);
		assertEquals(response.getResult(), ("0x1"));
	}

	@Test
	public void testAccountHistory() throws Exception {
		String address = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		HistoryParams params = new HistoryParams(address);
		String expected = "";
		String actual = Blockchain.getAccountTransactions(params);
		assertEquals(expected, actual);
	}
}
