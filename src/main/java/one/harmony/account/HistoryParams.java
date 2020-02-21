package one.harmony.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class helps to set the query parameters for getAccountTransactions api
 * Default values for parameters are: pageIndex = 0, pageSize = 1000, fullTx =
 * true (false for only transaction hashes), txType = ""|"ALL" (for including
 * everything, specify transaction type for filtering), order = "" (for
 * ascending, use DESC for descending)
 * 
 * @author gupadhyaya
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryParams {
	@JsonProperty("address")
	private String address;

	@JsonProperty("pageIndex")
	private int pageIndex = 0;

	@JsonProperty("pageSize")
	private int pageSize = 1000;

	@JsonProperty("fullTx")
	private boolean fullTx = true;

	@JsonProperty("txType")
	private String txType = "";

	@JsonProperty("order")
	private String order = "";

	/**
	 * Account address for which the transactions are queried must be specified
	 * 
	 * @param address
	 */
	public HistoryParams(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isFullTx() {
		return fullTx;
	}

	public void setFullTx(boolean fullTx) {
		this.fullTx = fullTx;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
