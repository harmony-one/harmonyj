package one.harmony.cmd;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import one.harmony.account.Address;
import one.harmony.common.Config;
import one.harmony.keys.Store;

/**
 * Keys class provides the local key management features such as adding a
 * account, import/export keys, delete keystore, etc.
 * 
 * @author gupadhyaya
 *
 */
public class Keys {

	private static final Logger log = LoggerFactory.getLogger(Keys.class);

	private static String generateMnemonic() {
		byte[] initialEntropy = new byte[32];
		new SecureRandom().nextBytes(initialEntropy);
		return MnemonicUtils.generateMnemonic(initialEntropy);
	}

	/**
	 * addKey method creates a new key in the local keystore, but requires
	 * mnemonics. If the accountName already exists or the supplied mnemonics is
	 * invalid then this method throws an IllegalArgumentException.
	 * 
	 * @param accountName
	 * @param passphrase
	 * @param mnemonic
	 * @return
	 */
	public static String addKey(String accountName, String passphrase, String mnemonic)
			throws CipherException, IllegalArgumentException, IOException {
		if (Store.doesNamedAccountExists(accountName)) {
			throw new IllegalArgumentException("Account already exists by name: " + accountName);
		}
		if (!MnemonicUtils.validateMnemonic(mnemonic)) {
			throw new IllegalArgumentException("Invalid mnemonic");
		}
		// byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase);
		// ECKeyPair ecKeyPair = ECKeyPair.create(Hash.sha256(seed));
		// Credentials.create(ecKeyPair);
		Credentials credentials = Store.loadBip44Credentials(passphrase, mnemonic);
		String address = credentials.getAddress();
		String oneAddress = Address.toBech32(address);
		Store.generateWalletFile(accountName, passphrase, credentials.getEcKeyPair());
		return oneAddress;
	}

	/**
	 * addKey method creates a new key in the keystore using the automatically
	 * generated mnemonics. The generated mnemonics is written to the log file.
	 * 
	 * @param accountName
	 * @param passphrase
	 * @return
	 */
	public static String addKey(String accountName, String passphrase)
			throws CipherException, IllegalArgumentException, IOException {
		String mnemonic = generateMnemonic();
		log.info(
				"**Important** write this seed phrase in a safe place, it is the only way to recover your account if you ever forget your password",
				mnemonic);
		return addKey(accountName, passphrase, mnemonic);
	}

	/**
	 * addKey method creates a new key in the keystore using the default passphrase
	 * (harmony-one) and the automatically generated mnemonics. The generated
	 * mnemonics is written to the log file.
	 * 
	 * @param accountName
	 * @return
	 */
	public static String addKey(String accountName) throws CipherException, IllegalArgumentException, IOException {
		String mnemonic = generateMnemonic();
		log.info(
				"**Important** write this seed phrase in a safe place, it is the only way to recover your account if you ever forget your password");
		log.info(mnemonic);
		return addKey(accountName, Config.passphrase, mnemonic);
	}

	/**
	 * importKeyStore method imports an existing keystore key using the provides
	 * path and account name, but using the default passphrase (harmony-one).
	 * 
	 * @param keyFilePath absolute path to key store file
	 * @param accountName
	 */
	public static String importKeyStore(String keyFilePath, String accountName)
			throws CipherException, IllegalArgumentException, IOException {
		return importKeyStore(keyFilePath, accountName, Config.passphrase);
	}

	/**
	 * importKeyStore method imports an existing keystore key using the provided
	 * path, account name, and passphrase. If the account name already exists, it
	 * will throw an IllegalArgumentException exception.
	 * 
	 * @param keyFilePath
	 * @param accountName
	 * @param passphrase
	 */
	public static String importKeyStore(String keyFilePath, String accountName, String passphrase)
			throws CipherException, IllegalArgumentException, IOException {
		if (Store.doesNamedAccountExists(accountName)) {
			throw new IllegalArgumentException("Account name exists");
		}
		Credentials credentials = WalletUtils.loadCredentials(passphrase, keyFilePath);
		String address = credentials.getAddress();
		String oneAddress = Address.toBech32(address);
		String searched = Store.searchForAccount(oneAddress);
		if (searched != "") {
			throw new IllegalArgumentException("Account name for the private key exists: " + searched);
		}
		Store.generateWalletFile(accountName, passphrase, credentials.getEcKeyPair());
		return oneAddress;
	}

	/**
	 * Import an existing keystore key (only accept secp256k1 private keys), account
	 * name, and passphrase
	 * 
	 * @param secp256k1PRV
	 * @param accountName
	 * @param passphrase
	 * @return
	 * @throws CipherException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static String importPrivateKey(String secp256k1PRV, String accountName, String passphrase)
			throws CipherException, IllegalArgumentException, IOException {
		if (Store.doesNamedAccountExists(accountName)) {
			throw new IllegalArgumentException("Account name exists");
		}
		ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(secp256k1PRV, 16));
		Credentials credentials = Credentials.create(ecKeyPair);
		String address = credentials.getAddress();
		String oneAddress = Address.toBech32(address);

		try {
			String searched = Store.searchForAccount(oneAddress);
			if (searched != "") {
				throw new IllegalArgumentException("Account name for the private key exists: " + searched);
			}
		} catch (Exception e) {

		}

		Store.generateWalletFile(accountName, passphrase, ecKeyPair);
		return oneAddress;
	}

	/**
	 * Import an existing keystore key (only accept secp256k1 private keys) and uses
	 * the default passphrase.
	 * 
	 * @param secp256k1PRV
	 * @param accountName
	 * @return
	 */
	public static String importPrivateKey(String secp256k1PRV, String accountName)
			throws CipherException, IllegalArgumentException, IOException {
		return importPrivateKey(secp256k1PRV, accountName, Config.passphrase);
	}

	/**
	 * Export the secp256k1 private key using the provided Harmony one address and
	 * passphrase.
	 * 
	 * @param oneAddress
	 * @param passphrase
	 * @throws CipherException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static String exportPrivateKeyFromAddress(String oneAddress, String passphrase)
			throws CipherException, JsonParseException, JsonMappingException, IOException {

		WalletFile walletFile = Store.extractWalletFileFromAddress(oneAddress);
		Credentials credentials = Credentials.create(Wallet.decrypt(passphrase, walletFile));
		return credentials.getEcKeyPair().getPrivateKey().toString(16);

	}

	/**
	 * Export the secp256k1 private key using the provided Harmony one address.
	 * 
	 * @param oneAddress
	 * @throws IOException
	 * @throws CipherException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static String exportPrivateKeyFromAddress(String oneAddress)
			throws JsonParseException, JsonMappingException, CipherException, IOException {
		return exportPrivateKeyFromAddress(oneAddress, Config.passphrase);
	}

	/**
	 * Export the secp256k1 private key using the provided account name and
	 * passphrase.
	 * 
	 * @param accountName
	 * @param passphrase
	 * @throws CipherException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static String exportPrivateKeyFromAccountName(String accountName, String passphrase)
			throws CipherException, JsonParseException, JsonMappingException, IOException {
		WalletFile walletFile = Store.extractWalletFileFromAccountName(accountName);
		Credentials credentials = Credentials.create(Wallet.decrypt(passphrase, walletFile));
		return credentials.getEcKeyPair().getPrivateKey().toString(16);
	}

	/**
	 * Export the secp256k1 private key using the provided account name and default
	 * passphrase.
	 * 
	 * @param accountName
	 * @throws IOException
	 * @throws CipherException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static String exportPrivateKeyFromAccountName(String accountName)
			throws JsonParseException, JsonMappingException, CipherException, IOException {
		return exportPrivateKeyFromAccountName(accountName, Config.passphrase);
	}

	/**
	 * Export the keystore file contents using the Harmony one address and provided
	 * passphrase.
	 * 
	 * @param oneAddress
	 * @param passphrase
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws CipherException
	 */
	public static String exportKeyStoreFromAddress(String oneAddress, String passphrase)
			throws JsonParseException, JsonMappingException, IOException, CipherException {
		WalletFile walletFile = Store.extractWalletFileFromAddress(oneAddress);
		Credentials.create(Wallet.decrypt(passphrase, walletFile));
		String content = Store.extractKeyStoreFileFromAddress(oneAddress);
		log.info(content);
		return content;
	}

	/**
	 * Export the keystore file contents using the Harmony one address and default
	 * passphrase.
	 * 
	 * @param oneAddress
	 * @throws CipherException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static String exportKeyStoreFromAddress(String oneAddress)
			throws JsonParseException, JsonMappingException, IOException, CipherException {
		return exportKeyStoreFromAddress(oneAddress, Config.passphrase);
	}

	/**
	 * Export the keystore file contents using the provided account name and
	 * passpharase.
	 * 
	 * @param accountName
	 * @param passphrase
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws CipherException
	 */
	public static String exportKeyStoreFromAccountName(String accountName, String passphrase)
			throws JsonParseException, JsonMappingException, IOException, CipherException {
		WalletFile walletFile = Store.extractWalletFileFromAccountName(accountName);
		Credentials.create(Wallet.decrypt(passphrase, walletFile));
		String content = Store.extractKeyStoreFileFromAccountName(accountName);
		return content;
	}

	/**
	 * Export the keystore file contents using the provided account name and default
	 * passphrase (harmony-one)
	 * 
	 * @param oneAddress
	 * @throws CipherException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static String exportKeyStoreFromAccountName(String accountName)
			throws JsonParseException, JsonMappingException, IOException, CipherException {
		return exportKeyStoreFromAccountName(accountName, Config.passphrase);
	}

	/**
	 * 
	 * @return map of account names and addresses
	 */
	public static Map<String, String> listAccounts() {
		return Store.getLocalAccounts();
	}

	/**
	 * getKeysLocation method helps with getting the local keystore location where
	 * the keys are stored.
	 * 
	 * @return keystore location path
	 */
	public static String getKeysLocation() {
		return Store.getDefaultKeyDirectory();
	}

	/**
	 * setAccountName method helps with changing the account name of an already
	 * existing account
	 * 
	 * @param oneAddress
	 * @param accountName
	 * @return boolean indicating the status
	 */
	public static boolean setAccountName(String oneAddress, String accountName) {
		Store.setAccountName(oneAddress, accountName);
		return true;
	}

	/**
	 * cleanKeyStore method clears the local keystore. All accounts will be
	 * permanently deleted.
	 * 
	 * @throws IOException
	 */
	public static void cleanKeyStore() throws IOException {
		Store.clean();
	}

}
