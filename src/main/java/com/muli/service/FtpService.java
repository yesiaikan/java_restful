package com.muli.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FtpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpService.class);

    private FTPClient ftpClient;

    public FtpService(String host, int port, String user, String password) throws IOException {
        this(host, port, user, password, true);
    }


    public FtpService(String host, int port, String user, String password, boolean isPassivce) throws IOException {

        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(10000);//todo 连接10秒超时
        ftpClient.connect(host, port);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connect replyCode:{}", ftpClient.getReplyCode());
        }
        ftpClient.login(user, password);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("login replyCode:{}", ftpClient.getReplyCode());
        }
        if (isPassivce) {
            ftpClient.enterLocalPassiveMode();
        }

        setFileTypeBinary(true);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connected to:{} with user:{}", host + ":" + port, user);
        }
    }

    public void reConnect(String host, int port, String user, String password, boolean isPassivce)throws IOException{

        if (ftpClient!=null ){
            try {
                ftpClient.disconnect();
            }catch ( Exception e){
                LOGGER.debug("close server:{}", e.getMessage());
            }

            ftpClient.connect(host, port);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("connect replyCode:{}", ftpClient.getReplyCode());
            }
            ftpClient.login(user, password);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("login replyCode:{}", ftpClient.getReplyCode());
            }
            if (isPassivce) {
                ftpClient.enterLocalPassiveMode();
            }

            setFileTypeBinary(true);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("connected to:{} with user:{}", host + ":" + port, user);
            }
        }

    }

    public boolean setFileTypeBinary(boolean isBinary) throws IOException {
        return ftpClient.setFileType(isBinary ? FTP.BINARY_FILE_TYPE : FTP.ASCII_FILE_TYPE);
    }

    public boolean  isConnected(){
        return  ftpClient.isConnected();
    }


    public void closeServer() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            LOGGER.warn("close ftp server error", e);
        }

    }

    // =======================================================================
    // == About directory =====
    // The following method using relative path better.
    // =======================================================================

    public boolean changeToDir(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    public void mkdir(String pathName) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("pathName:" + pathName);
        }
        if (dirExists(pathName)) {
            return;
        }
        File file = new File(pathName);
        String parentPath = FilenameUtils.separatorsToUnix(file.getParent());
        if (!dirExists(parentPath)) {
            mkdir(parentPath);
        }
        boolean result = ftpClient.makeDirectory(pathName);
        if (!result) {
            throw new IOException("mkdir failed!" + pathName);
        }
    }

    public void rename(String from, String to) throws IOException {
        boolean rename = ftpClient.rename(from, to);
        if (!rename) {
            throw new IOException("file rename failed, from:" + from + ", to:" + to);
        }
    }

    public boolean removeDir(String path) throws IOException {
        return ftpClient.removeDirectory(path);
    }

    // delete all subDirectory and files.
    public boolean removeDirs(String path)
            throws IOException {

        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return removeDir(path);
        }
        //
        for (FTPFile ftpFile : ftpFileArr) {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                LOGGER.info("* [sD]Delete subPath [{}]", path + "/" + name);
                removeDirs(path + "/" + name);
            } else if (ftpFile.isFile()) {
                LOGGER.info("* [sD]Delete file [{}]", path + "/" + name);
                deleteFile(path + "/" + name);
            } else if (ftpFile.isSymbolicLink()) {
                deleteFile(path + "/" + name);
            } else if (ftpFile.isUnknown()) {
                deleteFile(path + "/" + name);
            }
        }
        return ftpClient.removeDirectory(path);
    }

    // Check the path is exist; exist return true, else false.
    public boolean dirExists(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    // =======================================================================
    // == About file =====
    // Download and Upload file using
    // ftpUtil.setFileType(FtpUtil.BINARY_FILE_TYPE) better!
    // =======================================================================

    // #1. list & delete operation
    // Not contains directory
    public List<String> getFileList(String path) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);

        List<String> retList = new ArrayList<String>();
        if (ftpFiles == null || ftpFiles.length == 0) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getName());
            }
        }
        return retList;
    }

    public boolean deleteFile(String pathName) throws IOException {
        return ftpClient.deleteFile(pathName);
    }

    public boolean uploadFile(File localFile, String remoteDir, String fileName) throws IOException {
        return uploadFile(localFile.getAbsolutePath(), remoteDir, fileName);
    }

    public boolean uploadFile(String localFile, String remoteDir) throws IOException {
        return uploadFile(new File(localFile), remoteDir);
    }

    public boolean uploadFile(File localFile, String remoteDir) throws IOException {
        return uploadFile(localFile.getAbsolutePath(), remoteDir, localFile.getName());
    }

    // #2. upload to ftp server
    // InputStream <------> byte[] simple and See API

    public boolean uploadFile(String localFile, String remoteDir, String newName)
            throws IOException {
        boolean changeToDirResult = ftpClient.changeWorkingDirectory(remoteDir);
        if (!changeToDirResult) {
            throw new IOException("change to dir " + remoteDir + "error!");
        }
        boolean flag = false;
        InputStream iStream = null;
        try {
            iStream = new FileInputStream(localFile);
            flag = ftpClient.storeFile(newName, iStream);
        } finally {
            IOUtils.closeQuietly(iStream);
        }
        return flag;
    }

    public boolean uploadFile(InputStream iStream, String remoteDir, String newName) throws IOException {
        boolean changeToDirResult = ftpClient.changeWorkingDirectory(remoteDir);
        if (!changeToDirResult) {
            throw new IOException("change to dir " + remoteDir + "error!");
        }
        boolean flag = false;
        try {
            flag = ftpClient.storeFile(newName, iStream);
        } finally {
            IOUtils.closeQuietly(iStream);
        }
        return flag;
    }

    public boolean download(String remoteFileName, String localFileName)
            throws IOException {
        boolean flag = false;
        File outfile = new File(localFileName);
        OutputStream oStream = null;
        try {
            oStream = new FileOutputStream(outfile);
            flag = ftpClient.retrieveFile(remoteFileName, oStream);
        } finally {
            IOUtils.closeQuietly(oStream);
        }
        return flag;
    }

    public boolean download(String remoteFileName, File outfile)
            throws IOException {
        boolean flag = false;
        OutputStream oStream = null;
        try {
            oStream = new FileOutputStream(outfile);
            flag = ftpClient.retrieveFile(remoteFileName, oStream);
        } finally {
            IOUtils.closeQuietly(oStream);
        }
        return flag;
    }
}