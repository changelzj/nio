package chat;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ChatClient {
    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8800);
        SocketChannel socketChannel = SocketChannel.open(address);
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        SocketAddress localAddress = socketChannel.getLocalAddress();
        System.out.println("client init " + localAddress);
        
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println("请输入");
                    Scanner scanner = new Scanner(System.in);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(line.getBytes());
                        socketChannel.write(byteBuffer);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }

                
            }
        }).start();
        
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);

                while (true) {
                    if (selector.select(1000) > 0) {
                        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isReadable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                channel.read(buffer);
                                buffer.flip();
                                String s = new String(buffer.array(), 0, buffer.limit());
                                System.out.println("接收消息 " + s);
                            }
                            keyIterator.remove();
                        }
                        
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        
        
        
        
    }
}
