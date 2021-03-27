package dev.granitkrasniqi.simpleserver;

import java.io.*;
import java.net.Socket;

public interface SocketServer {
    void serve(Socket s) throws IOException;

    class StreamUtility {
        public static PrintStream getPrintStream(Socket s) throws IOException {
            OutputStream os = s.getOutputStream();
            return new PrintStream(os);
        }

        public static BufferedReader getBufferedReader(Socket s) throws IOException {
            InputStream is = s.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            return new BufferedReader(isr);
        }
    }
}
