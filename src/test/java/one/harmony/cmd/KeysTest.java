package one.harmony.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import one.harmony.keys.Store;

public class KeysTest {

	@Test
	public void testListAccounts() throws Exception {
		Store.clean();
		assertTrue(Keys.listAccounts().isEmpty());
	}

	@Test
	public void testGetKeysLocation() throws Exception {
		assertEquals(Keys.getKeysLocation(),
				String.format("%s%s%s", System.getProperty("user.home"), File.separator, ".hmy_java/accounts-keys"));
	}

	@Test
	public void testImportPrivateKey() throws Exception {
		String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String accountName = "a1";
		Keys.importPrivateKey(key, accountName);
	}

	@Test
	public void testAddKey() throws Exception {
		String mnemonics = "fragile pilot provide copper use average thrive forum behave inmate level account grape primary board cradle phone popular hello shuffle fault session window skin";
		Keys.addKey("a2", "harmony", mnemonics);
	}

	@Test
	public void testExportPrivateKey() throws Exception {
		String expected = "4fa9d77fe95fc698ffe4c9cd1b1393ef0713aee83ef576513e5ee4de6030bf5b";
		String actual = Keys.exportPrivateKeyFromAccountName("a3");
		assertEquals(expected, actual);
	}

	@Test
	public void testExportKeyStore() throws Exception {
		String expected = "{\"address\":\"02bef72d19705398f84927d795659446a4da5e80\",\"id\":\"639594f8-2c9c-46b9-88c0-1e39a3289f9c\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"b48c891ace2dbfb5d4fdd42340b9c0bbc33df62ad3acb8ea198e2c8eb7b66981\",\"cipherparams\":{\"iv\":\"e8610f647b00d97d7f921dfd7321a60e\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"83914844015b3d10c99455a45309b6b9e9576034123b52a38fc813b14aa1effc\"},\"mac\":\"61f38aa79452fd98cc16b3e25eb8727c2c4246256f8eba33844dcaf0fe6f30bc\"}}";
		String actual = Keys.exportKeyStoreFromAccountName("a1");
		assertEquals(expected, actual);
	}

//	@Test
//	public void testImportPrivateKey() throws Exception {
//		String key = "4fa9d77fe95fc698ffe4c9cd1b1393ef0713aee83ef576513e5ee4de6030bf5b";
//		String account = "a7";
//		Keys.importPrivateKey(key, account);
//	}

	@Test
	public void testCleanKeyStore() throws Exception {
		Keys.cleanKeyStore();
	}
}