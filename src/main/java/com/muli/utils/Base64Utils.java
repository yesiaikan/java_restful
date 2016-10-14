package com.muli.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * 使用base64编码方式对文件和字符串进行编码解码
 * @author lms
 */
public class Base64Utils {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Base64Utils.class);
    /**
     * 对文件进行编码
     * @param file 需要编码的问家
     * @return 对文件进行base64编码后的字符串
     * @throws Exception
     */
    public static String file2String(File file) throws Exception{
        StringBuffer sb = new StringBuffer();
        FileInputStream in = new FileInputStream(file);
        int b;
        char ch;
        while((b=in.read())!=-1){
            ch = (char)b;
            sb.append(ch);
        }
        in.close();
        //将buffer转化为string
        String oldString = new String(sb);
        //使用base64编码
        String newString = compressData(oldString);
        return newString;
    }

    /**
     * 对文件进行解码
     * @param oldString 需要解码的字符串
     * @param filePath	将字符串解码到filepath文件路径
     * @return	返回解码后得到的文件
     * @throws Exception
     */
    public static File string2File(String oldString,String filePath) throws Exception{

        FileOutputStream out =null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                System.out.println("文件已经存在，不能将base64编码转换为文件");
                return null;
            } else {
                file.createNewFile();
            }
             out = new FileOutputStream(file);
            //对oldString进行解码
            byte[] bytes = decodeBase64(oldString);
            if (bytes != null) {
                out.write(bytes);
            } else {
                return null;
            }
            return file;
        }catch (Exception e){
            LOGGER.warn("string2File fail {} :"+e.getMessage());
            return null;
        }finally {
            out.close();
        }
    }

    /**
     * 使用base64编码字符串
     * @param data
     * @return 编码后的字符串
     */
    public static String compressData(String data) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DeflaterOutputStream zos = new DeflaterOutputStream(bos);
            zos.write(data.getBytes());
            zos.close();
            return new String(getenBASE64inCodec(bos.toByteArray()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ZIP_ERR";
        }
    }

    /**
     * 使用base64解码字符串
     * @param encdata
     * @return 解码后的字符串
     */
    public static String decompressData(String encdata) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InflaterOutputStream zos = new InflaterOutputStream(bos);
            zos.write(getdeBASE64inCodec(encdata));
            zos.close();
            return new String(bos.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return "UNZIP_ERR";
        }
    }

    public  static  byte[]  decodeBase64(String encdata){

        try {
            Base64.decodeBase64(encdata);
            return  Base64.decodeBase64(encdata);
        } catch (Exception ex) {

            return null;
        }

    }


    /**
     * 调用apache的编码方法
     */
    public static String getenBASE64inCodec(byte [] b) {
        if (b == null)
            return null;
        return new String((new Base64()).encode(b));
    }

    /**
     * 调用apache的解码方法
     */
    public static byte[] getdeBASE64inCodec(String s) {
        if (s == null)
            return null;
        return new Base64().decode(s.getBytes());
    }


}
