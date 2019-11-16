package one.harmony.cmd;

import one.harmony.common.Config;
import one.harmony.rpc.HmyResponse;
import one.harmony.rpc.RPC;

/**
 * Blockchain class retrieves the harmony protocol version.
 * 
 * @author gupadhyaya
 *
 */
public class Blockchain {

	/**
	 * GetProtocolVersion of Harmony network.
	 * 
	 * @return protocol version
	 * @throws Exception
	 */
	public static String getProtocolVersion() throws Exception {
		RPC rpc = new RPC(Config.node);
		HmyResponse response = rpc.getProtocolVersion().send();
		if (response.hasError()) {
			throw new Exception("failed to fetch protocol version");
		}
		return response.getResult();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getProtocolVersion());
	}

}
