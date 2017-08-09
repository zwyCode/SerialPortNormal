package com.lingware.serialport;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by wuyiz on 2017/8/3.
 */

public class SerialPortUtil {
    private SerialPort serialPort;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;
    private ByteBuffer mInputByteBuffer, mOutputByteBuffer;
    private static final int SERIAL_PORT_BUFFERSIZE = 1024;
    private onDataReceiveListener mOnDataReceive;
    private receiveThread dataReceiveThread;

    private boolean isRunningFlag = false;

    public interface onDataReceiveListener {
        void onReceiveData(String data);
    }

    public void openSerialPort() {
        try {
            serialPort = new SerialPort(new File(SerialPortParameter.PORT), SerialPortParameter.BAUDRATE, SerialPortParameter.FLAGS);
            fileInputStream = serialPort.getInputSteam();
            fileOutputStream = serialPort.getOutputStream();
            mInputByteBuffer = ByteBuffer.allocate(SERIAL_PORT_BUFFERSIZE);
            mOutputByteBuffer = ByteBuffer.allocate(SERIAL_PORT_BUFFERSIZE);
            isRunningFlag = true;
            dataReceiveThread = new receiveThread();
            dataReceiveThread.start();
        } catch (IOException e) {
            isRunningFlag = false;
            e.printStackTrace();
        }
    }

    public void closeSerialPort(){
        isRunningFlag = false;

        if(dataReceiveThread != null) {
            dataReceiveThread.interrupt();
            dataReceiveThread = null;
        }

        if(serialPort != null) {
            serialPort.closeSerialPort();
            serialPort = null;
        }
    }

    private String byteArrayToString(byte[] buffer,int length) {
        String h = "";
        for (int i = 0; i < buffer.length && (i < length); i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            temp = "0x" + temp;
            h = h + " " + temp;
        }
        return h;
    }

    public void setOndataReceive(onDataReceiveListener dataReceive) {
        mOnDataReceive = dataReceive;
    }

    private class receiveThread extends Thread {
        @Override
        public synchronized void start() {
            super.start();

            while(isRunningFlag) {
                byte[] readData = new byte[1024];

                if(fileInputStream == null) {
                    return;
                }

                try {
                    int size = fileInputStream.read(readData);
                    String result = byteArrayToString(readData, size);
                    if(size > 0 && isRunningFlag) {
                        Log.e("ZWY", "read data -----> : " + byteArrayToString(readData, size));
                        Log.e("ZWY", "read data2 -----> : " + new String(readData, 0, size));
                    }

                    if(mOnDataReceive != null) {
                        mOnDataReceive.onReceiveData(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendCmds(String cmd) {
        boolean result = false;
        byte[] mBufferCmd = (cmd + "\r\n").getBytes();

        try{
            if(fileOutputStream != null) {
                fileOutputStream.write(mBufferCmd);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        String tail = "\r\n";
        byte[] tailBuffer = tail.getBytes();
        byte[] mBufferTemp = new byte[mBuffer.length+tailBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);

        try {
            if (fileOutputStream != null) {
                fileOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
