package com.crypticbit.javelin.store;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* Create a MessageDigest based on a byte array, or potentially an InputStream */
public class KeyFactory {

	private static final DigestMethod DEFAULT_DIGEST_METHOD = DigestMethod.SHA1;

	private DigestMethod defaultDigestMethod = DEFAULT_DIGEST_METHOD;;

	/**
	 * generate a digest for the data <code>data</code> using the default digest
	 * method
	 */
	public Key getDefaultDigest(byte[] data) {
		return getDigest(defaultDigestMethod, data);
	}

	/**
	 * generate a digest for the data <code>data</code> using the specified
	 * digest method
	 */
	public Key getDigest(DigestMethod method, byte[] data) {
		MessageDigest digest = method.createDigest();
		digest.update(data);
		return new Key(digest.digest());
	}

	/**
	 * Set the method that will be used to generate digests if
	 * <code>getDefaultDigest</code> is called
	 */
	public void setDefault(DigestMethod method) {
		this.defaultDigestMethod = method;
	}

	/** The set of supported digest methods, accessed as an enum */
	public enum DigestMethod {
		SHA1("SHA-1"), SHA256("SHA-256");
		private String algorithm;

		DigestMethod(String algorithm) {
			this.algorithm = algorithm;
		}

		MessageDigest createDigest() {
			try {
				return MessageDigest.getInstance(algorithm);
			} catch (NoSuchAlgorithmException e) {
				throw new Error("Digest algorithm not supported on this VM: "
						+ algorithm);
			}
		}
	}

}
