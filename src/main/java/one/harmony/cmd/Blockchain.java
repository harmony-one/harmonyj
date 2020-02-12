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

	/**
	 * Send raw transaction to blockchain
	 * 
	 * @param rawTransaction
	 * @return
	 * @throws Exception
	 */
	public static String sendRawTransaction(String rawTransaction) throws Exception {
		HmyResponse response = new RPC(Config.node).sendRawTransaction(rawTransaction).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}
		return response.getResult();
	}

	/**
	 * Send raw transaction to blockchain using the specified node url
	 * 
	 * @param nodeUrl
	 * @param rawTransaction
	 * @return
	 * @throws Exception
	 */
	public static String sendRawTransaction(String nodeUrl, String rawTransaction) throws Exception {
		HmyResponse response = new RPC(nodeUrl).sendRawTransaction(rawTransaction).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}
		return response.getResult();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getProtocolVersion());
		String node = "http://localhost:9500";
		String rawTransaction = "0x...";
		System.out.println(sendRawTransaction(node, rawTransaction));
	}

}
