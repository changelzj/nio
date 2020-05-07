package test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BlockClient {

    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8564);
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
}
