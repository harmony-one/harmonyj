package one.harmony.rpc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.Response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import one.harmony.rpc.AccountTransactionsResponse.AccountTransactions;

public class AccountTransactionsResponse extends Response<AccountTransactions> {
	@Override
	@JsonDeserialize(using = AccountTxsDeserialiser.class)
	public void setResult(AccountTransactions txs) {
		super.setResult(txs);
	}

	public static class AccountTransactions {
		private List<AccountTransaction> txs;

		public List<AccountTransaction> getTxs() {
			return txs;
		}

		@JsonDeserialize(using = AccountTxDeserialiser.class)
		public void setTransactions(List<AccountTransaction> txs) {
			this.txs = txs;
		}

		public void addTx(AccountTransaction tx) {
			this.txs.add(tx);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (txs.size() == 0) {
				return sb.toString();
			}
			sb.append("[\n");
			for (int i = 0; i < txs.size(); i++) {
				sb.append(txs.get(i).toString());
				if (i < txs.size() - 1) {
					sb.append(',');
				}
				sb.append('\n');
			}
			sb.append(']');
			return sb.toString();
		}

	}

	public static class AccountTransaction {
		private String blockHash;
		private String blockNumber;
		private String from;
		private String gas;
		private String gasPrice;
		private String hash;
		private String input;
		private String nonce;
		private String r;
		private String s;
		private int shardID;
		private String timestamp;
		private String to;
		private int toShardID;
		private String transactionIndex;
		private String v;
		private String value;

		public String getBlockHash() {
			return blockHash;
		}

		public void setBlockHash(String blockHash) {
			this.blockHash = blockHash;
		}

		public String getBlockNumber() {
			return blockNumber;
		}

		public void setBlockNumber(String blockNumber) {
			this.blockNumber = blockNumber;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getGas() {
			return gas;
		}

		public void setGas(String gas) {
			this.gas = gas;
		}

		public String getGasPrice() {
			return gasPrice;
		}

		public void setGasPrice(String gasPrice) {
			this.gasPrice = gasPrice;
		}

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public String getInput() {
			return input;
		}

		public void setInput(String input) {
			this.input = input;
		}

		public String getNonce() {
			return nonce;
		}

		public void setNonce(String nonce) {
			this.nonce = nonce;
		}

		public String getR() {
			return r;
		}

		public void setR(String r) {
			this.r = r;
		}

		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}

		public int getShardID() {
			return shardID;
		}

		public void setShardID(int shardID) {
			this.shardID = shardID;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public int getToShardID() {
			return toShardID;
		}

		public void setToShardID(int toShardID) {
			this.toShardID = toShardID;
		}

		public String getTransactionIndex() {
			return transactionIndex;
		}

		public void setTransactionIndex(String tansactionIndex) {
			this.transactionIndex = tansactionIndex;
		}

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			if (this.blockHash == null) {
				sb.append("\"");
				sb.append(this.getHash());
				sb.append("\"");
			} else {
				sb.append("{");
				Field[] fields = this.getClass().getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field f = fields[i];
					sb.append('\n');
					sb.append("\"");
					sb.append(f.getName());
					sb.append("\"");
					sb.append(':');
					try {
						Object field = f.get(this);
						if (field instanceof Integer) {
							sb.append(((Integer) field).intValue());
						} else {
							sb.append("\"");
							sb.append(field);
							sb.append("\"");
						}
						if (i < fields.length - 1) {
							sb.append(',');
						}
					} catch (IllegalAccessException ex) {
						ex.printStackTrace();
					}
				}
				sb.append('\n');
				sb.append('}');
			}
			return sb.toString();
		}
	}

	public static class AccountTxsDeserialiser extends JsonDeserializer<AccountTransactions> {

		private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

		@Override
		public AccountTransactions deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			if (p.getCurrentToken() != JsonToken.VALUE_NULL) {
				return objectReader.readValue(p, AccountTransactions.class);
			} else {
				return null;
			}
		}
	}

	public static class AccountTxDeserialiser extends JsonDeserializer<List<AccountTransaction>> {

		private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

		@Override
		public List<AccountTransaction> deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonToken nextToken = p.nextToken();
			List<AccountTransaction> txs = new ArrayList<AccountTransaction>();
			if (nextToken == JsonToken.START_OBJECT) {
				Iterator<AccountTransaction> txsIter = objectReader.readValues(p, AccountTransaction.class);
				while (txsIter.hasNext()) {
					txs.add(txsIter.next());
				}
			} else if (nextToken == JsonToken.VALUE_STRING) {
				Iterator<String> txsIter = objectReader.readValues(p, String.class);
				while (txsIter.hasNext()) {
					String txHash = txsIter.next();
					AccountTransaction accountTx = new AccountTransaction();
					accountTx.setHash(txHash);
					txs.add(accountTx);
				}

			}

			return (List<AccountTransaction>) txs;
		}

	}
}
