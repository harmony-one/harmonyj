package one.harmony.rpc;

import java.util.Arrays;
import java.util.Collections;

import org.web3j.protocol.core.Request;
import org.web3j.protocol.http.HttpService;

import one.harmony.common.Config;

public class RPC {
	private final HttpService service;

	public RPC() {
		this.service = new HttpService(Config.node);
	}

	public RPC(HttpService service) {
		this.service = service;
	}

	public RPC(String url) {
		this.service = new HttpService(url);
	}

	public RPC(String url, boolean includeRawResponse) {
		this.service = new HttpService(url, includeRawResponse);
	}

	public Request<?, HmyResponse> rpcClient() {
		return new Request<>("", Collections.<String>emptyList(), service, HmyResponse.class);
	}

	public Request<?, HmyResponse> sendRawTransaction(String raw) {
		return new Request<>(RPCMethod.SendRawTransaction, Arrays.asList(raw), service, HmyResponse.class);
	}

	public Request<?, ShardingStructure> getShardingStructure() {
		return new Request<>(RPCMethod.GetShardingStructure, Collections.<String>emptyList(), service,
				ShardingStructure.class);
	}

	public Request<?, HmyResponse> getBalance(String oneAddress) {
		return new Request<>(RPCMethod.GetBalance, Arrays.asList(oneAddress, "latest"), service, HmyResponse.class);
	}

	public Request<?, HmyResponse> getProtocolVersion() {
		return new Request<>(RPCMethod.ProtocolVersion, Arrays.asList("latest"), service, HmyResponse.class);
	}

	public Request<?, HmyResponse> getTransactionCount(String hexAddr) {
		return new Request<>(RPCMethod.GetTransactionCount, Arrays.asList(hexAddr, "pending"), service,
				HmyResponse.class);
	}

	public Request<?, HmyResponse> getTransactionReceipt(String txHash) {
		return new Request<>(RPCMethod.GetTransactionReceipt, Arrays.asList(txHash), service, HmyResponse.class);
	}
}
