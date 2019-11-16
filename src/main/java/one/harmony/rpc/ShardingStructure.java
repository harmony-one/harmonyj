package one.harmony.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.Response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import one.harmony.rpc.ShardingStructure.RPCRoutes;

public class ShardingStructure extends Response<List<RPCRoutes>> {

	@Override
	@JsonDeserialize(using = ShardingStructure.RPCRoutesDeserialiser.class)
	public void setResult(List<RPCRoutes> structure) {
		super.setResult(structure);
	}

	public List<RPCRoutes> getShardingResponse() {
		return getResult();
	}

	public static class RPCRoutes {
		private boolean current;
		private String http;
		private int shardID;
		private String ws;

		public RPCRoutes() {
		}

		public RPCRoutes(boolean current, String http, int shardID, String ws) {
			this.current = current;
			this.http = http;
			this.shardID = shardID;
			this.ws = ws;
		}

		public boolean isCurrent() {
			return current;
		}

		public void setCurrent(boolean current) {
			this.current = current;
		}

		public String getHttp() {
			return http;
		}

		public void setHttp(String http) {
			this.http = http;
		}

		public int getShardID() {
			return shardID;
		}

		public void setShardID(int shardID) {
			this.shardID = shardID;
		}

		public String getWs() {
			return ws;
		}

		public void setWs(String ws) {
			this.ws = ws;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			sb.append("\"current\":");
			sb.append(this.current);
			sb.append(",");
			sb.append("\"http\":\"");
			sb.append(this.http);
			sb.append("\",");
			sb.append("\"shardID\":");
			sb.append(this.shardID);
			sb.append(",");
			sb.append("\"ws\":\"");
			sb.append(this.ws);
			sb.append("\"}");
			return sb.toString();
		}
	}

	public static class RPCRoutesDeserialiser extends JsonDeserializer<List<RPCRoutes>> {

		private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

		@Override
		public List<RPCRoutes> deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			p.nextToken();
			List<RPCRoutes> rpcRoutes = new ArrayList<RPCRoutes>();
			Iterator<RPCRoutes> rpcRoutesIter = objectReader.readValues(p, RPCRoutes.class);
			while (rpcRoutesIter.hasNext()) {
				rpcRoutes.add(rpcRoutesIter.next());
			}

			return (List<RPCRoutes>) rpcRoutes;
		}

	}
}
