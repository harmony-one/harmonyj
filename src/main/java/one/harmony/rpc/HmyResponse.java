package one.harmony.rpc;

import org.web3j.protocol.core.Response;

public class HmyResponse extends Response<String> {

	public String getClientResponse() {
		return getResult();
	}
}
