package chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class GroupChatServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8800));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        while (true) {
            int select = selector.select(1000);
            if (select > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        accept(key);
                    }
                    else if (key.isReadable()) {
                        read(key);
                    }
                    else if (key.isWritable()) {
                        write(key);
                    }
                    keyIterator.remove();
                }
            } else {
                //System.out.println("服务器等待连接 " + System.currentTimeMillis());
            }
        }
        
        
    }


    private static void accept(SelectionKey key) throws Exception {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        Selector selector = key.selector();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println(socketChannel.getRemoteAddress() + " 上线了");
    }
    
    
    private static void read(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Selector selector = key.selector();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        
        for (SelectionKey selectionKey : selector.keys()) {
            Channel channel = selectionKey.channel();
            if (channel instanceof SocketChannel && channel != socketChannel) {
                SocketChannel cast = (SocketChannel) channel;
                
                buffer.rewind();
                cast.write(buffer);
                //buffer.clear();
            }
        }
    }


    private static void write(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        
    }
    
    
    
    
}
