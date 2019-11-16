package one.harmony.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import one.harmony.rpc.HmyResponse;
import one.harmony.rpc.RPC;

/**
 * Blockchain class retrieves the harmony protocol version.
 * 
 * @author gupadhyaya
 *
 */
public class Blockchain {
	private static final Logger log = LoggerFactory.getLogger(Blockchain.class);

	/**
	 * GetProtocolVersion of Harmony network.
	 * 
	 * @return protocol version
	 * @throws Exception
	 */
	public static String getProtocolVersion() throws Exception {
		RPC rpc = new RPC(RPC.DEFAULT_URL);
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
