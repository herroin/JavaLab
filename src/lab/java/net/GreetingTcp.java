package lab.java.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GreetingTcp {

    private static final String LOCAL_HOST = "localhost";
    private static final int PORT_SERVER = 2048;


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
                // 连接(客户端端口由系统随机分配)
                Socket client = new Socket(serverName, port);
                System.out.println("客户端-" + num + "-log--连接成功，"
                        + "LocalSocketAddress：" + client.getLocalSocketAddress()
                        + "，RemoteSocketAddress：" + client.getRemoteSocketAddress()
                        + "，LocalAddress：" + client.getLocalAddress()
                        + "，InetAddress：" + client.getInetAddress());

                // 发送
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);
                out.writeUTF("=====Hello from " + client.getLocalSocketAddress() + "====");

                // 接收
                InputStream inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);
                System.out.println(in.readUTF());

                // 关闭
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Server extends Thread {

        ServerSocket serverSocket;

        Server() throws IOException {
            serverSocket = new ServerSocket(PORT_SERVER);
            serverSocket.setSoTimeout(10000);
            System.out.println("服务端-0-log--创建欢迎socket，"
                    + "LocalSocketAddress：" + serverSocket.getLocalSocketAddress()
                    + "，InetAddress：" + serverSocket.getInetAddress());
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    // 连接
                    Socket server = serverSocket.accept();
                    System.out.println("服务端-0-log--创建连接socket，"
                            + "LocalSocketAddress：" + server.getLocalSocketAddress()
                            + "，RemoteSocketAddress：" + server.getRemoteSocketAddress()
                            + "，LocalAddress：" + server.getLocalAddress()
                            + "，InetAddress：" + server.getInetAddress());

                    // 接收
                    DataInputStream in = new DataInputStream(server.getInputStream());
                    System.out.println(in.readUTF());

                    // 回复
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF("=====Hello too from " + server.getLocalSocketAddress() + "====");

                    // 关闭
                    server.close();
                } catch (SocketTimeoutException s) {
                    System.out.println("服务端-0-log--ServerSocket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }


}
