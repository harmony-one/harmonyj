package one.harmony.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.web3j.protocol.http.HttpService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public abstract class RequestTester {
	private OkHttpClient httpClient;
	private HttpService httpService;

	private RequestInterceptor requestInterceptor;

	@BeforeEach
	public void setUp() {
		requestInterceptor = new RequestInterceptor();
		httpClient = new OkHttpClient.Builder().addInterceptor(requestInterceptor).build();
		httpService = new HttpService(Config.DEFAULT_URL, httpClient);
		initWeb3Client(httpService);
	}

	protected abstract void initWeb3Client(HttpService httpService);

	protected void verifyResult(String expected) throws Exception {
		RequestBody requestBody = requestInterceptor.getRequestBody();
		assertNotNull(requestBody);
		assertEquals(requestBody.contentType(), (HttpService.JSON_MEDIA_TYPE));

		Buffer buffer = new Buffer();
		requestBody.writeTo(buffer);
		assertEquals(replaceRequestId(buffer.readUtf8()), (replaceRequestId(expected)));
	}

	private String replaceRequestId(String json) {
		return json.replaceAll("\"id\":\\d*}$", "\"id\":<generatedValue>}");
	}

	private class RequestInterceptor implements Interceptor {

		private RequestBody requestBody;

		@Override
		public okhttp3.Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			this.requestBody = request.body();

			okhttp3.Response response = chain.proceed(request);

			return response;
		}

		public RequestBody getRequestBody() {
			return requestBody;
		}
	}
}
