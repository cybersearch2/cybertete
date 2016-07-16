/**
    Copyright (C) 2015  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.cybertete.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.junit.*;

/**
 * DatagramTest
 * @author Andrew Bowley
 * 30 Oct 2015
 */
public class DatagramTest
{
    private final static int PACKETSIZE = 100 ;
    private DatagramSocket sendSocket;
    private DatagramSocket recvSocket;

    @Before
    public void setUp() throws SocketException
    {
        sendSocket = new DatagramSocket();
        recvSocket = new DatagramSocket(50001);
    }
 
    @After
    public void shutDown()
    {
        sendSocket.close();
        recvSocket.close();
    }
    
    @Test
    public void doDatagramTest() throws IOException, InterruptedException
    {
        String message = "Test message";
        final Object monitor = new Object();
        Runnable messageHandler = new Runnable(){

            @Override
            public void run()
            {
                // Create a packet
                DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
                // Receive a packet (blocking)
                try
                {
                    recvSocket.receive(packet);
                    System.out.println( packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData(), 0, packet.getLength()) ) ;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                synchronized(monitor)
                {
                    monitor.notifyAll();
                }
            }
        };
        new Thread(messageHandler).start();
        InetAddress address = InetAddress.getLocalHost();

        DatagramPacket packet= new DatagramPacket(message.getBytes(), message.length(), address, 50001);
        sendSocket.send(packet);
    }

}
