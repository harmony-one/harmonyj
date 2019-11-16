package one.harmony.account;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletFile;

/**
 * Account class represents a Harmony account with account name, Harmony address
 * (one address + hex address), credentials (public/private keys), and keystore
 * file. The account class also provides the active account balance (to be
 * implemented) and the shardID of the balance.
 * 
 * @author gupadhyaya
 *
 */
public class Account {
	private String name;
	private Address address; // holds one and hex addresses
	private Credentials credentials; // holds ECKeyPairs (public, private)
	private WalletFile keyFile; // holds key store data
	private String balance;
	private int nonce = 0;
	private int shardID;

	public Account(String name, Address address, Credentials credentials, WalletFile walletFile) {
		this.name = name;
		this.address = address;
		this.credentials = credentials;
		this.keyFile = walletFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public WalletFile getKeyFile() {
		return keyFile;
	}

	public void setKeyFile(WalletFile keyFile) {
		this.keyFile = keyFile;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public int getShardID() {
		return shardID;
	}

	public void setShardID(int shardID) {
		this.shardID = shardID;
	}

}
