package in.walkwithus.eguide.helpers;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import in.walkwithus.eguide.BuildConfig;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */


public class Cryptography {

    public static String md5(String input) {
        if (input == null || input.length() == 0) {
            return null;
        }

        return Base64.encodeToString(md5(input.getBytes()), Base64.NO_WRAP);
    }

    public static byte[] md5(byte[] input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(ALGORITHM_MD5);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        return digest.digest(input);
    }

    public static String encrypt(String input) {
        if (input == null) {
            return null;
        }

        try {
            byte[] encodedBytes = getEncryptCipher().doFinal(input.getBytes());
            return Base64.encodeToString(encodedBytes, BASE64_FLAGS);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                throw new AssertionError("Cryptography.encrypt failed for: " + input);
            }
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String input) {
        if (input == null) {
            return null;
        }

        try {
            byte[] decodedBytes = getDecryptCipher().doFinal(Base64.decode(input, BASE64_FLAGS));
            return new String(decodedBytes);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                throw new AssertionError("Cryptography.decrypt failed for: " + input);
            }
            e.printStackTrace();
            return null;
        }
    }

    private static synchronized Key getKey() {
        if (key == null) {
            key = new SecretKeySpec(__(), ALGORITHM_SHA256);
        }
        return key;
    }

    private static synchronized Cipher getEncryptCipher() {
        if (encryptCipher == null) {
            encryptCipher = createCipher(Cipher.ENCRYPT_MODE);
        }
        return encryptCipher;
    }

    private static synchronized Cipher getDecryptCipher() {
        if (decryptCipher == null) {
            decryptCipher = createCipher(Cipher.DECRYPT_MODE);
        }
        return decryptCipher;
    }

    private static Cipher createCipher(int operationMode) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(operationMode, getKey());
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] __() {
        return md5(Base64.decode("IU1ZaypiTiVYc3AtPXMxNWNMYGUmVUF8YUAtUSMuKVI=", BASE64_FLAGS));
    }

    private static final String ALGORITHM_MD5 = "MD5";
    private static final String ALGORITHM_SHA256 = "HmacSHA256";
    private static final String TRANSFORMATION = "AES";
    private static final int BASE64_FLAGS = Base64.NO_WRAP;
    private static Key key;
    private static Cipher encryptCipher;
    private static Cipher decryptCipher;
}
