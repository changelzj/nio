package test;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

public class TestDatagram {
   
    public static void main(String[] s) throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 2054);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            buffer.put(scanner.next().getBytes());
            buffer.flip();
            channel.send(buffer, socketAddress);
            buffer.clear();
        }
        channel.close();
    }
    
    @Test
    public void recive() throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(2054));
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
            }
            iterator.remove();
        }

        
        
    }
}
