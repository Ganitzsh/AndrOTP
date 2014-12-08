package lol.otp;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

/**
 * This class is contains the main tools to TOTP authentication.
 * 
 * @author ganich_j
 *
 */
public final class TOTPUtility {

	private static String	HMAC_ALGORITHM = "HmacSHA1";
	public static int		TIME_STEP = 30;
	
	/**
	 * This method generates a byte array corresponding to the
	 * signed secret using value a signing operator
	 * 
	 * @param	value	the value used to sign the key
	 * @param	secretKey	the key to sign
	 * @return	a byte array corresponding to the raw HMAC-SHA1 hash
	 */
	public static byte[]	hmacSha1(byte[] value, byte[] secretKey) throws RuntimeException {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secretKey,
					HMAC_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(value);
			return (rawHmac);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This method generate a 6-digit code to use for TOTP authentication.
	 * The process is the following:
	 * 
	 *  <ol>
  	 *		<li>Get UNIX-time divided by the time step you need in a long value</li>
  	 *		<li>Convert the UNIX-time to a byte array</li>
  	 *		<li>Generate a binary HMAC-SHA1 using the UNIX-time byte array</li>
  	 *		<li>Store the last byte from the HMAC-SHA1</li>
  	 *		<li>Compute an offset from the last 4-bits from the last byte</li>
  	 *		<li>Get a 4-bytes wide chunk from the HMAC-SHA1 hash starting at offset</li>
  	 *		<li>Convert it to an integer  (4 bytes = 32 bits = sizeof(integer))</li>
  	 *		<li>Get the six-digit corresponding to the remains of the previously
	 *  	    computed number divided by 1 000 000</li>
	 *	</ol>
	 * 
	 * @param	secret	the secret key to sign
	 * @return	the 6-digit number as a string
	 */
	public static String	generateDaCode(byte[] secret) {
		ByteBuffer chunkWrapper = null;
		String daCode = null;
		byte[] hmac = null, chunk = null, timeAsByteAray = null;
		long sixDigitNumberInteger = 0, time = 0;
		byte lastByte = 0;
		int offset = 0;
		
		// Get unix-time divided by TIME_STEP
		time = getUnixTimeDividedByTimeStep();
		// Convert time to a byte array
		timeAsByteAray = ByteBuffer.allocate(8).putLong(time).array();
		// Sign the secret using the unix-time
		hmac = TOTPUtility.hmacSha1(timeAsByteAray, secret);
		// Getting the last byte from the previously generated HMAC-SHA1 hash
		lastByte = hmac[hmac.length - 1];
		// Computing the offset from where to truncate the HMAC hash
		offset = (lastByte & 0xF);
		// Getting a 4-byte sized chunk from the HMAC hash starting at offset
		chunk = Arrays.copyOfRange(hmac, offset, offset+4);
		// Preparing the chunk to be converted to an integer
		chunkWrapper = ByteBuffer.wrap(chunk);
		// Removing the less significant byte from the chunk (sign does not matter) 
		// and getting the 6-digit code we wanted
		sixDigitNumberInteger = (chunkWrapper.getInt() & 0x7FFFFFFF) % 1000000;
		//Finally returning the code as a String and we're done! :D
		if (String.valueOf(sixDigitNumberInteger).length() != 6)
			daCode = "0" + String.valueOf(sixDigitNumberInteger);
		else
			daCode = String.valueOf(sixDigitNumberInteger);
		Log.d("OMG", "LE CODE: " + daCode);
		return (daCode);
	}
	
	/**
	 * Tis method provides the UNIX-time divided by TIME_STEP in milliseconds
	 * 
	 * @return	the UNIX-time divided by TIME_STEP in milliseconds
	 */
	public static long	getUnixTimeDividedByTimeStep() {
		return (new Date().getTime() / TimeUnit.SECONDS.toMillis(TIME_STEP));
	}
	
	/**
	 * Tis method provides the UNIX-time in milliseconds
	 * 
	 * @return	the actual UNIX-time in seconds
	 */
	public static long getUnixTime() {
		return (new Date().getTime() / 1000);
	}
	
	/**
	 * This method provides the time till next tick based on time
	 * 
	 * @param	time	The current UNIX-time
	 * @return	the time till the next tick based on the TIME_STEP
	 */
	public static long	getTimeTillNextTick(long time) {
		return (TIME_STEP - (time % TIME_STEP));
	}
}
