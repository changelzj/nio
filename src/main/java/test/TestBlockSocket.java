package test;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * nio的阻塞式网络通信
 */
public class TestBlockSocket {
    
    @Test
    public void client() throws Exception {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
        SocketChannel socketChannel = SocketChannel.open(address);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileChannel fileChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }
        socketChannel.shutdownOutput();
        int len = 0;
        while ((len = socketChannel.read(buffer)) != -1) {
            buffer.flip();
            String s = new String(buffer.array(), 0, len);
            System.out.println(s);
            buffer.clear();
        }
        
        fileChannel.close();
        socketChannel.close();
    }
    
    @Test
    public void server() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(5000);
        serverSocketChannel.bind(address);
        SocketChannel socketChannel = serverSocketChannel.accept();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileChannel fileChannel = FileChannel.open(Paths.get("5.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }

        socketChannel.shutdownInput();
        
        buffer.put("谢谢".getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        
        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }
    
    
}




