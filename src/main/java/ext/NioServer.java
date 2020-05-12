package ext;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
    public static void main(String[] args) throws Exception {
        Selector serverSelector = Selector.open();
        Selector clientSelector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9527));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
        
        new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                while (true) {
                    if (serverSelector.select(1000) > 0) {
                        Iterator<SelectionKey> iterator = serverSelector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey next = iterator.next();
                            ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                            if (next.isAcceptable()) {
                                SocketChannel socketChannel = channel.accept();
                                socketChannel.configureBlocking(false);
                                socketChannel.register(clientSelector, SelectionKey.OP_READ);
                                System.out.println(socketChannel.getLocalAddress() + " online");
                            }
                            iterator.remove();
                        }
                    }
                    
                }
                
                
            }
        }, "accept").start();

        
        
        new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                while (true) {
                    if (clientSelector.select(1000) > 0) {
                        Iterator<SelectionKey> iterator = clientSelector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey next = iterator.next();
                            SocketChannel channel = (SocketChannel) next.channel();
                            if (next.isReadable()) {
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                int read = channel.read(buffer);
                                if (read == -1) {
                                    next.cancel();
                                    channel.close();
                                    break;
                                }
                                buffer.flip();
                                String s = new String(buffer.array(), 0, buffer.limit());
                                System.out.println("server read " + s);
                                buffer.clear();
                            }
                            iterator.remove();
                        }
                    }
                    
                }
                
            }
        }, "work").start();


    }
    
    
}
