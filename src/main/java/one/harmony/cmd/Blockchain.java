package one.harmony.cmd;

//import org.json.simple.JSONObject;

import one.harmony.account.HistoryParams;
import one.harmony.common.Config;
import one.harmony.rpc.AccountTransactionsResponse;
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

	public static String getAccountTransactions(String node, HistoryParams params) throws Exception {
		AccountTransactionsResponse response = new RPC(node).getTransactionsHistory(params).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}
		return response.getResult().toString();
	}

	public static String getAccountTransactions(HistoryParams params) throws Exception {
		return getAccountTransactions(Config.node, params);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getProtocolVersion());
	}

}
