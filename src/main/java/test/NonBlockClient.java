package test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NonBlockClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8087));
        ByteBuffer byteBuffer = ByteBuffer.wrap("hello world".getBytes());
        socketChannel.write(byteBuffer);
        TimeUnit.SECONDS.sleep(9999);
    }
}
