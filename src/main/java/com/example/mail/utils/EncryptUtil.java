package com.example.mail.utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptUtil {

	public static String SHA1(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(str.getBytes());
			return byteToString(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String byteToString(byte[] digest) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			String tempStr = Integer.toHexString(digest[i] & 0xff);
			if (tempStr.length() == 1) {
				buf.append("0").append(tempStr);
			} else {
				buf.append(tempStr);
			}
		}
		return buf.toString().toLowerCase();
	}

	public static String MD5(String value) {
		if (value == null || "".equals(value)) {
			return value;
		}
		try {
			StringBuilder sb = new StringBuilder();
			MessageDigest md = MessageDigest.getInstance("md5");// sha
			byte[] bs = value.getBytes();
			byte[] mb = md.digest(bs);
			for (int i = 0; i < mb.length; i++) {
				int v = mb[i] & 0xFF;// 0~255
				if (v < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(v).toUpperCase());
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) throws UnsupportedEncodingException {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes("utf-8");
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) throws UnsupportedEncodingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray(), "utf-8");
	}

	// 算法密钥
	private static final String DES_KEY = "A1BBC3D4G5F60709";

	/**
	 * DES加密
	 * @param datasource byte[]
	 * @param password   String
	 * @return byte[]
	 */
	public static String encrypt(String data) {
		String encryptedData = null;
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			DESKeySpec deskey = new DESKeySpec(DES_KEY.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(deskey);
			// 加密对象
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key, sr);
			// 加密，并把字节数组编码成字符串
			encryptedData = Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("加密错误，错误信息：", e);
		}
		return encryptedData;
	}

	/**
	 * DES解密
	 * @param src      byte[]
	 * @param password String
	 * @return byte[]
	 * @throws Exception
	 */
	public static String decrypt(String data) {
		String decryptedData = null;
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			DESKeySpec deskey = new DESKeySpec(DES_KEY.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(deskey);
			// 解密对象
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key, sr);
			// 把字符串解码为字节数组，并解密
			decryptedData = new String(cipher.doFinal(Base64.getDecoder().decode(data)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解密错误，错误信息：", e);
		}
		return decryptedData;
	}
	
	private static final String AES = "AES";
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";// 默认的加密算法

	/**
	 * AES加密字符串
	 * @param content 需要被加密的字符串
	 * @param sKey    加密需要的密码
	 * @param ivKey   秘钥偏移量
	 * @return 密文
	 */
	public static String encryptAES(String content, String sKey, String ivKey) {
		try {
			Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
			IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));
			return Base64.getEncoder().encodeToString(encrypted);// 通过Base64转码返回
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密AES加密过的字符串
	 * @param content AES加密过过的内容
	 * @param sKey    加密需要的密码
	 * @param ivKey   秘钥偏移量
	 * @return 明文
	 */
	public static String decryptAES(String content, String sKey, String ivKey) {
		try {
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
			Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64.getDecoder().decode(content);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			return new String(original, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * MAC算法可选以下多种算法
	 * <pre>
	 * HmacMD5
	 * HmacSHA1
	 * HmacSHA256
	 * HmacSHA384
	 * HmacSHA512
	 * </pre>
	 */
	public static final String KEY_MAC = "HmacMD5";

	/**
	 * HMAC加密
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptHMAC(String data, String key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return byteArrayToHexString(mac.doFinal(data.getBytes("utf-8")));
	}

	/* byte数组转换为HexString */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v).toUpperCase());
		}
		return sb.toString();
	}

}
