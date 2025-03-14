package one.harmony.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.beans.Transient;

import org.junit.jupiter.api.Test;

import one.harmony.account.Address;
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

	@Test
	public void testBalanceShard0() throws Exception {
		String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		Address addr = new Address(oneAddress);
		assertEquals(Balance.checkLocal(oneAddress), Balance.checkLocal(addr.getHexAddr()));
	}
}
