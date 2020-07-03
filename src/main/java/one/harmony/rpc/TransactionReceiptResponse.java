package one.harmony.rpc;

import java.io.IOException;

import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;

public class TransactionReceiptResponse extends Response<TransactionReceipt> {

	public TransactionReceipt getReceipt() {
		return getResult();
	}

	public static class ResponseDeserialiser extends JsonDeserializer<TransactionReceipt> {

		private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

		@Override
		public TransactionReceipt deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException {
			if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
				return objectReader.readValue(jsonParser, TransactionReceipt.class);
			} else {
				return null; // null is wrapped by Optional in above getter
			}
		}
	}
}
