package one.harmony.common;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.http.HttpService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

public class ResponseTester {
	private HttpService web3jService;
	private OkHttpClient okHttpClient;
	private ResponseInterceptor responseInterceptor;

	@BeforeEach
	public void setUp() {
		responseInterceptor = new ResponseInterceptor();
		okHttpClient = new OkHttpClient.Builder().addInterceptor(responseInterceptor).build();
		configureWeb3Service(false);
	}

	protected void buildResponse(String data) {
		responseInterceptor.setJsonResponse(data);
	}

	protected void configureWeb3Service(boolean includeRawResponses) {
		web3jService = new HttpService(Config.DEFAULT_URL, okHttpClient, includeRawResponses);
	}

	protected <T extends Response> T deserialiseResponse(Class<T> type) {
		T response = null;
		try {
			response = web3jService.send(new Request(), type);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		return response;
	}

	private class ResponseInterceptor implements Interceptor {

		private String jsonResponse;

		public void setJsonResponse(String jsonResponse) {
			this.jsonResponse = jsonResponse;
		}

		@Override
		public okhttp3.Response intercept(Chain chain) throws IOException {

			if (jsonResponse == null) {
				throw new UnsupportedOperationException("Response has not been configured");
			}

			okhttp3.Response response = new okhttp3.Response.Builder()
					.body(ResponseBody.create(HttpService.JSON_MEDIA_TYPE, jsonResponse)).request(chain.request())
					.protocol(Protocol.HTTP_2).code(200).message("").build();

			return response;
		}
	}
}
