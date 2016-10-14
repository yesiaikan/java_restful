package com.muli.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created with IntelliJ IDEA.
 * User: dianxin
 * Date: 5/2/13
 * Time: 10:44 AM
 * Despt: 3des加密，解密；
 */
public class TripleDES {
    static final byte[] DX_KEY_PREFIX = {0x5e, 0x0b, 0x0c, 0x14, 0x0c, 0x0d, 0x63, 0x4d, 0x56, 0x1b, 0x06, 0x00, 0x2c, 0x0a, 0x69, 0x77};
    static final String DEFAULT_CHARASET = "utf-8";

    public static byte[] encrypt(byte[] key, byte[] iv, byte[] message) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = skf.generateSecret(new DESedeKeySpec(key));
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            result = cipher.doFinal(message);
        } catch (Exception e) {
            throw new SecurityException(e);
        }

        return result;
    }

    public static byte[] decrypt(byte[] key, byte[] iv, byte[] message) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = skf.generateSecret(new DESedeKeySpec(key));
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            result = cipher.doFinal(message);
        } catch (Exception e) {
            throw new SecurityException(e);
        }

        return result;
    }

    public static byte[] dx_encrypt(long iv, byte[] message) {
        byte[] ivBytes = longToBytes(iv); //8 bytes
        ByteBuffer keyBuffer = ByteBuffer.allocate(24);
        keyBuffer.put(DX_KEY_PREFIX);
        keyBuffer.put(ivBytes);

        return encrypt(keyBuffer.array(), ivBytes, message);
    }

    public static String encryptWithBase64(String key, long iv, String message) {
        try {
            byte[] cipherText = encrypt(key.getBytes(DEFAULT_CHARASET), longToBytes(iv), message.getBytes(DEFAULT_CHARASET));
            return Base64.encodeBase64String(cipherText);
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(e);
        }
    }

    public static String decryptWithBase64(String key, long iv, String cipherText) {
        try {
            byte[] text = decrypt(key.getBytes(DEFAULT_CHARASET), longToBytes(iv), Base64.decodeBase64(cipherText));
            return new String(text, DEFAULT_CHARASET);
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(e);
        }
    }
    public static byte[] dx_decrypt(long iv, byte[] message) {
        byte[] ivBytes = longToBytes(iv); //8 bytes
        ByteBuffer keyBuffer = ByteBuffer.allocate(24);
        keyBuffer.put(DX_KEY_PREFIX);
        keyBuffer.put(ivBytes);

        return decrypt(keyBuffer.array(), ivBytes, message);
    }

    public static byte[] longToBytes(long v) {
//        byte[] writeBuffer = new byte[8];
//        writeBuffer[0] = (byte) (v >>> 56);
//        writeBuffer[1] = (byte) (v >>> 48);
//        writeBuffer[2] = (byte) (v >>> 40);
//        writeBuffer[3] = (byte) (v >>> 32);
//        writeBuffer[4] = (byte) (v >>> 24);
//        writeBuffer[5] = (byte) (v >>> 16);
//        writeBuffer[6] = (byte) (v >>> 8);
//        writeBuffer[7] = (byte) (v >>> 0);
//        return writeBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(v);
        return byteBuffer.array();
    }

    /*
    * AES加密(结果为16进制字符串)
    *
    * @param content
    *            要加密的字符串(11位电话号码)
    * @param password
    *            密钥(16位)
    * @return 加密后的32位16进制字符串
    */
    public static String encryptLocalNumber(long iv, String appSecret, String message) {
        if (StringUtils.isEmpty(appSecret) || StringUtils.isEmpty(message)) {
            return null;
        }
        String result = null;
        byte[] byteresult = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//“算法／模式／填充”
            //KeyGenerator 生成aes算法密钥
            while (appSecret.length() < 16){
                appSecret += appSecret;
            }
            appSecret = appSecret.substring(0,16);
            SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(longToBytes16(iv));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);//使用密钥
            byteresult = cipher.doFinal(message.getBytes()); //加密
            result = byte2str(byteresult);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
        return result;
    }

    public static String decryptLocalNumber(long iv, String appSecret, String message) {
        if (StringUtils.isEmpty(appSecret) || StringUtils.isEmpty(message)) {
            return null;
        }
        String result = null;
        byte[] bytemessage = str2byte(message);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//“算法／模式／填充”
            while (appSecret.length() < 16){
                appSecret += appSecret;
            }
            appSecret = appSecret.substring(0,16);
            //KeyGenerator 生成aes算法密钥
            SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(longToBytes16(iv));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);//使用密钥
            byte[] byteresult = cipher.doFinal(bytemessage); //解密
            result = new String(byteresult, "utf-8");
        } catch (Exception e) {
            throw new SecurityException(e);
        }
        return result;
    }

    private static byte[] longToBytes16(long v) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(v);
        return byteBuffer.array();
    }

    private static String byte2str(byte[] b) { // 一个字节的数，
        if(b == null){
            return null;
        }
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) { //保证每个字节转成两位十六进制数
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }

    private static byte[] str2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        long a = 12345678l;
//
//        byte[] byteKeyPrefix = {0x5e, 0x0b, 0x0c, 0x14, 0x0c, 0x0d, 0x63, 0x4d, 0x56, 0x1b, 0x06, 0x00, 0x2c, 0x0a, 0x69, 0x77};
//        byte[] iv = longToBytes(a); //8 bytes
//        ByteBuffer byteBuffer = ByteBuffer.allocate(24);
//        byteBuffer.put(byteKeyPrefix);
//        byteBuffer.put(iv);
//
//        String text = "[ {   \"address\" : \"10086\",   \"body\" : \"恭喜发财，回复YYX123456即可申请12期，电话123-456-7899；或点击cgbchina.cn/C1S9mx0rq申请分期付款，实际费率以申请时为准。\",   \"date\" : 1366627056113 }]";
//        byte[] cTxt = TripleDES.encrypt(byteBuffer.array(), iv, "hello, world.".getBytes("UTF-8"));
//        System.out.println("cipher: " + Base64.encodeBase64String(cTxt));
//
//        byte[] txt = TripleDES.decrypt(byteBuffer.array(), longToBytes(a), cTxt);
//        System.out.println("txt: " + new String(txt, "UTF-8"));
//
//        cTxt = TripleDES.dx_encrypt(a, text.getBytes("UTF-8"));
//        System.out.println("cipher2: " + Base64.encodeBase64String(cTxt));
        long createTime = 1446704975910l;
        String phone = "19989238736";
        String appsecret = "50b13132bb394901f151bc12";
        String enc = encryptLocalNumber(createTime,appsecret,phone);
        System.out.println(enc);
        String num = decryptLocalNumber(createTime,appsecret,enc);
        System.out.println(num);
    }
}