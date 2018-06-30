package knickknacker.RSA;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public abstract class RSA {
    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keys = keyGen.generateKeyPair();
        return keys;
    }

    public static byte[] encryptBytes(byte[] bytes, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static byte[] decryptBytes(byte[] b, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(b);
    }

    public static byte[] signBytes(byte[] bytes, PrivateKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(key);
        rsa.update(bytes);
        return rsa.sign();
    }

    public static boolean verifyBytes(byte[] sign, byte[] content, PublicKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initVerify(key);
        rsa.update(content);
        return rsa.verify(sign);
    }

    public static byte[] encryptString(String s, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return encryptBytes(s.getBytes(), key);
    }

    public static String decryptString(byte[] b, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return new String(decryptBytes(b, key));
    }

    public static byte[] signString(String s, PrivateKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        return signBytes(s.getBytes(), key);
    }

    public static boolean verifyString(byte[] sign, String content, PublicKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        return verifyBytes(sign, content.getBytes(), key);
    }

    public static byte[] signObject(Serializable object, PrivateKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        byte[] bytes = Serialize.serialize(object);
        return signBytes(bytes, key);
    }

    public static boolean verifyObject(byte[] sign, Serializable o, PublicKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        byte[] bytes = Serialize.serialize(o);
        return verifyBytes(sign, bytes, key);
    }

    public static PrivateKey loadPrivateKey(String key) throws GeneralSecurityException {
        byte[] encoded = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = fact.generatePrivate(keySpec);
        Arrays.fill(encoded, (byte) 0);
        return privateKey;
    }

    public static PublicKey loadPublicKey(String key) throws GeneralSecurityException {
        byte[] encoded = Base64.decode(key, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    public static String saveKey(Key key) {
        byte[] encoded = key.getEncoded();
        return Base64.encodeToString(encoded, Base64.DEFAULT);
    }
}
