package com.suntha.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceivingService extends Service<Boolean> {

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static final String PATH_SEPARATOR = System.getProperty("file.separator");

    private static final String PROTOCOL = "#TRIFLEPROTOCOL#";

    private int port;

    private String basepath;

    private String server;

    public final static int FILE_SIZE = Integer.MAX_VALUE;

    public ReceivingService(int port, String basepath, String server) {
        this.port = port;
        this.basepath = basepath;
        this.server = server;
    }

    @Override
    protected Task<Boolean> createTask() {

        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                main();
                return Boolean.TRUE;
            }
        };

    }

    private void receiveDirectory(String dirPath) throws Exception {

        String meta;
        int totalFiles = -1;
        String basepath = null;
        String filePath = null;
        String newFilePath = null;

        do {
            meta = dataInputStream.readUTF();
            if (totalFiles == -1)
                totalFiles = Integer.valueOf(meta.split(PROTOCOL)[0]);
            totalFiles--;
            basepath = meta.split(PROTOCOL)[1];
            filePath = meta.split(PROTOCOL)[2];
            newFilePath = filePath.replace(basepath, dirPath);
            receiveFile(newFilePath);

        }
        while (totalFiles > 0);
    }

    private void receiveFile(String fileName) throws Exception {

        System.out.println("Receiving File " + fileName);
        int bytes = 0;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException fnfe) {
            String dirpath = fileName.substring(0, fileName.lastIndexOf(PATH_SEPARATOR));
            File dir = new File(dirpath);
            dir.mkdirs();
            fileOutputStream = new FileOutputStream(fileName);
        }

        long size = dataInputStream.readLong();     // read file size
        byte[] buffer = new byte[4 * 1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }

    public void main() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket + " connected.");
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            receiveDirectory(basepath);

            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
