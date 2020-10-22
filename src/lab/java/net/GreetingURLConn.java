package lab.java.net;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class GreetingURLConn {


    public static void main(String[] args) {
        testRead();
    }

    private static void testRead() {
        InputStream input = null;
        try {
            // 连接
            URL url = new URL("http://www.baidu.com");
            URLConnection connection = url.openConnection();

            // 读取
            input = connection.getInputStream();
            InputStream raw = new BufferedInputStream(input);
            Reader r = new InputStreamReader(raw);
            StringBuffer result = new StringBuffer();
            int c;
            while ((c = r.read()) > 0) {
                result.append((char) c);
            }

            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
