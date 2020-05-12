package test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * nio的非阻塞式网络通信
 *
 * nio的网络通信，三要素
 *
 * 通道 
 * - Channel 接口：
 *      - SelectableChannel
 *           - SocketChannel
 *           - ServerSocketChannel
 *           - DatagramChannel
 *           - Pipe.SinkChannel
 *           - Pipe.SourceChannel
 *
 *   FileChannel不能切换成非阻塞模式，非阻塞的IO都是相对于网络而言的
 *
 * Buffer:数据存取
 *
 * Selector:SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 * 
 * 
 *  SelectionKey:通道和选择器之间的关系，选择器监控通道的什么状态（读 写 连接 接收），
 *  如果监控的不止一种状态，使用位或操作符连接
 *  
 *  
 */
public class NonBlockServer {
    
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8087));
        // 声明一个选择器
        Selector selector = Selector.open();
        // 通道注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 轮询获取选择器上准备就绪的事件
        while (true) {
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待连接");
                continue;
            }
            // 关注事件的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 判断什么事件准备就绪
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("生成socket " + socketChannel);
                }
                if (key.isReadable()) {
                    // 获取当前选择器上读就绪的通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    while (true) {
                        int read = channel.read(byteBuffer);
                        if (read > 0) {
                            byteBuffer.flip();
                            String s = new String(byteBuffer.array(), 0, byteBuffer.limit());
                            System.out.println(s);
                            byteBuffer.clear();
                        } 
                        else if (read == 0){
                            break;
                        } 
                        else if (read == -1) {
                            System.out.println("断开连接 " + channel.toString());
                            channel.close();
                            key.cancel();
                            break;
                        }
                        
                    }
                }
                
                iterator.remove();
            }

        }
    }
}
