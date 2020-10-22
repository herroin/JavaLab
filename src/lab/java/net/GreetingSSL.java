package lab.java.net;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class GreetingSSL {

    private static final int SEV_PORT = 2090;
    // 服务器端授权的用户名和密码
    private static final String USER_NAME = "Lily";
    private static final String PASSWORD = "Lily";


    public static void main(String[] args) {
        // 需要分别配置客户端和服务端的证书，才可以运行
        try {
            // 开启服务端
            SSLServer server = new SSLServer();
            server.start();
            // 开启客户端1
            SSLClient client1 = new SSLClient();
            client1.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class SSLServer extends Thread {


        // 服务器端保密内容
        private static final String SECRET_CONTENT = "This content is secret!";


        private SSLServerSocket serverSocket = null;

        SSLServer() throws IOException {
            // 通过套接字工厂，获取一个服务器端套接字
            SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) socketFactory.createServerSocket(SEV_PORT);
        }

        public void run() {
            while (!interrupted()) {
                try {
                    System.out.println("Waiting for connection...");
                    // 服务器端套接字进入阻塞状态，等待来自客户端的连接请求
                    SSLSocket socket = (SSLSocket) serverSocket.accept();

                    // 获取服务器端套接字输入流
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // 从输入流中读取客户端用户名和密码
                    String userName = input.readLine();
                    String password = input.readLine();

                    // 获取服务器端套接字输出流
                    PrintWriter output = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream()));

                    // 对请求进行认证，如果通过则将保密内容发送给客户端
                    if (userName.equals(USER_NAME) && password.equals(PASSWORD)) {
                        output.println("Welcome, " + userName);
                        output.println(SECRET_CONTENT);
                    } else {
                        output.println("Authentication failed, you have no access to the content...");
                    }

                    // 关闭流资源和套接字资源
                    output.close();
                    input.close();
                    socket.close();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private static class SSLClient {

        private SSLSocket socket = null;

        SSLClient() throws IOException {
            // 通过套接字工厂，获取一个客户端套接字
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket("localhost", SEV_PORT);
        }

        void connect() {
            try {
                // 获取客户端套接字输出流
                PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                // 将用户名和密码通过输出流发送到服务器端
                output.println(USER_NAME);
                output.println(PASSWORD);
                output.flush();

                // 获取客户端套接字输入流
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // 从输入流中读取服务器端传送的数据内容，并打印出来
                String response = input.readLine();
                response += "\n " + input.readLine();
                System.out.println(response);

                // 关闭流资源和套接字资源
                output.close();
                input.close();
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }


}
