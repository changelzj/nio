package test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BlockClient {

    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
        SocketChannel socketChannel = SocketChannel.open(address);
        write(socketChannel);
        socketChannel.shutdownOutput();
        read(socketChannel);
        socketChannel.shutdownInput();
        socketChannel.close();
    }
    
    public static void write(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap("hello server".getBytes());
        socketChannel.write(buffer);
    }

    public static void read(SocketChannel socketChannel) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            String s = new String(byteBuffer.array(), 0, byteBuffer.limit());
            System.out.println(s);
            byteBuffer.clear();
        }
    }
    
}



