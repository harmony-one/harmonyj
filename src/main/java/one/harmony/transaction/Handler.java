package one.harmony.transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import one.harmony.account.Account;
import one.harmony.account.Address;
import one.harmony.keys.Store;
import one.harmony.rpc.HmyResponse;
import one.harmony.rpc.RPC;
import one.harmony.rpc.ShardingStructure.RPCRoutes;
import one.harmony.rpc.TransactionReceiptResponse;
import one.harmony.sharding.Sharding;

/**
 * Transaction handler class
 * 
 * @author gupadhyaya
 *
 */
public class Handler {
	private static final Logger log = LoggerFactory.getLogger(Handler.class);
	private static final BigInteger NANO = BigInteger.TEN.pow(9);
	private static final BigInteger ONE = NANO.multiply(NANO);
	private static final BigDecimal DECIMAL_ONE = new BigDecimal(ONE);
	private static final int DEFAULT_WAIT_TIME = 40;
	private static final long DEFAULT_NONCE = -1;
	private static final long DEFAULT_GAS = 21000;
	private static final String DEFAULT_FROM = "0x0000000000000000000000000000000000000000";

	private Transaction transaction;
	private TxParams txParams;
	private Account sender;
	private String from;
	private String url;
	private RPC rpc;
	private int chainID;

	public Handler(Account account, String url) {
		this.sender = account;
		this.txParams = new TxParams();
		this.url = url;
		this.rpc = new RPC(this.url);
	}

	public Handler(String from, String passphrase, String node, int chainID) throws Exception {
		this.from = from;
		this.txParams = new TxParams();
		if (node != null) {
			this.url = node;
		} else {
			List<RPCRoutes> shards = Sharding.getShardingStructure();
			this.url = Sharding.getHandlerFor(shards, 0);
		}
		this.rpc = new RPC(this.url);
		String accountName = Store.getAccountNameFromAddress(from);
		boolean isHex = false;
		Address address = new Address(from, isHex);
		WalletFile walletFile = Store.extractWalletFileFromAddress(from);
		Credentials credentials = Credentials.create(Wallet.decrypt(passphrase, walletFile));
		Account account = new Account(accountName, address, credentials, walletFile);
		this.sender = account;
		this.chainID = chainID;
	}

	public Handler(String node, int chainID) throws Exception {
		this.txParams = new TxParams();
		if (node != null) {
			this.url = node;
		} else {
			List<RPCRoutes> shards = Sharding.getShardingStructure();
			this.url = Sharding.getHandlerFor(shards, 0);
		}
		this.rpc = new RPC(this.url);
		this.chainID = chainID;
	}

	public void setChain(int chainID) {
		this.chainID = chainID;
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
			if ((Long.MAX_VALUE - gas) / 4 < z) {
				throw new Exception("out of gas");
			}
			gas += z * 4; // TxDataZeroGas = 4
		}

		return gas;
	}

	private void setIntrinsicGas(String payload, long provided) throws Exception {
		byte[] data = payload.getBytes(); // Base64.getDecoder().decode(payload);
		long gas = Math.max(computeIntrinsicGas(data, false, true), provided);
		txParams.setGas(gas);
	}

	private void setAmount(String amount) {
		txParams.setTransferAmount(amount);
	}

	private void verifyBalance(String amount) throws Exception {
		if (this.rpc == null) {
			this.rpc = new RPC(this.url);
		}
		HmyResponse response = this.rpc.getBalance(this.sender.getAddress().getOneAddr()).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}

		BigInteger balance = Numeric.toBigInt(response.getResult());

		BigDecimal amt = new BigDecimal(amount).multiply(DECIMAL_ONE);
		BigInteger transfer = amt.toBigInteger();

		double tns = transfer.divide(ONE).doubleValue();
		double bln = balance.divide(ONE).doubleValue();

		if (transfer.compareTo(balance) > 0) {
			throw new Exception(
					String.format("current balance of %f is not enough for the requested transfer %f", bln, tns));
		}
	}

	private void setReceiver(String receiver) {
		if (receiver == null) { // contract
			txParams.setReceiver(receiver);
			return;
		}
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
		if (this.rpc == null) {
			this.rpc = new RPC(this.url);
		}
		HmyResponse response = this.rpc.getTransactionCount(sender.getAddress().getHexAddr()).send();
		if (response.hasError()) {
			throw new Exception();
		}
		BigInteger nonce = Numeric.toBigInt(response.getResult());
		this.txParams.setNonce(nonce.longValue());
	}

	private void setNewTransactionWithDataAndGas(long nonce, String payload, boolean isHex, String amount,
			long gasPrice) throws Exception {
		BigInteger amt = new BigDecimal(amount).multiply(DECIMAL_ONE).toBigInteger();
		BigInteger gas = BigInteger.valueOf(gasPrice).multiply(NANO);

		long nextNonce = txParams.getNonce();
		if (nonce != -1) {
			nextNonce = nonce;
		}

		byte[] data;
		if (isHex) {
			data = Numeric.hexStringToByteArray(payload);
		} else {
			data = payload.getBytes();
		}
		this.transaction = new Transaction(getFromAddress(), nextNonce, txParams.getReceiver(), txParams.getFromShard(),
				txParams.getToShard(), amt, txParams.getGas(), gas, data);
	}

	private void signAndPrepareTxEncodedForSending(int chainId) throws JsonProcessingException {
		Transaction signedTransaction = this.transaction.sign(chainId, this.sender.getCredentials());
		log.info(String.format("signed transaction with chainId %d", chainId));
		ObjectMapper mapper = new ObjectMapper();
		log.info(mapper.writeValueAsString(signedTransaction));
	}

	private void sendSignedTx() throws Exception {
		if (this.rpc == null) {
			this.rpc = new RPC(this.url);
		}
		HmyResponse response = this.rpc.sendRawTransaction(new String(this.transaction.getRawHash())).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}
		this.transaction.setTxHash(response.getResult());
	}

	private TransactionReceipt txConfirm(int waitToConfirmTime) throws Exception {
		if (this.rpc == null) {
			this.rpc = new RPC(this.url);
		}
		if (waitToConfirmTime > 0) {
			int start = waitToConfirmTime;
			for (;;) {
				if (start < 0) {
					log.error("could not fetch receipt for transaction with hash: %s", this.transaction.getTxHash());
					return null;
				}
				TransactionReceiptResponse response = this.rpc.getTransactionReceipt(this.transaction.getTxHash())
						.send();
				if (response.hasError()) {
					throw new Exception(response.getError().getMessage());
				}
				if (response.getReceipt() != null) {
					log.info(String.format("received transaction confirmation: %s", response.getReceipt()));
					return response.getReceipt();
				}
				Thread.sleep(2000); // 2 seconds sleep
				start = start - 2;
			}
		}
		return null;
	}

	private String getFromAddress() throws Exception {
		if (this.sender != null) {
			return this.sender.getAddress().getHexAddr();
		} else if (this.from != null) {
			return new Address(this.from, false).getHexAddr();
		} else {
			return DEFAULT_FROM;
		}
	}

	public HmyResponse getCode(String contractAddress, DefaultBlockParameter defaultBlockParameter) throws IOException {
		return this.rpc.getCode(contractAddress, defaultBlockParameter).send();
	}

	public String call(String to, String data, DefaultBlockParameter defaultBlockParameter, BigInteger gasLimit)
			throws Exception {
		CallArgs args = new CallArgs();
		args.from = getFromAddress();
		args.to = to;
		args.data = data;
		long gas = Math.max(computeIntrinsicGas(data.getBytes(), false, true), gasLimit.longValue());
		args.gas = Numeric.encodeQuantity(BigInteger.valueOf(gas));
		BigInteger gasPrice = BigInteger.valueOf(1).multiply(NANO);
		args.gasPrice = Numeric.encodeQuantity(gasPrice);
		args.value = Numeric.encodeQuantity(BigInteger.ZERO);

		HmyResponse response = this.rpc.call(args, defaultBlockParameter).send();
		if (response.hasError()) {
			throw new Exception(response.getError().getMessage());
		}
		return response.getResult();
	}

	public TransactionReceipt send(String to, String data, BigInteger value, BigInteger gasPrice, BigInteger gasLimit,
			boolean constructor) throws Exception {
		String amount = value.toString();
		setShardIDs(0, 0);
		setIntrinsicGas(data, gasLimit.longValue()); //
		setAmount(amount);
		verifyBalance(amount);
		setReceiver(to);
		setGasPrice();
		setNextNonce();
		setNewTransactionWithDataAndGas(DEFAULT_NONCE, data, true, amount, gasPrice.longValue());
		signAndPrepareTxEncodedForSending(this.chainID);
		sendSignedTx();
		return txConfirm(DEFAULT_WAIT_TIME);
	}

	public String execute(int chainId, long nonce, String receiver, String payload, String amount, long gas, long gasPrice,
			int fromShard, int toShard, boolean dryRun, int waitToConfirmTime) throws Exception {
		setShardIDs(fromShard, toShard);
		setIntrinsicGas(payload, gas);
		setAmount(amount);
		verifyBalance(amount);
		setReceiver(receiver);
		setGasPrice();
		setNextNonce();
		setNewTransactionWithDataAndGas(nonce, payload, false, amount, gasPrice);
		signAndPrepareTxEncodedForSending(chainId);
		if (!dryRun) {
			sendSignedTx();
			txConfirm(waitToConfirmTime);
		} else {
			log.info(new String(this.transaction.getRawHash()));
		}
		return this.transaction.getTxHash();
	}
}
