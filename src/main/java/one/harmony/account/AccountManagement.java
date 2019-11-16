package one.harmony.account;

import java.util.HashMap;
import java.util.Map;

/**
 * AccountManagement class provides easier way to access local accounts instead
 * of every time reading from the local keystore which is I/O expensive (to be
 * implemented).
 * 
 * @author gupadhyaya
 *
 */
public class AccountManagement {
	private static final Map<String, Account> accounts = new HashMap<String, Account>();

	public AccountManagement() {

	}

	public static Account getAccount(String name) {
		return accounts.get(name);
	}

	public static void addAccount(Account account) {
		if (!accounts.containsKey(account.getName())) {
			accounts.put(account.getName(), account);
		}
	}

	public static void removeAccount(Account account) {
		accounts.remove(account.getName());
	}

	public static void clear() {
		accounts.clear();
	}

}
