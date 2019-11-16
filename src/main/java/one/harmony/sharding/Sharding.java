package one.harmony.sharding;

import java.util.List;

import one.harmony.common.Config;
import one.harmony.rpc.RPC;
import one.harmony.rpc.ShardingStructure;
import one.harmony.rpc.ShardingStructure.RPCRoutes;

/**
 * Sharding class
 * 
 * @author gupadhyaya
 *
 */
public class Sharding {

	public static List<RPCRoutes> getShardingStructure() throws Exception {
		RPC rpc = new RPC(Config.node);

		ShardingStructure structure = rpc.getShardingStructure().send();
		if (structure.hasError()) {
			throw new Exception(structure.getError().getMessage());
		}

		return structure.getResult();
	}

	public static boolean validateShardIDs(int fromShardID, int toShardID, int shardCount)
			throws IllegalArgumentException {
		if (!isValidShardID(fromShardID, shardCount)) {
			throw new IllegalArgumentException("Invalid fromShardID");
		}
		if (!isValidShardID(toShardID, shardCount)) {
			throw new IllegalArgumentException("Invalid toShardID");
		}
		return true;
	}

	private static boolean isValidShardID(int shardID, int shardCount) {
		if (shardID > (shardCount - 1)) {
			return false;
		}
		return true;
	}

	public static String getHandlerFor(List<RPCRoutes> sharding, int shardID) throws IllegalArgumentException {
		for (RPCRoutes rpcRoute : sharding) {
			if (rpcRoute.getShardID() == shardID) {
				return rpcRoute.getHttp();
			}
		}
		throw new IllegalArgumentException("No handler exists for the input shardID");
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getShardingStructure());
	}

}
