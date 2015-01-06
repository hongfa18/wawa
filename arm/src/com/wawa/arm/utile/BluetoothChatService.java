/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wawa.arm.utile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import com.wawa.arm.ARMActivity;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.utile.log.LogUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {
    // Debugging
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";

    // Unique UUID for this application
    //ssp 00001101-0000-1000-8000-00805F9B34FB
    //fa87c0d0-afac-11de-8a39-0800200c9a66
    //SDP 00001105-0000-1000-8000-00805F9B34FB
    /*NX403A 00001000-0000-1000-8000-00805f9b34fb,
		    00001001-0000-1000-8000-00805f9b34fb,
		    00001200-0000-1000-8000-00805f9b34fb,
		    00001800-0000-1000-8000-00805f9b34fb,
		    00001801-0000-1000-8000-00805f9b34fb,
		    0000110a-0000-1000-8000-00805f9b34fb,
		    0000110c-0000-1000-8000-00805f9b34fb,
		    00001112-0000-1000-8000-00805f9b34fb,
		    00001105-0000-1000-8000-00805f9b34fb,
		    0000111f-0000-1000-8000-00805f9b34fb,
		    0000112f-0000-1000-8000-00805f9b34fb,
		    00001116-0000-1000-8000-00805f9b34fb,
		    00001106-0000-1000-8000-00805f9b34fb,
		    0000112d-0000-1000-8000-00805f9b34fb,
		    00001132-0000-1000-8000-00805f9b34fb,
		    00001132-0000-1000-8000-00805f9b34fb,
		    00001101-0000-1000-8000-00805f9b34fb*/
    /*ThinkPad502 笔记本      00001000-0000-1000-8000-00805f9b34fb,
		    0000110a-0000-1000-8000-00805f9b34fb,
		    0000110c-0000-1000-8000-00805f9b34fb,
		    00001115-0000-1000-8000-00805f9b34fb,*/
    /*MacBook Air 0000110c-0000-1000-8000-00805f9b34fb,
		    0000110a-0000-1000-8000-00805f9b34fb,
		    00001117-0000-1000-8000-00805f9b34fb,
		    00001112-0000-1000-8000-00805f9b34fb,
		    0000111f-0000-1000-8000-00805f9b34fb,
		    00001200-0000-1000-8000-00805f9b34fb,
		    00001101-0000-1000-8000-00805f9b34fb,*/
    /*iphone4s 00000000-deca-fade-deca-deafdecacafe,
		    00001132-0000-1000-8000-00805f9b34fb,
		    0000110c-0000-1000-8000-00805f9b34fb,
		    0000110a-0000-1000-8000-00805f9b34fb,
		    0000112f-0000-1000-8000-00805f9b34fb,
		    0000111f-0000-1000-8000-00805f9b34fb,
		    00001200-0000-1000-8000-00805f9b34fb,*/
    /*ipad air 00000000-deca-fade-deca-deafdecacafe,
		    00001000-0000-1000-8000-00805f9b34fb,
		    0000110a-0000-1000-8000-00805f9b34fb,
		    0000110c-0000-1000-8000-00805f9b34fb,
		    0000110e-0000-1000-8000-00805f9b34fb,
		    0000110f-0000-1000-8000-00805f9b34fb,
		    0000111f-0000-1000-8000-00805f9b34fb,
		    00001132-0000-1000-8000-00805f9b34fb,
		    00001200-0000-1000-8000-00805f9b34fb,
		    00001203-0000-1000-8000-00805f9b34fb,*/
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 11;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	public static final int STATE_LISTEN_CLOSE = 12;
	public static final int STATE_SEND_MSG_ERROR = 4;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(ARMActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "---创建与监测设备通讯连接--- " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "---获取设备串口通信连接成功---");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(ARMActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(ARMActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(ARMActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ARMActivity.TOAST, "与监测设备建立通讯连接失败");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        setState(STATE_LISTEN_CLOSE);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(ARMActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ARMActivity.TOAST, "与监测设备通讯中断");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    
    private void sendMsgError() {
        setState(STATE_SEND_MSG_ERROR);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(ARMActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ARMActivity.TOAST, "指令下发失败");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            /**
             * 根据名称，UUID创建并返回BluetoothServerSocket，这是创建BluetoothSocket服务器端的第一步
             */
            Log.d(TAG, "----创建主机模式----");
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "----创建主机模式失败----", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "---主机模式阻塞接收失败accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_LISTEN_CLOSE:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            /**
             * 根据UUID创建并返回一个蓝牙从机对象BluetoothSocket
             */
            try {
            	//方式一 通过UUID建立连接
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                //方式二  通过遍历端口号 端口号连接时会有自动超时
				/*Method createBondMethod = device.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
				tmp = (BluetoothSocket)createBondMethod.invoke(device, 1);//这里端口为1  端口范围1~30，10.11.12.19保留端口*/
            } catch (Exception e) {//TODO 1 配对成功后建立设备时为什么会失败？？
                Log.e(TAG, "---创建连接ARM蓝牙设备对象失败---", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
            	if(!mmSocket.isConnected()){
            		mmSocket.connect();
            	}
                OMApplication.getInstance().setVal(CommonConsts.ARM_NAME, mmDevice.getName(), true);
                OMApplication.getInstance().setVal(CommonConsts.ARM_ADDR, mmDevice.getAddress(), true);
            } catch (IOException e) {//TODO 2 建立设备后为什么会导致打开通讯连接失败（情形一：设备不支持MY_UUID）
            	//对方会判断是否已配对，没有发送配对密码请求广播
            	//对方已取消配对 java.io.IOException: Connection reset by peer
            	//长时间不输人配对密码 java.io.IOException: Connection timed out
            	
            	//java.io.IOException: Device or resource busy
            	
            	//对方不支持MY_UUID或者该配对设备没在可连接范围（大概5s返回，底层控制不可设置） java.io.IOException: Service discovery failed
            	Log.e(TAG, "--获取设备串口通信支持失败（配对设备取消配对彼端密码输入超时or错误|对方设备不支持串口|对方设备不再周围|设备被别的蓝牙连接）--", e);
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                //TODO 2014-11-16注释
                //BluetoothChatService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {//TODO 3 打开设备的输入输出流为什么会终端：；处理：
                Log.e(TAG, "--打开通讯输入输出流失败--", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    byte[] truebytes = Arrays.copyOfRange(buffer, 0, bytes);
                    LogUtil.d("---ARM设备上报数据---"+Arrays.toString(truebytes));
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(ARMActivity.MESSAGE_READ, -1, -1,truebytes)
                            .sendToTarget();
                } catch (Exception e) {//TODO 4 为什么会导致读取失败：  ；处理：重走？流程
                	//对方关闭蓝牙或两个相连设备超过通讯距离 java.io.IOException: Software caused connection abort
                    Log.e(TAG, "---通讯中断---", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);//TODO 4 为什么会导致写失败

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(ARMActivity.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "---传出数据失败---", e);
                sendMsgError();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭蓝牙连接Socket失败", e);
            }
        }
    }
}
