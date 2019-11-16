package one.harmony.account;

import java.io.ByteArrayOutputStream;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Bech32;
import org.bitcoinj.core.Bech32.Bech32Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Numeric;

/**
 * Harmony address class that provides Harmony one address and hex address.
 * Also, provides utility functions to convert between these two addresses.
 * 
 * @author gupadhyaya
 *
 */
public class Address {
	private static final Logger log = LoggerFactory.getLogger(Address.class);

	private static final int HASH_LENGTH = 32;
	private static final int ADDRESS_LENGTH = 20;
	private static final String BECH32_ADDRESS_HRP = "one";

	private final String oneAddr;
	private final String hexAddr;

	public Address(String addr, boolean isHex) {
		if (isHex) {
			this.oneAddr = toBech32(addr);
			this.hexAddr = addr;
		} else {
			this.oneAddr = addr;
			this.hexAddr = parseBech32(addr);
		}
	}

	public String getOneAddr() {
		return oneAddr;
	}

	public String getHexAddr() {
		return hexAddr;
	}

	public static boolean isOneAddr(String addr) {
		return addr.startsWith("one");
	}

	private static byte[] convertBits(final byte[] in, final int inStart, final int inLen, final int fromBits,
			final int toBits, final boolean pad) throws AddressFormatException {
		int acc = 0;
		int bits = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream(64);
		final int maxv = (1 << toBits) - 1;
		final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
		for (int i = 0; i < inLen; i++) {
			int value = in[i + inStart] & 0xff;
			if ((value >>> fromBits) != 0) {
				throw new AddressFormatException(
						String.format("Input value '%X' exceeds '%d' bit size", value, fromBits));
			}
			acc = ((acc << fromBits) | value) & max_acc;
			bits += fromBits;
			while (bits >= toBits) {
				bits -= toBits;
				out.write((acc >>> bits) & maxv);
			}
		}
		if (pad) {
			if (bits > 0)
				out.write((acc << (toBits - bits)) & maxv);
		} else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
			throw new AddressFormatException("Could not convert bits, invalid padding");
		}
		return out.toByteArray();
	}

	private static String convertAndEncode(String hrp, byte[] data) {
		byte[] converted = convertBits(data, 0, data.length, 8, 5, true);
		return Bech32.encode(hrp, converted);
	}

	private static byte[] decodeAndConvert(String bech) {
		Bech32Data bech32Data = Bech32.decode(bech);
		return convertBits(bech32Data.data, 0, bech32Data.data.length, 5, 8, true);
	}

	/**
	 * parseBech32 parses a Harmony one address to fetch the hex address.
	 * 
	 * @param oneAddress
	 * @return
	 */
	public static String parseBech32(String oneAddress) {
		return Numeric.toHexString(decodeAndConvert(oneAddress));
	}

	/**
	 * toBech32 method parses a hex address to compute Harmony one address (a Bech32
	 * address)
	 * 
	 * @param hexAddress
	 * @return
	 */
	public static String toBech32(String hexAddress) {
		return convertAndEncode(BECH32_ADDRESS_HRP, Numeric.hexStringToByteArray(hexAddress));
	}
}
