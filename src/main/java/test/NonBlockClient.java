package test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

public class NonBlockClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8087));
        // 切换为非阻塞模式
        socketChannel.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(new Date().toString().getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();

        socketChannel.close();
    }
}
