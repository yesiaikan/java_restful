package com.muli.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: zhangjunhong
 * Date: 8/17/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompressUtils {

    public static void zip(File input, File output) throws IOException {
        compress(input, null, output);
    }

    public static void unzip(File input, File output) throws IOException {
        ZipFile zipFile = new ZipFile(input);
        Enumeration<? extends ZipEntry> zipFiles = zipFile.entries();

        File targetFile = null;
        FileOutputStream targetFileOutput = null;
        while (zipFiles.hasMoreElements()) {
            ZipEntry zipEntry = zipFiles.nextElement();
            InputStream eis = zipFile.getInputStream(zipEntry);

            String filePath = String.format("%s%s%s", output.getAbsolutePath(), File.separator, zipEntry.getName());
            targetFile = new File(filePath);
            if (zipEntry.isDirectory()) {
                if(!targetFile.exists() && !targetFile.mkdirs() ){
                    throw new IOException("Don't create directory.");
                }
                continue;
            } else {
                if (null != targetFile.getParentFile()&&!targetFile.getParentFile().exists()) {
                    if(!targetFile.getParentFile().mkdirs())throw new IOException("Don't create directory.");
                }
                if(!targetFile.createNewFile())throw new IOException("Don't create new file.");
            }
            targetFileOutput = new FileOutputStream(targetFile);
            IOUtils.copy(eis, targetFileOutput);
            targetFileOutput.flush();
            //close it
            IOUtils.closeQuietly(eis);
            IOUtils.closeQuietly(targetFileOutput);
        }
        try {
            zipFile.close();
        } catch (IOException e) {
        }
    }

    public static void compress(File inputFile, String[] extensions, File outputFile) throws IOException {
        BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputFile));
        ZipOutputStream zipOutput = new ZipOutputStream(bufferedOut);
        zipOutput.setLevel(9);
        Collection<File> files = new LinkedList<File>();
        if (inputFile.exists() && inputFile.isDirectory()) {
            files = FileUtils.listFiles(inputFile, extensions, true);
            if (null == files) files = new LinkedList<File>();
        } else {
            files.add(inputFile);
        }
        try {
            for (File file : files) {
                zipOutput.putNextEntry(new ZipEntry(file.getName()));
                BufferedInputStream bufferedInput = null;
                try {
                    bufferedInput = new BufferedInputStream(new FileInputStream(file));
                    IOUtils.copy(bufferedInput, zipOutput);

                    zipOutput.flush();
                } finally {
                    IOUtils.closeQuietly(bufferedInput);
                }

            }
        } catch (IOException e) {
            if (outputFile.exists() && outputFile.isFile()) {
                FileUtils.deleteQuietly(outputFile);
            }
            throw e;
        } finally {
            IOUtils.closeQuietly(zipOutput);
        }
    }

    public static void main(String[] args) {
        String dataPath = Thread.currentThread().getClass().getResource("/").getPath();

        File inputFile = new File(String.format("%s/%s", dataPath, "11.db"));
        File outputFile = new File(String.format("%s/%s.zip", dataPath, "123.db"));


        try {
            CompressUtils.zip(inputFile, outputFile);
            CompressUtils.unzip(outputFile, new File(dataPath + "/" + "123"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
