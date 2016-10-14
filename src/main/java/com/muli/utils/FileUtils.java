package com.muli.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangshichao on 2014/12/26.
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static File in2File(InputStream in, String filePath) throws Exception {

        FileOutputStream out = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            if (file.exists()) {
                return file;
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            byte[] bytes = IOUtils.toByteArray(in);
            if (bytes != null) {
                out.write(bytes);
            } else {
                return null;
            }
            return file;
        } catch (Exception e) {
            logger.error("write stream to file error: {}", e.getMessage());
            throw new Exception("write stream to file error", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void file2Thumbnail(File file, String outputPath, int high, int weight) throws IOException {

        Thumbnails.of(file).size(high, weight).outputFormat("jpg").outputQuality(0.8f).toFile(outputPath);

    }
}
