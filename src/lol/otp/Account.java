package lol.otp;

import java.util.Locale;

import org.apache.commons.codec.binary.Base32;

/**
 * Current key for debug: "6yof 6u6b pvgp j34k 2zc5 jzl2 d3eu bjmr"
 * 
 * @author ganich_j
 *
 */
public class Account {
	private long	id = 0;
	private String	originalSecret = "00000000000000000000000000000000";
	private byte[]	decodedSecret = null;
	private String	accountName = "unknown";
	private String	code = "000000";
	
	public Account() {
	}
	
	public Account(String name, String secret) {
		Base32 codec = new Base32();
		
		accountName = name;
		originalSecret = secret.replaceAll("\\s+","").toUpperCase(Locale.US);
		decodedSecret = codec.decode(originalSecret);
	}

	public String getOriginalSecret() {
		return originalSecret;
	}

	public void setOriginalSecret(String originalSecret) {
		Base32 codec = new Base32();
		this.originalSecret = originalSecret;
		decodedSecret = codec.decode(originalSecret);
	}

	public byte[] getDecodedSecret() {
		return decodedSecret;
	}

	public void setDecodedSecret(byte[] decodedSecret) {
		this.decodedSecret = decodedSecret;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return (this.id);
	}
}
