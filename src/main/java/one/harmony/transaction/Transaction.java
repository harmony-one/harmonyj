package one.harmony.transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import one.harmony.rpc.RPC;

/**
 * Transaction class provides methods for Harmony transaction creation,
 * encoding, and signing.
 * 
 * @author gupadhyaya
 *
 */
public class Transaction {
	private String from;
	private BigInteger nonce;
	private BigInteger gasPrice;
	private BigInteger gasLimit;
	private int shardID;
	private int toShardID;
	private String recipient;
	private BigInteger amount;
	private byte[] payload;

	private SignatureData signature;

	protected byte[] rlpEncoded;

	private byte[] rawHash;

	private String txHash;

	public Transaction(String from, long nonce, String recipient, int shardID, int toShardID, BigInteger amount,
			long gasLimit, BigInteger gasPrice, byte[] payload) {
		this.from = from;
		this.nonce = BigInteger.valueOf(nonce);
		this.recipient = recipient;
		this.shardID = shardID;
		this.toShardID = toShardID;
		this.amount = amount;
		this.gasLimit = BigInteger.valueOf(gasLimit);
		this.gasPrice = gasPrice;
		this.payload = payload;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public void setNonce(BigInteger nonce) {
		this.nonce = nonce;
	}

	public BigInteger getGasPrice() {
		return gasPrice;
	}

	public void setGasPrice(BigInteger gasPrice) {
		this.gasPrice = gasPrice;
	}

	public BigInteger getGasLimit() {
		return gasLimit;
	}

	public void setGasLimit(BigInteger gasLimit) {
		this.gasLimit = gasLimit;
	}

	public int getShardID() {
		return shardID;
	}

	public void setShardID(int shardID) {
		this.shardID = shardID;
	}

	public int getToShardID() {
		return toShardID;
	}

	public void setToShardID(int toShardID) {
		this.toShardID = toShardID;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public BigInteger getAmount() {
		return amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public SignatureData getSignature() {
		return signature;
	}

	public void setSignature(SignatureData signature) {
		this.signature = signature;
	}

	public byte[] getRlpEncoded() {
		return rlpEncoded;
	}

	public void setRlpEncoded(byte[] rlpEncoded) {
		this.rlpEncoded = rlpEncoded;
	}

	public byte[] getRawHash() {
		return rawHash;
	}

	public void setRawHash(byte[] rawHash) {
		this.rawHash = rawHash;
	}

	public String getTxHash() {
		return txHash;
	}

	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}

	public Transaction sign(int chainId, Credentials credentials) {
		byte[] encoded = this.encode(chainId);
		SignatureData signatureData = signMessage(chainId, encoded, credentials.getEcKeyPair(), true);
		this.signature = signatureData;
		encoded = encode(signatureData);
		this.rlpEncoded = encoded;
		this.rawHash = Numeric.toHexString(encoded).getBytes();
		return this;
	}

	private static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	private byte[] encode(long chainId) {
		SignatureData signatureData = new SignatureData((byte) chainId, new byte[] {}, new byte[] {});
		return encode(signatureData);
	}

	private byte[] encode(SignatureData signatureData) {
		List<RlpType> values = asRlpValues(signatureData);
		RlpList rlpList = new RlpList(values);
		return RlpEncoder.encode(rlpList);
	}

	private List<RlpType> asRlpValues(SignatureData signatureData) {
		List<RlpType> result = new ArrayList<>();

		result.add(RlpString.create(this.getNonce()));
		result.add(RlpString.create(this.getGasPrice()));
		result.add(RlpString.create(this.getGasLimit()));
		result.add(RlpString.create(this.getShardID()));
		result.add(RlpString.create(this.getToShardID()));
		if (this.getRecipient() != null) {
			// skip contract deploy
			result.add(RlpString.create(Numeric.hexStringToByteArray(this.getRecipient())));
		} else {
			result.add(RlpString.create(""));
		}
		result.add(RlpString.create(this.getAmount()));
		result.add(RlpString.create(this.getPayload()));

		if (signatureData != null) {
			result.add(RlpString.create(signatureData.getV()));
			result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
			result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
		}

		return result;
	}

	public String sendRawTransaction() throws IOException {
		return new RPC().sendRawTransaction(rawHash.toString()).send().getJsonrpc();
	}

	/**
	 * Re-implements ethereum's signMessage to handle Harmony specific sign value
	 * extraction.
	 * 
	 * @param chainId
	 * @param message
	 * @param keyPair
	 * @param needToHash
	 * @return
	 */
	private static SignatureData signMessage(int chainId, byte[] message, ECKeyPair keyPair, boolean needToHash) {
		BigInteger publicKey = keyPair.getPublicKey();
		byte[] messageHash;
		if (needToHash) {
			messageHash = Hash.sha3(message);
		} else {
			messageHash = message;
		}

		ECDSASignature sig = keyPair.sign(messageHash);
		// Now we have to work backwards to figure out the recId needed to recover the
		// signature.
		int recId = -1;
		for (int i = 0; i < 4; i++) {
			BigInteger k = Sign.recoverFromSignature(i, sig, messageHash);
			if (k != null && k.equals(publicKey)) {
				recId = i;
				break;
			}
		}
		if (recId == -1) {
			throw new RuntimeException("Could not construct a recoverable key. Are your credentials valid?");
		}

		int headerByte = recId + 27;

		if (chainId != 0) {
			headerByte = 0;
			headerByte += 35;
			headerByte += chainId * 2;
			headerByte += recId;
		}

		// 1 header + 32 bytes for R + 32 bytes for S
		byte v = (byte) headerByte;
		byte[] r = Numeric.toBytesPadded(sig.r, 32);
		byte[] s = Numeric.toBytesPadded(sig.s, 32);

		return new SignatureData(v, r, s);
	}

}
