package dev.granitkrasniqi.simpleserver;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import static dev.granitkrasniqi.simpleserver.SocketServer.StreamUtility.getBufferedReader;
import static dev.granitkrasniqi.simpleserver.SocketServer.StreamUtility.getPrintStream;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SocketServiceTest {
    private int connections = 0;
    private SocketServer connectionCounter;
    private SocketService ss;
    private static final int PORT_NUMBER = 1999;

    public SocketServiceTest() {
        connectionCounter = s -> connections++;
    }

    @Before
    public void setUp() throws Exception {
        connections = 0;
    }

    @Test
    public void testNoConnections() throws Exception {
        SocketServer connectionCounter1 = this.connectionCounter;
        ss = createSocketService(connectionCounter1);
        ss.close();
        assertEquals(0, connections);
    }

    @Test
    public void testOneConnection() throws Exception {
        ss = createSocketService(connectionCounter);
        connect();
        ss.close();
        assertEquals(1, connections);
    }

    @Test
    public void testManyConnections() throws Exception {
        ss = createSocketService(connectionCounter);
        for (int i = 0; i < 10; i++)
            connect();
        ss.close();
        assertEquals(10, connections);
    }

    @Test
    public void testSendMessage() throws Exception {
        ss = createSocketService(new HelloService());
        Socket s = new Socket("localhost", PORT_NUMBER);
        BufferedReader br = getBufferedReader(s);
        String answer = br.readLine();
        s.close();
        ss.close();
        assertEquals("Hello", answer);
    }

    @Test
    public void testReceiveMessage() throws Exception {
        ss = createSocketService(new EchoService());
        Socket s = new Socket("localhost", PORT_NUMBER);
        BufferedReader br = getBufferedReader(s);
        PrintStream ps = getPrintStream(s);
        ps.println("MyMessage");
        String answer = br.readLine();
        s.close();
        ss.close();
        assertEquals("MyMessage", answer);
    }

    private void connect() {
        try {
            Socket s = new Socket("localhost", PORT_NUMBER);
            sleep(30);
            s.close();
        } catch (IOException | InterruptedException e) {
            fail("Could not connect");
        }
    }

    private SocketService createSocketService(SocketServer socketServer) throws IOException {
        return new SocketService(socketServer, false, new PlainServerSocketFactory().createServerSocket(PORT_NUMBER));
    }

    private static class HelloService implements SocketServer {
        @Override
        public void serve(Socket s) {
            try {
                PrintStream ps = getPrintStream(s);
                ps.println("Hello");
            } catch (IOException e) {
            }
        }
    }

    private static class EchoService implements SocketServer {
        @Override
        public void serve(Socket s) {
            try {
                PrintStream ps = getPrintStream(s);
                BufferedReader br = getBufferedReader(s);
                String token = br.readLine();
                ps.println(token);
            } catch (IOException e) {
            }
        }
    }
}
