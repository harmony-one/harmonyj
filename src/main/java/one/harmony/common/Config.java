package one.harmony.common;

import java.io.IOException;
import java.util.Properties;

/**
 * Config class reads the user provided configurations for keystore location and
 * default passphrase.
 * 
 * @author gupadhyaya
 *
 */
public class Config {
	public static final String DEFAULT_URL = "http://localhost:9500/";
	public static final String DEFAULT_DIR_NAME = ".hmy_java";
	public static final String DEFAULT_ACCOUNT_ALIAS_DIR_NAME = "accounts-keys";
	public static final String DEFAULT_PASSPHRASE = "harmony-one";
	public static final int SECP256K1_PK_BYTES_LENGTH = 32;

	public static String node = DEFAULT_URL;
	public static String keystore = DEFAULT_DIR_NAME;
	public static String accounts = DEFAULT_ACCOUNT_ALIAS_DIR_NAME;
	public static String passphrase = DEFAULT_PASSPHRASE;

	static {
		try {
			String input = readNode();
			if (input != null) {
				node = input;
			}
			input = readDirName();
			if (input != null) {
				keystore = input;
			}
			input = readAccountAliasDirName();
			if (input != null) {
				accounts = input;
			}
			input = readPassphrase();
			if (input != null) {
				passphrase = input;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readNode() throws IOException {
		return loadProperties().getProperty("node");
	}

	public static String readDirName() throws IOException {
		return loadProperties().getProperty("keystore.dir");
	}

	public static String readAccountAliasDirName() throws IOException {
		return loadProperties().getProperty("accounts.dir");
	}

	public static String readPassphrase() throws IOException {
		return loadProperties().getProperty("passphrase");
	}

	/**
	 * Set the config parameters using the supplied inputs, overwrites the
	 * parameters read from hmy-config.properties
	 * 
	 * @param nodeUrl
	 * @param keystoreDir
	 * @param accountsDir
	 * @param defaultPassPhrase
	 */
	public static void setConfigParameters(String nodeUrl, String keystoreDir, String accountsDir,
			String defaultPassPhrase) {
		node = nodeUrl;
		keystore = keystoreDir;
		accounts = accountsDir;
		passphrase = defaultPassPhrase;
	}

	private static Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(Config.class.getResourceAsStream("/hmy-config.properties"));
		return properties;
	}

}
