package one.harmony.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class KeysTest {

	@Test
	public void testListAccounts() throws Exception {
		Keys.cleanKeyStore();
		assertTrue(Keys.listAccounts().isEmpty());
	}

	@Test
	public void testGetDefaultKeysLocation() throws Exception {
		assertEquals(Keys.getKeysLocation(), String.format("%s", ".hmy_java/accounts-keys"));
	}

	@Test
	public void testImportPrivateKey() throws Exception {
		Keys.cleanKeyStore();
		String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String accountName = "a1";
		Keys.importPrivateKey(key, accountName);
	}

	@Test
	public void testAddKey() throws Exception {
		Keys.cleanKeyStore();
		String mnemonics = "fragile pilot provide copper use average thrive forum behave inmate level account grape primary board cradle phone popular hello shuffle fault session window skin";
		Keys.addKey("a2", "harmony", mnemonics);
	}

	@Test
	public void testExportPrivateKey() throws Exception {
		testImportPrivateKey();
		String expected = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String actual = Keys.exportPrivateKeyFromAccountName("a1");
		assertEquals(expected, actual);
	}

	@Test
	public void testCleanKeyStore() throws Exception {
		Keys.cleanKeyStore();
	}
}