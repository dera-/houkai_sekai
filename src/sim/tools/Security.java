package sim.tools;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/** 文字列の暗号化・復号化を行うクラス */
public class Security {
    /**
     * 暗号化キーアルゴリズム
     */
    private static final String ALGORITHM_KEY = "AES";
    /**
     * 暗号化アルゴリズム
     */
    private static final String ALGORITHM_CRYPT = "AES/ECB/PKCS5Padding";
    /**
     * 暗号化キー(128ビット)
     */
    public static final String KEY_SAVE_DATA = "encrypt-key-save";
    
    /**
     * エンコーディング
     */
    private static final String ENCODING = "UTF-8";
    
    /** 第2引数の文字列を暗号化するメソッド */
    public static String encrypt(String key, String text){
        try {
            byte[] encryptedArray = encryptToByteArray(key, text);
            return toHexString(encryptedArray);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    /** 第2引数の文字列を復号化するメソッド */
    public static String decrypt(String key, String text){
        try {
            byte[] decryptedArray = decode(text);
            return decryptFromByteArray(key, decryptedArray);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * 第2引数の文字列をByte配列に変換するメソッド
     * 
     * @param key
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private static byte[] encryptToByteArray(String key, String text)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(ENCODING),
                ALGORITHM_KEY);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CRYPT);
        cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
        byte[] encrypted = cipher.doFinal(text.getBytes(ENCODING));
        return encrypted;
    }

    /**
     * 第2引数のByte配列を文字列に変換するメソッド
     * 
     * @param key
     * @param encrypted
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private static String decryptFromByteArray(String key, byte[] encrypted)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(ENCODING),
                ALGORITHM_KEY);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CRYPT);
        cipher.init(Cipher.DECRYPT_MODE, sksSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }
    
    /**
     * バイト配列を16進数文字列に変換する。
     * 
     * @param b
     * @return
     */
    private static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() < 2)
                hex = "0" + hex;
            sb.append(hex);
        }
        return new String(sb);
    }

    /**
     * 16進数文字列をバイト配列に変換する。
     * 
     * @param value
     * @return
     */
    private static byte[] decode(String value) {
        byte bytes[] = new byte[value.length() / 2];

        for (int i = 0; i < value.length() / 2; i++) {
            StringBuffer hex = new StringBuffer("0x");
            hex.append(value.charAt(i * 2));
            hex.append(value.charAt(i * 2 + 1));
            bytes[i] = Integer.decode(new String(hex)).byteValue();
        }
        return bytes;

    }

}
