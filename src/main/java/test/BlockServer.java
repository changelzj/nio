package test;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * nio的阻塞式网络通信
 */
public class BlockServer {
    
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(5000);
        serverSocketChannel.bind(address);
        SocketChannel socketChannel = serverSocketChannel.accept();
        
        read(socketChannel);
        socketChannel.shutdownInput();
        
        write(socketChannel);
        socketChannel.shutdownOutput();
        
        socketChannel.close();
        serverSocketChannel.close();
    }


    public static void write(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap("hello client".getBytes());
        socketChannel.write(buffer);
    }
    
    public static void read(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            String s = new String(buffer.array(), 0, buffer.limit());
            System.out.println(s);
            buffer.clear();
        }
    }
    
}




