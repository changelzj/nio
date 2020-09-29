package chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
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

                    keyIterator.remove();
                }
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
    
    
    private static void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Selector selector = key.selector();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            socketChannel.read(buffer);

            for (SelectionKey selectionKey : selector.keys()) {
                Channel channel = selectionKey.channel();
                if (channel instanceof SocketChannel && channel != socketChannel) {
                    SocketChannel cast = (SocketChannel) channel;

                    buffer.rewind();
                    cast.write(buffer);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            key.channel();
            try {
                socketChannel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        

    }



    
    
    
    
}
