package com.muli.utils;

import com.google.inject.Inject;
import com.muli.service.FtpService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhengfan.
 * User: dianxin
 * Date: 14-3-26
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public class FtpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

    private  ObjectPool<FtpService> pool;

    private Configuration configuration;

    @Inject
    public FtpUtil(ObjectPool<FtpService> pool,Configuration configuration) {
        this.pool = pool;
        this.configuration =  configuration;
    }


    public String uploadToCDN(InputStream in, String dir, String name) throws Exception {
        FtpService ftpService =null;
        try {
            ftpService = pool.borrowObject();

            if(!ftpService.isConnected()){
                String host = configuration.getString("cdn.ftp.server.host");
                int port = configuration.getInt("cdn.ftp.server.port");
                String user = configuration.getString("cdn.ftp.server.user");
                String password = configuration.getString("cdn.ftp.server.password");
                ftpService.reConnect(host, port, user, password,true);
            }
            String remoteDir = configuration.getString("cdn.ftp.server.base") +dir;
            ftpService.mkdir(remoteDir);

            //String cdnURL = configuration.getString("cdn.server.base") + dir + name;
            final String cdnURL = configuration.getString("cdn.server.base") + remoteDir+name;

            boolean flag = ftpService.uploadFile(in,remoteDir,name);
            if (!flag) {
                throw new IOException("failed to upload: " + remoteDir + name);
            }
            return cdnURL;

        }finally {
            try {
                if (null != ftpService) {
                    pool.returnObject(ftpService);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
    }


    public void deleteFileOnCdn(String file){

        FtpService ftpService = null;
        try {
            ftpService = pool.borrowObject();
            String host = configuration.getString("cdn.ftp.server.host");
            int port = configuration.getInt("cdn.ftp.server.port");
            String user = configuration.getString("cdn.ftp.server.user");
            String password = configuration.getString("cdn.ftp.server.password");

            if (!ftpService.isConnected()){
                ftpService.reConnect(host, port, user, password,true);
            }

            LOGGER.info("delete file : {}", file);
            boolean flag = ftpService.deleteFile(file);
            if (!flag) {
                LOGGER.error("failed to delete file:  " + file);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        } finally {
            try {
                if (null != ftpService) {
                    pool.returnObject(ftpService);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
    }


}
