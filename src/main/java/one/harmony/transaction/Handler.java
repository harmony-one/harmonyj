package one.harmony.transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Numeric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import one.harmony.account.Account;
import one.harmony.account.Address;
import one.harmony.rpc.HmyResponse;
import one.harmony.rpc.RPC;
import one.harmony.rpc.ShardingStructure.RPCRoutes;
import one.harmony.sharding.Sharding;

/**
 * Transaction handler class
 * 
 * @author gupadhyaya
 *
 */
public class Handler {
	private static final Logger log = LoggerFactory.getLogger(Handler.class);

	private Transaction transaction;
	private TxParams txParams;
	private Account sender;
	private String url;

	public Handler(Account account, String url) {
		this.sender = account;
		this.txParams = new TxParams();
		this.url = url;
	}

	private void setShardIDs(int fromShard, int toShard) throws Exception {
		List<RPCRoutes> shards = Sharding.getShardingStructure();
		if (!Sharding.validateShardIDs(fromShard, toShard, shards.size())) {
			throw new IllegalArgumentException("Invalid shard ids passed");
		}
		this.txParams.setFromShard(fromShard);
		this.txParams.setToShard(toShard);
	}

	private long computeIntrinsicGas(byte[] data, boolean contractCreation, boolean homestead) throws Exception {
		long gas;
		if (contractCreation && homestead) {
			gas = 53000; // constant per transaction that creates a contract
		} else {
			gas = 21000; // Per transaction not creating a contract
		}
		// Bump the required gas by the amount of transactional data
		if (data.length > 0) {
			// Zero and non-zero bytes are priced differently
			long nz = 0;
			for (byte b : data) {
				if (b != 0) {
					nz++;
				}
			}
			// Make sure we don't exceed uint64 for all data combinations
			if ((Long.MAX_VALUE - gas) / 68 < nz) {
				throw new Exception("out of gas");
			}
			gas += nz * 68; // TxDataNonZeroGas = 68

			long z = data.length - nz;
			if ((Long.MAX_VALUE - gas) / 68 < z) {
				throw new Exception("out of gas");
			}
			gas += z * 68; // TxDataNonZeroGas = 68
		}

		return gas;
	}

	private void setIntrinsicGas(String payload) throws Exception {
		byte[] data = Base64.getDecoder().decode(payload);
		long gas = computeIntrinsicGas(data, false, true);
		txParams.setGas(gas);
	}

	private void setAmount(double amount) {
		BigInteger nano = BigInteger.TEN.pow(9);
		double nanoAmount = nano.doubleValue() * amount;
		BigInteger amt = new BigDecimal(nanoAmount).toBigInteger().multiply(nano);
		txParams.setTransferAmount(amt.doubleValue());
	}

	private void verifyBalance(double amount) throws Exception {
		HmyResponse response = new RPC(this.url).getBalance(this.sender.getAddress().getOneAddr()).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}

		BigInteger nano = BigInteger.TEN.pow(9);

		BigInteger balance = Numeric.toBigInt(response.getResult());
		// new BigInteger(Numeric.prependHexPrefix(), 16).divide(nano);

		double nanoAmount = nano.doubleValue() * amount;
		BigInteger transfer = new BigDecimal(nanoAmount).toBigInteger();

		double bln = balance.divide(nano).doubleValue();
		double tns = transfer.divide(nano).doubleValue();

		if (tns > bln) {
			throw new Exception(
					String.format("current balance of %lf is not enough for the requested transfer %lf", bln, tns));
		}
	}

	private void setReceiver(String receiver) {
		if (Address.isOneAddr(receiver)) {
			txParams.setReceiver(Address.parseBech32(receiver));
		} else {
			txParams.setReceiver(receiver);
		}
	}

	private void setGasPrice() {
		txParams.setGasPrice(1);
	}

	private void setNextNonce() throws Exception {
		RPC rpc = new RPC(this.url);
		HmyResponse response = rpc.getTransactionCount(sender.getAddress().getHexAddr()).send();
		if (response.hasError()) {
			throw new Exception();
		}
		BigInteger nonce = Numeric.toBigInt(response.getResult());
		this.txParams.setNonce(nonce.longValue());
	}

	private void setNewTransactionWithDataAndGas(String payload, double amount, long gasPrice) {
		BigInteger nano = BigInteger.TEN.pow(9);
		double nanoAmount = nano.doubleValue() * amount;
		BigInteger amt = new BigDecimal(nanoAmount).toBigInteger().multiply(nano);
		BigInteger gas = BigInteger.valueOf(gasPrice).multiply(nano);

		this.transaction = new Transaction(txParams.getNonce(), txParams.getReceiver(), txParams.getFromShard(),
				txParams.getToShard(), amt, txParams.getGas(), gas, payload.getBytes());
	}

	private void signAndPrepareTxEncodedForSending(int chainId) throws JsonProcessingException {
		Transaction signedTransaction = this.transaction.sign(chainId, this.sender.getCredentials());
		log.info(String.format("signed transaction with chainId %d", chainId));
		ObjectMapper mapper = new ObjectMapper();
		log.info(mapper.writeValueAsString(signedTransaction));
	}

	private void sendSignedTx() throws Exception {
		HmyResponse response = new RPC(this.url).sendRawTransaction(new String(this.transaction.getRawHash())).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}
		this.transaction.setTxHash(response.getResult());
	}

	private void txConfirm(int waitToConfirmTime) throws Exception {
		if (waitToConfirmTime > 0) {
			int start = waitToConfirmTime;
			for (;;) {
				if (start < 0) {
					return;
				}
				HmyResponse response = new RPC(this.url).getTransactionReceipt(this.transaction.getTxHash()).send();
				if (response.hasError()) {
					throw new Exception(response.getError().getMessage());
				}
				if (response.getResult() != null) {
					log.info("received transaction confirmation, ", response.getResult());
					return;
				}
				Thread.sleep(2000); // 2 seconds sleep
				start = start - 2;
			}
		}
	}

	public String execute(int chainId, String receiver, String payload, double amount, long gasPrice, int fromShard,
			int toShard, boolean dryRun, int waitToConfirmTime) throws Exception {
		setShardIDs(fromShard, toShard);
		setIntrinsicGas(payload);
		setAmount(amount);
		verifyBalance(amount);
		setReceiver(receiver);
		setGasPrice();
		setNextNonce();
		setNewTransactionWithDataAndGas(payload, amount, gasPrice);
		signAndPrepareTxEncodedForSending(chainId);
		if (!dryRun) {
			sendSignedTx();
			txConfirm(waitToConfirmTime);
		}
		return this.transaction.getTxHash();
	}
}
