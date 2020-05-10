package test;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * nio的阻塞式网络通信
 */
public class BlockServer {
    
    public static void main(String[] s) throws Exception {
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




