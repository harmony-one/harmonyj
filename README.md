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
```xml
<dependency>
  <groupId>one.harmony</groupId>
  <artifactId>harmonyj</artifactId>
  <version>1.0.19</version>
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

```java
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
```java
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
3. [Transfer](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Transfer.java)
4. [Keys](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Keys.java)
5. [Contract](https://github.com/harmony-one/harmonyj/blob/master/src/main/java/one/harmony/cmd/Contract.java)


### Blockchain

Currently, the `Blockchain` class only provides an api for fetching the Harmony protocol version. In future, this class will support more functionalities like querying block information, etc.
The example shown below retrieves the protocol version. 

```java
import one.harmony.cmd.Blockchain;

public void getProtocolVersion() {
	String protocol = Blockchain.getProtocolVersion();
}
```
Blockchain class can be used to send raw transactions.
```java
import one.harmony.cmd.Blockchain;

public static void main(String[] args) throws Exception {
	String nodeUrl = "http://localhost:9500";
	String rawTransaction = "0x...";
	String txHash = Blockchain.sendRawTransaction(nodeUrl, rawTransaction);
}

```

The account transactions history can be queried as follows:

```java
import one.harmony.cmd.Blockchain;

public static void main(String[] args) throws Exception {
	// By default Config.node is used, however, a node url can be passed
	String node = "http://localhost:9500/";
	String address = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
	HistoryParams params = new HistoryParams(address);
	// For getting only transaction hashes, set `params.setFullTx(false)`
	System.out.println(getAccountTransactions(node, params));
}
```

The output with full transaction history will be: 
```
[
	{
		"blockHash":"0x57db6b6622c2fc6a047d372750c4f0bbf1745847b27848126290a5e0621b9a6c",
		"blockNumber":"0x18db22",
		"from":"one18n8e7472pg5fqvcfcr5hg0npquha24wsxmjheg",
		"gas":"0x33450",
		"gasPrice":"0x174876e800",
		"hash":"0x6193734698696eb8e33354a54493e0a760d5e00ef12710bd0847cddd09e85e9a",
		"input":"0x",
		"nonce":"0x1f",
		"r":"0xa00c7c12ae9ca9dbfbea0abc0fe9abc241bcd73caf9849e7194002f354c1088e",
		"s":"0x588dcfbb8fc354465d2ba6393a09b8ccb555ec647d97de639a106a071f755b77",
		"shardID":0,
		"timestamp":"0x5de4ca90",
		"to":"one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy",
		"toShardID":0,
		"transactionIndex":"0x0",
		"v":"0x26",
		"value":"0x16345785d8a0000"
	}, ...
]
```

The output with only transaction hashes:

```
[
	"0x6193734698696eb8e33354a54493e0a760d5e00ef12710bd0847cddd09e85e9a",
	"0xd53a22fdac3697d049f1a76c528da42502a1b4be9a37bf6e11ae5ef55d3672cd",
	...
]
```


### Balance

The `Balance` class provides an api for checking the balance of a Harmony account using the Harmony's one address. The `check` method retrieves balances on all shards (note that, Harmony is a sharded blockchain).
The example below retrieves the balance of a localnet account.

```java
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

```java
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

```java
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
```java
String nodeUrl ; "http://127.0.0.1:9500";
t.prepare(passphrase, nodeUrl);
String txHash = t.execute(LOCAL_NET, dryRun, waitToConfirmTime)
```

### Keys

The `Keys` class provides the local key management functionality such as creating new account or adding a key, importing/exporting a private key or keystore, listing local accounts, etc. 

#### Add key

Multiple addKey apis are provided. The `addKey` api returns the Harmony one address of the created account. 

```java
public static String addKey(String accountName, String passphrase, String mnemonic);
public static String addKey(String accountName, String passphrase);
public static String addKey(String accountName);
```

An example:

```java
import one.harmony.cmd.Keys;

String mnemonics = ""; // long string containing words
String oneAddress = Keys.addKey("a2", "harmony", mnemonics);
```

#### Import private key

To create an account using a private key, use `importPrivateKey` api. This api returns the Harmony one address of the created account.

```java
public static String importPrivateKey(String secp256k1PRV, String accountName, String passphrase);
public static String importPrivateKey(String secp256k1PRV, String accountName);
```

An example:

```java
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

```java
public static String importKeyStore(String keyFilePath, String accountName);
public static String importKeyStore(String keyFilePath, String accountName, String passphrase);
```

#### Export private key

```java
public static String exportPrivateKeyFromAddress(String oneAddress, String passphrase);
public static String exportPrivateKeyFromAddress(String oneAddress);
public static String exportPrivateKeyFromAccountName(String accountName, String passphrase);
public static String exportPrivateKeyFromAccountName(String accountName);
```


#### Export keystore

For exporting the keystore file `exportKeyStore...` apis can be used. Multiple variations of the `exportKeyStore...` api are provided.

```java
public static String exportKeyStoreFromAddress(String oneAddress, String passphrase);
public static String exportKeyStoreFromAddress(String oneAddress);
public static String exportKeyStoreFromAccountName(String accountName, String passphrase);
public static String exportKeyStoreFromAccountName(String accountName);
```


#### List local accounts

The list all local account information use `listAccounts` api.

```java
import one.harmony.Keys;

Map<String, String> accountInfo = Keys.listAccounts();
```
The output will display account names and Harmony one addresses for all the local accounts.

#### Get key location

To retrieve the local keystore path use `getKeysLocation` api. 

```java
import one.harmony.cmd.Keys;

String keydir = Keys.getKeysLocation();
```

#### Change local account name

The `setAccountName(oneAddress, accountName)` lets users to change the account name associated with a local account with Harmony one address.

```java
import one.harmony.cmd.Keys;

String oneAddress = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
boolean status = Keys.setAccountName(oneAddress, "local-account-2");
```

#### Clean keystore

The `cleanKeyStore()` api wipes out the keystore and permanently deletes all the local account informations.

```java
import one.harmony.cmd.Keys;

Keys.cleanKeyStore();
```

For details about all the APIs, refer to javadocs.

### Contract

Similar to Web3j, Harmonyj provides a codegen feature to generate a java wrapper for your contract, which can be used to deploy and interact with contract. The steps are as follows:
1. Generate `.abi` and `.bin` file for your contract
2. Generate `.java` wrapper for your contract
3. Use `.java` to deploy and interact with your contract

The detailed steps are below:

Generate `.abi` and `.bin` files for your solidity contract. e.g., if you have `Counter.sol`, you could use `solc` or `solcjs`

```sol
contract Counter {
    int256 private count = 0;

    function incrementCounter() public {
        count += 1;
    }

    function decrementCounter() public {
        count -= 1;
    }

    function getCount() public view returns (int256) {
        return count;
    }
}
```

```
solcjs Counter.sol --bin --abi --optimize -o <output-dir>
```

The output directory will contain `Counter_sol_Counter.abi` and `Counter_sol_Counter.bin`.

Generate Harmonyj wrapper, using `SolidityFunctionWrapperGenerator` class. e.g.,

```java
String[] options = new String[] { "-a", "Counter_sol_Counter.abi", "-b", "Counter_sol_Counter.bin", "-o", ".", "-p", "one.harmony.cmd" };
SolidityFunctionWrapperGenerator.main(options);
```
The possible options that can be passed are:
```
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
  -a, --abiFile=<abiFile>    abi file with contract definition.
  -b, --binFile=<binFile>    bin file with contract compiled code in order to
                               generate deploy methods.
  -o, --outputDir=<destinationFileDir>
                             destination base directory.
  -p, --package=<packageName>
                             base package name.
      -jt, --javaTypes       use native java types.
                               Default: true
      -st, --solidityTypes   use solidity types.
```

The `SolidityFunctionWrapperGenerator` will generate a wrapper `Counter_sol_Counter.java` int the package `one.harmony.cmd`, which can be used to deploy the contract and interact.

#### Deploying the contract

Import/add the account that you wish to use for deploying the contract. e.g.,
```java
String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
String accountName = "a1";
Keys.importPrivateKey(key, accountName);
```

Create a handler with account information such as one-address, passphrase, node url, and chainID. For mainnet: `ChainID.MAINNET`, testnet: `ChainID.TESTNET`, localnet: `ChainID.LOCAL`.
```java
String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
String passphrase = "harmony-one";
String node = "http://127.0.0.1:9500/";
Handler handler = new Handler(from, passphrase, node, ChainID.LOCAL);
```

Create a contract gas provider:
```java
class MyGasProvider extends StaticGasProvider {
	public MyGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
		super(gasPrice, gasLimit);
	}
}
MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("6721900")); 
```

Deploy contract:
```java
Counter_sol_Counter counter = Counter_sol_Counter.deploy(handler, contractGasProvider).send();
System.out.println("Contract deploy at " + counter.getContractAddress());
```

Load the deployed contract for interacting:
```java
MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("6721900")); 
Counter_sol_Counter contract = Counter_sol_Counter.load(contractAddress, contractGasProvider);
contract.setHandler(handler);
```
For read-only contract interactions (such as calling a contract to fetch some value), the handler need not be associated with an account. e.g., 
```java
String node = "http://127.0.0.1:9500/";
Handler handler = new Handler(node, ChainID.LOCAL);
```
For modifying (or updating) the contract, the handler must be associated with an account, as shown for deploying.

Calling a read-only contract method. e.g., `getCount()` in the Counter contract:
```java
System.out.println("Value stored in remote smart contract: " + contract.getCount().send());
```

Updating the contract, e.g., `incrementCounter()` in the Counter contract:
```java
TransactionReceipt transactionReceipt = contract.incrementCounter().send();
```
The `TransactionReceipt` will contain all the details:
```
{
	transactionHash='0x54492458ea1b6200a481d38dc99cb8a19cea16ee777c5ea95bcfafabd7293392', 
	transactionIndex='0x0', 
	blockHash='0xe5b6de4343dda1433c3fcffefbc008e17a8a1d17eaa794a8541be10acc67281f', 
	blockNumber='0xb3a', 
	cumulativeGasUsed='0xa289', 
	gasUsed='0xa289', 
	contractAddress='null', 
	root='null', 
	status='0x1', 
	from='one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy', 
	to='one19f70ncsqp3ny7wp2h5vmd2540zr6yx2yjs45n7', 
	logs=[], logsBloom='0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
}
```

For the complete auto-generated Counter contract: [Auto-generated Counter contract wrapper](https://github.com/harmony-one/harmonyj/blob/master/src/test/java/one/harmony/cmd/Counter_sol_Counter.java)

Complete example of deploying and interacting with Counter contract: [CounterContractExample](https://github.com/harmony-one/harmonyj/blob/master/src/test/java/one/harmony/cmd/CounterContractExample.java)

Decoding the contract logs:

After updating a contract, if we want to process the logs for events, e.g., in the Greeter.sol contract, we want to process the emitted event `Modified`
```sol
function newGreeting(string memory _greeting) public {
	emit Modified(greeting, _greeting, greeting, _greeting);
	greeting = _greeting;
}

event Modified(
	string indexed oldGreetingIdx, string indexed newGreetingIdx,
	string oldGreeting, string newGreeting
);
```
```java
TransactionReceipt transactionReceipt = contract.newGreeting("Well hello again").send();
for (Greeter_sol_Greeter.ModifiedEventResponse event : contract.getModifiedEvents(transactionReceipt)) {
	System.out.println(
			"Modify event fired, previous value: " + event.oldGreeting + ", new value: " + event.newGreeting);
	System.out.println("Indexed event previous value: " + Numeric.toHexString(event.oldGreetingIdx)
			+ ", new value: " + Numeric.toHexString(event.newGreetingIdx));
}
```

The complete [GreeterContractExample](https://github.com/harmony-one/harmonyj/blob/master/src/test/java/one/harmony/cmd/GreeterContractExample.java)



