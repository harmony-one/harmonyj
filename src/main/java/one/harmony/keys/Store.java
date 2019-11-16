package one.harmony.keys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import one.harmony.account.Address;
import one.harmony.common.Config;

/**
 * Store class represents the local keystore and provides functionalities to
 * manage local keys.
 * 
 * @author gupadhyaya
 *
 */
public class Store {
	private static final Logger log = LoggerFactory.getLogger(Store.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		init();
	}

	private static void init() {
		File keysDir = new File(getDefaultKeyDirectory());
		if (!keysDir.exists()) {
			keysDir.mkdirs();
		}
	}

	public static File getKeyFileFromAccountName(String accountName) throws FileNotFoundException {
		String keysDirName = getDefaultKeyDirectory();
		File keysDir = new File(keysDirName);

		for (File file : keysDir.listFiles()) {
			if (file.isDirectory()) {
				if (file.getName().equals(accountName)) {
					return file.listFiles()[0];
				}
			}
		}
		throw new FileNotFoundException();
	}

	public static File getKeyFileFromAddress(String oneAddress) throws FileNotFoundException {
		String keysDirName = getDefaultKeyDirectory();
		File keysDir = new File(keysDirName);

		for (File file : keysDir.listFiles()) {
			if (file.isDirectory()) {
				File keyFile = file.listFiles()[0];
				if (keyFile.getName().equals(oneAddress + ".json")) {
					return keyFile;
				}
			}
		}
		throw new FileNotFoundException();
	}

	public static String getAccountNameFromAddress(String oneAddress) throws IllegalArgumentException {
		String keysDirName = getDefaultKeyDirectory();
		File keysDir = new File(keysDirName);

		for (File file : keysDir.listFiles()) {
			if (file.isDirectory()) {
				File keyFile = file.listFiles()[0];
				if (keyFile.getName().equals(oneAddress + ".json")) {
					return file.getName();
				}
			}
		}
		throw new IllegalArgumentException("Account does not exists for address: " + oneAddress);
	}

	public static Map<String, String> getLocalAccounts() {
		Map<String, String> accounts = new HashMap<String, String>();
		String keysDirName = getDefaultKeyDirectory();
		File keysDir = new File(keysDirName);

		for (File file : keysDir.listFiles()) {
			if (file.isDirectory()) {
				accounts.put(file.getName(), file.listFiles()[0].getName());
			}
		}
		return accounts;
	}

	public static boolean doesNamedAccountExists(String name) {
		return getLocalAccounts().containsKey(name);
	}

	public static boolean doesAccountExists(String oneAddress) {
		return getLocalAccounts().values().contains(oneAddress);
	}

	public static String searchForAccount(String oneAddress) throws IllegalArgumentException {
		return getAccountNameFromAddress(oneAddress);
	}

	public static String getDefaultKeyDirectory() {
		return getDefaultKeyDirectory(System.getProperty("os.name"));
	}

	public static File createAccountKeyDir(String accountName) {
		String accountDirName = String.format("%s%s%s", getDefaultKeyDirectory(), File.separator, accountName);
		File accountDir = new File(accountDirName);
		accountDir.mkdir();
		return accountDir;
	}

	public static boolean setAccountName(String oneAddress, String accountName) {
		File keysDir = new File(getDefaultKeyDirectory());
		for (File dir : keysDir.listFiles()) {
			if (dir.isDirectory()) {
				File addressFile = dir.listFiles()[0];
				if (addressFile.getName().equals(oneAddress)) {
					File newDir = new File(String.format("%s%s%s", dir.getParent(), File.separator, accountName));
					dir.renameTo(newDir);
				}
			}
		}
		return true;
	}

	private static String getDefaultKeyDirectory(String osName1) {
		String osName = osName1.toLowerCase();

		if (osName.startsWith("win")) {
			return String.format("%s%s%s%s%s", System.getenv("APPDATA"), File.separator, Config.keystore,
					File.separator, Config.accounts);
		} else {
			return String.format("%s%s%s%s%s", System.getProperty("user.home"), File.separator, Config.keystore,
					File.separator, Config.accounts);
		}
	}

	public static void generateWalletFile(String accountName, String password, ECKeyPair ecKeyPair)
			throws CipherException, IOException {
		WalletFile walletFile;
		walletFile = Wallet.createStandard(password, ecKeyPair);

		File destinationDirectory = createAccountKeyDir(accountName);

		String fileName = getWalletFileName(Address.toBech32(walletFile.getAddress()));
		File destination = new File(destinationDirectory, fileName);

		objectMapper.writeValue(destination, walletFile);
	}

	public static WalletFile extractWalletFileFromAccountName(String accountName)
			throws JsonParseException, JsonMappingException, IOException {
		File keyFile = getKeyFileFromAccountName(accountName);
		return objectMapper.readValue(keyFile, WalletFile.class);
	}

	public static WalletFile extractWalletFileFromAddress(String oneAddress)
			throws JsonParseException, JsonMappingException, IOException {
		File keyFile = getKeyFileFromAddress(oneAddress);
		return objectMapper.readValue(keyFile, WalletFile.class);
	}

	private static String usingBufferedReader(File file) {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				contentBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public static String extractKeyStoreFileFromAccountName(String accountName) throws FileNotFoundException {
		File keyFile = getKeyFileFromAccountName(accountName);
		return usingBufferedReader(keyFile);
	}

	public static String extractKeyStoreFileFromAddress(String oneAddress) throws FileNotFoundException {
		File keyFile = getKeyFileFromAddress(oneAddress);
		return usingBufferedReader(keyFile);
	}

	private static String getWalletFileName(String accountName) {
		return accountName + ".json";
	}

	public static void clean() throws IOException {
		File keysDir = new File(getDefaultKeyDirectory());
		FileUtils.deleteDirectory(keysDir);
	}
}
