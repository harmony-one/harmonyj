package one.harmony.rpc;

/**
 * RPCMethod class provides the list of supported Harmony RPC methods. Currently
 * supports only getting protocol version, balance using harmony address, and
 * sending signed transaction
 * 
 * @author gupadhyaya
 *
 */
public final class RPCMethod {
	/* Blockchain */
	static final String ProtocolVersion = "hmy_protocolVersion";

	/* Account */
	static final String GetBalance = "hmy_getBalance";

	/* Contract */
	static final String GetCode = "hmy_getCode";
	static final String Call = "hmy_call";

	/* Transaction */
	static final String GetTransactionCount = "hmy_getTransactionCount";
	static final String SendTransaction = "hmy_sendTransaction";
	static final String SendRawTransaction = "hmy_sendRawTransaction";
	static final String GetTransactionReceipt = "hmy_getTransactionReceipt";
	static final String GetShardingStructure = "hmy_getShardingStructure";
	static final String GetTransactionsHistory = "hmy_getTransactionsHistory";
}
