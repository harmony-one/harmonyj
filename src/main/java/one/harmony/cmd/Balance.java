package one.harmony.cmd;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.web3j.utils.Numeric;

import one.harmony.common.Config;
import one.harmony.rpc.HmyResponse;
import one.harmony.rpc.RPC;
import one.harmony.rpc.ShardingStructure.RPCRoutes;
import one.harmony.sharding.Sharding;

/**
 * Balance class provides utility to check the balance of a Harmony account
 * (provided using Harmony one address) across all shards.
 * 
 * @author gupadhyaya
 *
 */
public class Balance {
	/**
	 * Check account balance on all shards. Queries for the latest account balance
	 * given a Harmony One Address
	 * 
	 * @param oneAddress
	 * @return Formatted output containing shard id and balance
	 */
	public static String check(String oneAddress) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		List<RPCRoutes> structure = Sharding.getShardingStructure();
		for (RPCRoutes rpcRoutes : structure) {
			RPC node = new RPC(rpcRoutes.getHttp());
			HmyResponse response;
			response = node.getBalance(oneAddress).send();
			if (response.hasError()) {
				throw new Exception("failed to fetch the balance for address: " + oneAddress);
			}
			BigInteger bln = Numeric.toBigInt(response.getResult());
			sb.append(String.format("{ \"shard\": %d, \"amount\": %s }", rpcRoutes.getShardID(),
					covertBalanceToReadableFormat(bln)));
			sb.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Checks account balance using local Harmony instance
	 * 
	 * @param oneAddress
	 * @return
	 * @throws Exception
	 */
	public static String checkLocal(String oneAddress) throws Exception {
		RPC node = new RPC(Config.node);
		HmyResponse response = node.getBalance(oneAddress).send();
		if (response.hasError()) {
			throw new Exception("failed to fetch the balance for address: " + oneAddress);
		}
		BigInteger bln = Numeric.toBigInt(response.getResult());
		return covertBalanceToReadableFormat(bln);
	}

	/**
	 * Checks account balance using local Harmony instance
	 * 
	 * @param oneAddress
	 * @return
	 * @throws Exception
	 */
	public static String checkLocal(String url, String oneAddress) throws Exception {
		RPC node = new RPC(url);
		HmyResponse response = node.getBalance(oneAddress).send();
		if (response.hasError()) {
			throw new Exception("failed to fetch the balance for address: " + oneAddress);
		}
		BigInteger bln = Numeric.toBigInt(response.getResult());
		return covertBalanceToReadableFormat(bln);
	}

	private static String covertBalanceToReadableFormat(BigInteger balance) {
		BigDecimal decimalBln = new BigDecimal(balance);
		BigDecimal nano = new BigDecimal(BigInteger.TEN.pow(9));
		double bln = decimalBln.divide(nano).divide(nano).doubleValue();
		// TODO: reduce the precision if needed using BigDecimal.round(new
		// MathContext(13))
		return String.valueOf(bln);
	}

	public static void main(String[] args) throws Exception {
		String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy"; // One of the harmony accounts in the localnet
		String url = "http://127.0.0.1:9500";
		System.out.println(checkLocal(url, oneAddress));
	}
}
