package one.harmony.transaction;

public class TxParams {

	private long nonce;
	private int fromShard;
	private int toShard;
	private long gas;
	private String transferAmount;
	private String receiver;
	private long gasPrice;

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public int getFromShard() {
		return fromShard;
	}

	public void setFromShard(int fromShard) {
		this.fromShard = fromShard;
	}

	public int getToShard() {
		return toShard;
	}

	public void setToShard(int toShard) {
		this.toShard = toShard;
	}

	public long getGas() {
		return gas;
	}

	public void setGas(long gas) {
		this.gas = gas;
	}

	public String getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public long getGasPrice() {
		return gasPrice;
	}

	public void setGasPrice(long gasPrice) {
		this.gasPrice = gasPrice;
	}

}
