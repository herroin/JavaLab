package lab.java.net;

import java.io.*;
import java.net.*;

public class GreetingUDP {

    private static final String LOCAL_HOST = "localhost";
    private static final int PORT_SERVER = 2049;


    public static void main(String[] args) {
        try {
            // 开启服务端
            Server server = new Server();
            server.start();

            // 开启客户端1
            Client client1 = new Client(1);
            client1.start();

            // 开启客户端2
            Client client2 = new Client(2);
            client2.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static class Client extends Thread {

        private int num;

        Client(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            String serverName = LOCAL_HOST;
            int port = PORT_SERVER;
            try {
                // 连接
                DatagramSocket client = new DatagramSocket();
                client.connect(InetAddress.getByName(serverName), port);
                System.out.println("客户端-" + num + "-log--连接成功，"
                        + "LocalSocketAddress：" + client.getLocalSocketAddress()
                        + "，RemoteSocketAddress：" + client.getRemoteSocketAddress()
                        + "，LocalAddress：" + client.getLocalAddress()
                        + "，InetAddress：" + client.getInetAddress());

                // 发送
                byte[] data = ("=====Hello from " + client.getLocalSocketAddress() + "====").getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length);
                client.send(packet);

                // 接收
                byte[] buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                client.receive(packet);
                String resp = new String(packet.getData(), packet.getOffset(), packet.getLength());
                System.out.println(resp);


                // 关闭
                client.disconnect();
                client.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Server extends Thread {

        private DatagramSocket datagramSocket;

        Server() throws SocketException {
            datagramSocket = new DatagramSocket(PORT_SERVER);
            datagramSocket.setSoTimeout(10000);
            System.out.println("服务端-0-log--创建socket ：" + datagramSocket.getLocalSocketAddress());
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    // 连接
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);

                    System.out.println("服务端-0-log--连接成功，"
                            + "LocalSocketAddress：" + datagramSocket.getLocalSocketAddress()
                            + "，RemoteSocketAddress：" + datagramSocket.getRemoteSocketAddress()
                            + "，LocalAddress：" + datagramSocket.getLocalAddress()
                            + "，InetAddress：" + datagramSocket.getInetAddress());

                    // 接收
                    String data = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    System.out.println("服务端-0-log--读取到数据：" + data);

                    // 回复
                    String str_send = "=====Hello too from " + datagramSocket.getLocalSocketAddress() + "====";
                    packet = new DatagramPacket(str_send.getBytes(), str_send.length(), packet.getAddress(), packet.getPort());
                    datagramSocket.send(packet);

                } catch (SocketTimeoutException s) {
                    System.out.println("服务端-0-log--DatagramSocket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}
