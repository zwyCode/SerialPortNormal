package com.lingware.serialport;

import android.renderscript.ScriptGroup;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wuyiz on 2017/8/3.
 */

public class SerialPort {
    private FileDescriptor mFd;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

    public SerialPort(File path, int baudrete, int flags) throws IOException {
        mFd = open(path.getAbsolutePath(), baudrete, flags);

        if(mFd == null){
            throw new IOException();
        }

        fileInputStream = new FileInputStream(mFd);
        fileOutputStream = new FileOutputStream(mFd);
    }

    public FileInputStream getInputSteam() {
        return fileInputStream;
    }

    public FileOutputStream getOutputStream() {
        return fileOutputStream;
    }

    public void closeSerialPort() {
        try {
            if(fileOutputStream != null)
                fileOutputStream.close();

            if(fileInputStream != null)
                fileInputStream.close();
        } catch (IOException e) {

        }

        close();
    }

    private native static FileDescriptor open(String path, int baudrate, int flags);
    public native static void close();

    static {
        System.loadLibrary("serial_port");
    }
}
