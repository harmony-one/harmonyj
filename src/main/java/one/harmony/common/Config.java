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
	public static final String DEFAULT_DIR_NAME = ".hmy_java";
	public static final String DEFAULT_ACCOUNT_ALIAS_DIR_NAME = "accounts-keys";
	public static final String DEFAULT_PASSPHRASE = "harmony-one";
	public static final int SECP256K1_PK_BYTES_LENGTH = 32;

	public static String readDirName() throws IOException {
		return loadProperties().getProperty("keystore.dir");
	}

	public static String readAccountAliasDirName() throws IOException {
		return loadProperties().getProperty("accounts.dir");
	}

	public static String readPassphrase() throws IOException {
		return loadProperties().getProperty("passphrase");
	}

	private static Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(Config.class.getResourceAsStream("/hmy-config.properties"));
		return properties;
	}

}
