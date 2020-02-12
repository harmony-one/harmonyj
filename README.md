# Harmony Java SDK

HarmonyJ is a light-weight Java library for interacting with [Harmony](https://harmony.one) blockchain. For high-level information about Harmony and its goals, visit [harmony.one](https://harmony.one). 
The [harmony white paper](https://harmony.one/pdf/whitepaper.pdf) provides a complete conceptual overview.

## Features
* Partial implementation of Harmony's JSON-RPC client API over HTTP with features
* Local key management	
* Accounts and getting balance
* Creating, signing, and sending transactions

Dependencies
* [web3j](https://github.com/web3j/web3j/)
* [bitcoinj](https://github.com/bitcoinj/bitcoinj)
* Java 8 and Gradle 5.6.4

## Getting started

To get started, it is best to have the latest JDK and Gradle installed.

### Installation
Add the following Maven dependency to your project's `pom.xml`:
```
<dependency>
  <groupId>one.harmony</groupId>
  <artifactId>harmonyj</artifactId>
  <version>1.0.11</version>
</dependency>
```

### Building from the command line

To perform a full build (including JavaDocs and unit/integration tests) use JDK 8+

```
gradle clean build
```

To perform a full build without unit/integration tests use:

```
gradle clean assemble
```

### Specifying configuration properties

HarmonyJ reads the below listed user specified properties from `{user.home}/hmy-config.properties` file.
* `node` Harmony URL to connect to
* `keystore.dir` The keystore directory path. This should be absolute path.
* `accounts.dir` The accounts directory alias where the accounts and key files are stored.
* `passphrase` The default passphrase to use   
* `mnemonics.file.path` The path to mnemonics file that stores the generated mnemonics

An example hmy-config.properties file looks like:

```
node=http://localhost:9500/
keystore.dir=/Users/john/kestore.local
accounts.dir=accounts-keys
passphrase=harmony-one
mnemonics.file.path=/Users/john/kestore.local/mnemonics.txt
```

Another way to pass the configuration parameters is using the `Config.setConfigParameters` method.

```
import one.harmony.common.Config;

public class Test {
	public static void main(String[] args) throws Exception {
		String nodeUrl = "http://localhost:9500";
		String keystoreDir = "/Users/john/mystore";
		String accountsDir = "accounts-keys";
		String defaultPassPhrase = "harmony-one";
		Config.setConfigParameters(nodeUrl, keystoreDir, accountsDir, defaultPassPhrase);
	}
}
```

If the user wants to specify an absolute file path to store the generated mnemonics:
```
		...
		String mnemonicsFilePath = keystoreDir + "/mnemonics.txt";
		Config.setConfigParameters(nodeUrl, keystoreDir, accountsDir, defaultPassPhrase, mnemonicsFilePath);
```

### Generating Javadoc

HarmonyJ provides information about SDK classes and methods also in the form of `javadoc` that can be generated using gradle.

```
gradle javadoc
```

## Examples

The key API classes can be found under `one.harmony.cmd` are:
1. [Blockchain](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Blockchain.java)
2. [Balance](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Balance.java)
3. [Keys](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Keys.java)
4. [Transfer](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Transfer.java)


### Blockchain

Currently, the `Blockchain` class only provides an api for fetching the Harmony protocol version. In future, this class will support more functionalities like querying block information, etc.
The example shown below retrieves the protocol version. 

```
import one.harmony.cmd.Blockchain;

public void getProtocolVersion() {
	String protocol = Blockchain.getProtocolVersion();
}
```
Blockchain class can be used to send raw transactions.
```
import one.harmony.cmd.Blockchain;

public static void main(String[] args) throws Exception {
	String nodeUrl = "http://localhost:9500";
	String rawTransaction = "0x...";
	String txHash = Blockchain.sendRawTransaction(nodeUrl, rawTransaction);
}

```

### Balance

The `Balance` class provides an api for checking the balance of a Harmony account using the Harmony's one address. The `check` method retrieves balances on all shards (note that, Harmony is a sharded blockchain).
The example below retrieves the balance of a localnet account.

```
import one.harmony.cmd.Balance;

public void queryBalance() throws Exception {
	String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
	Balance.check(oneAddress);
}
```

The sample output is:


```
[
	{ 
		"shard": 0, 
		"amount": 15926.97359095238 
	},
	{ 
		"shard": 1, 
		"amount": 0.0 
	}
]
```

For checking the balance using your own Harmony node:

```
import one.harmony.cmd.Balance;

public void queryBalance() throws Exception {
	String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
	Balance.checkLocal(oneAddress);
}
```
The sample output is: `206.1904761904762`


### Transfer

The transfer class provides functionality to transfer funds between any two Harmony accounts. The key api's are `Transfer` constructor and `execute` method. The `execute` method returns the transaction hash of the transfer. To check that transfer is committed to the blockchain, increase the `waitToConfirmTime` in seconds. 
An example is shown below:

```
import one.harmony.cmd.Transfer;

public void testTransfer() throws Exception {
	String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy"; // Harmony localnet addresses
	String to = "one1pf75h0t4am90z8uv3y0dgunfqp4lj8wr3t5rsp"; // Harmony localnet addresses
	String amount = "50.714285714";
	int fromShard = 0;
	int toShard = 1;
	boolean dryRun = false;
	int waitToConfirmTime = 0;
	String passphrase = "harmony-one";
	String memo = "0x5061796d656e7420666f722078797a";
	Transfer t = new Transfer(from, to, amount, fromShard, toShard, memo);
	t.prepare(passphrase); // prepare transfer locally, before connecting to the network
	String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime); // needs connection to the network
}
```

For the transfer to work, the `from` address account key must exists in the local keystore. If not, add the key using the key management apis.
The transfer automatically creates a transaction, encodes it, signs the transaction using `from` address's key, and sends it to the blockchain. 

For performing transfers using Harmony node use:
```
String nodeUrl ; "http://127.0.0.1:9500";
t.prepare(passphrase, nodeUrl);
String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime)
```

### Keys

The `Keys` class provides the local key management functionality such as creating new account or adding a key, importing/exporting a private key or keystore, listing local accounts, etc. 

#### Add key

Multiple addKey apis are provided. The `addKey` api returns the Harmony one address of the created account. 

```
public static String addKey(String accountName, String passphrase, String mnemonic);
public static String addKey(String accountName, String passphrase);
public static String addKey(String accountName);
```

An example:

```
import one.harmony.cmd.Keys;

String mnemonics = ""; // long string containing words
String oneAddress = Keys.addKey("a2", "harmony", mnemonics);
```

#### Import private key

To create an account using a private key, use `importPrivateKey` api. This api returns the Harmony one address of the created account.

```
public static String importPrivateKey(String secp256k1PRV, String accountName, String passphrase);
public static String importPrivateKey(String secp256k1PRV, String accountName);
```

An example:

```
import one.harmony.cmd.Keys;

String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3"; // dummy private key
String accountName = "a1";
String oneAddress = Keys.importPrivateKey(key, accountName);
```

Sample output:

```
one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy
```

#### Import keystore

To import a keystore file (`json` file describing the keystore) as shown below, use `importKeyStore` api and pass the account name.

```
{
    "address":"",
    "id":"",
    "version":3,
    "crypto":{
    	...
    }
}
```

```
public static String importKeyStore(String keyFilePath, String accountName);
public static String importKeyStore(String keyFilePath, String accountName, String passphrase);
```

#### Export private key

```
public static String exportPrivateKeyFromAddress(String oneAddress, String passphrase);
public static String exportPrivateKeyFromAddress(String oneAddress);
public static String exportPrivateKeyFromAccountName(String accountName, String passphrase);
public static String exportPrivateKeyFromAccountName(String accountName);
```


#### Export keystore

For exporting the keystore file `exportKeyStore...` apis can be used. Multiple variations of the `exportKeyStore...` api are provided.

```

public static String exportKeyStoreFromAddress(String oneAddress, String passphrase);
public static String exportKeyStoreFromAddress(String oneAddress);
public static String exportKeyStoreFromAccountName(String accountName, String passphrase);
public static String exportKeyStoreFromAccountName(String accountName);
```


#### List local accounts

The list all local account information use `listAccounts` api.

```
import one.harmony.Keys;

Map<String, String> accountInfo = Keys.listAccounts();
```
The output will display account names and Harmony one addresses for all the local accounts.

#### Get key location

To retrieve the local keystore path use `getKeysLocation` api. 

```
import one.harmony.cmd.Keys;

String keydir = Keys.getKeysLocation();
```

#### Change local account name

The `setAccountName(oneAddress, accountName)` lets users to change the account name associated with a local account with Harmony one address.

```
import one.harmony.cmd.Keys;

String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
boolean status = Keys.setAccountName(oneAddress, "local-account-2");
```

#### Clean keystore

The `cleanKeyStore()` api wipes out the keystore and permanently deletes all the local account informations.

```
import one.harmony.cmd.Keys;

Keys.cleanKeyStore();
```

For details about all the APIs, refer to javadocs.
