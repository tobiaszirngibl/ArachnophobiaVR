package com.example.tobias.arachnophobiavr;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Julien on 01.10.2017.
 */

class UnityConnection {

    private int DEBUG_LEVEL = 1;

    private int port = 5005;

    private InetAddress address;
    private DatagramSocket sock;

    private ReceiveThread rt;
    private ExecutorService cachedPool;

    private short lastReceived = -1;

    public void init(String ip) {
        setIp(ip);
        initSocket();
        rt = new ReceiveThread();
        rt.start();
        cachedPool = Executors.newCachedThreadPool();
    }

    public short receive() {
        return lastReceived;
    }

    public void send(int s) {
        cachedPool.submit(createSendRunnable((short)(s)));
    }

    private void setIp(String ip)
    {
        try
        {
            if(DEBUG_LEVEL > 0) Log.d("UNITY", "ip: " + ip);
            address = InetAddress.getByName(ip);
            if(DEBUG_LEVEL > 0) Log.d("UNITY", "Success! IP stuff done!");
        }
        catch (Exception e)
        {
            if(DEBUG_LEVEL > 0) Log.d("UNITY", "Failure! Could not do IP stuff!");
            if(DEBUG_LEVEL > 0) Log.d("UNITY", e.toString());
        }
    }

    private void initSocket()
    {
        try
        {
            sock = new DatagramSocket(port);
            if(DEBUG_LEVEL > 0) Log.d("UNITY", "Success! Socket initialized!");
        }
        catch (Exception e)
        {
            if(DEBUG_LEVEL > 0) Log.d("UNITY", "Failure! Could not init socket! " + e.toString());
        }
    }

    private class ReceiveThread extends Thread {

        public void run() {

            byte message[];
            DatagramPacket packet;

            while(this.isAlive()) {
                message = new byte[1];
                packet = new DatagramPacket(message, message.length, address, port);

                lastReceived = -1;
                try
                {
                    sock.receive(packet);
                    lastReceived = (short)(packet.getData()[0]);
                    if(DEBUG_LEVEL > 0) Log.d("UNITYR", "message received: " + lastReceived);
                }
                catch (Exception e)
                {
                    if(DEBUG_LEVEL > 0) Log.d("UNITY", "could not receive packet: " + e.toString());
                }

                try
                {
                    sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private Runnable createSendRunnable(final short toSend){

        return new Runnable(){

            public void run(){
                byte message[] = new byte[1];
                message[0] = (byte)(toSend);

                DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
                if(DEBUG_LEVEL > 0) Log.d("UNITYS", "sending: " + toSend);

                try
                {
                    sock.send(packet);
                    if(DEBUG_LEVEL > 0) Log.d("UNITY", "packet sent");
                }
                catch (Exception e)
                {
                    if(DEBUG_LEVEL > 0) Log.d("UNITY", "could not send packet: " + e.toString());
                }
            }
        };
    }
}
