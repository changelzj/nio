package test;

import org.junit.Test;


import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Date;
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
 */
public class NonBlockServer {
    
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8087));
        // 声明一个选择器
        Selector selector = Selector.open();
        // 通道注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 轮询获取选择器上准备就绪的事件
        while (selector.select() > 0) {
            
            // 关注事件的集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 判断什么事件准备就绪
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                else if (key.isReadable()) {
                    // 获取当前选择器上读就绪的通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    while (channel.read(byteBuffer) != -1) {
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                        byteBuffer.clear();
                    }
                    channel.shutdownInput();
                }
                
                iterator.remove();
            }

        }
    }
}
