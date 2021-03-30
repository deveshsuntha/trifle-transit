package com.suntha.services;

import com.suntha.controller.AppController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SenderService extends Service<Boolean> {

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static AppController log;
    private String host = null;
    private String basepath = null;
    private static int totalFiles = 0;
    private int port;

    public SenderService(AppController parent, String basepath, String host, int port) {
        this.host = host;
        this.basepath = basepath;
        this.port = port;
        this.log = parent;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return execute();
            }
        };
    }

    private Boolean execute() throws Exception {

        try(Socket socket = new Socket(host,port)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            calculateNoOfFiles(basepath);
            sendDirectory(basepath);
            dataInputStream.close();
            dataInputStream.close();
            log.log("Transfer complete");
            return Boolean.TRUE;
        }catch (Exception e){
            log.log(e.getLocalizedMessage());
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    private static void calculateNoOfFiles(String basepath) {
        File f = new File(basepath);
        if (f.isDirectory()) {

            for (File file : f.listFiles())
                if (file.isDirectory())
                    calculateNoOfFiles(file.getPath());
                else
                    totalFiles++;

        }
        else
            totalFiles++;
    }

    private void sendDirectory(String directoryPath) throws Exception {

        File baseDir = new File(directoryPath);

        if (baseDir.isDirectory()) {

            for (File filePath : baseDir.listFiles()) {

                if (filePath.isDirectory())
                    sendDirectory(filePath.getPath());
                else
                    sendFile(filePath.getPath());
            }
        } else
            sendFile(directoryPath);
    }

    private void sendFile(String path) throws Exception{

        System.out.println("sending file " + path);
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        String meta = totalFiles + "#TRIFLEPROTOCOL#" + basepath + "#TRIFLEPROTOCOL#" + path;

        dataOutputStream.writeUTF(meta);

        // send file size
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
        log.log("File Sent : " + path);
    }
}
