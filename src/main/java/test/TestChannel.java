package test;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.stream.Stream;


/**
 * 
 * 
 * 通道：IO源与目标打开的连接,在NIO，负责缓冲区中数据的传输
 * java.nio.channels.Channel主要实现类
 *      FileChannel 本地
 *      SocketChannel 网络 TCP
 *      ServerSocketChannel 网络 TCP
 *      DatagramChannel 网络 UDP
 * 
 */
public class TestChannel {
    
    /**
     * 获取通道
     * 1.支持通道的类提供getChannel方法(Input/OutputStream)
     * 2.java7，NIO.2 针对各个通道提供了静态方法open
     * 3.java7，NIO.2 Files.newByteChannel()
     */
    @Test
    public void test() throws IOException {
        // 流中获取通道完成复制
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        
        FileChannel in = fileInputStream.getChannel();
        FileChannel out = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        
        while (true) {
            int len = in.read(byteBuffer);
            if (len != -1) {
                byteBuffer.flip();
                out.write(byteBuffer);
                byteBuffer.clear();
            } else {
                break;
            }
            
        }
        
        in.close();
        out.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    /**
     * 直接在堆外内存修改文件
     */
    @Test
    public void mappedBuffer() throws Exception {
        RandomAccessFile file = new RandomAccessFile("a.txt", "rw");
        FileChannel channel = file.getChannel();
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        buffer.put(0, (byte)'b');
        buffer.put(4, (byte)'9');
        file.close();
    }

    /**
     * 内存映射文件的方式复制文件，将文件区域直接复制到物理内存，无需使用通道
     * 
     *      Paths.get() 路径，支持传入多个可变参数拼接
     *      
     *      StandardOpenOption 枚举:
     *      
     *      StandardOpenOption.READ 读模式
     *      StandardOpenOption.WRITE 写模式
     *      StandardOpenOption.CREATE 不存在就创建，存在就覆盖
     *      StandardOpenOption.CREATE_NEW 不存在就创建，存在就报错
     */
    @Test
    public void test2() throws IOException {
        
        FileChannel in = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("2.txt"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);
        
        MappedByteBuffer inBuffer = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
        MappedByteBuffer outBuffer = out.map(FileChannel.MapMode.READ_WRITE, 0, in.size());

        byte [] bytes = new byte[inBuffer.limit()];
        inBuffer.get(bytes);
        outBuffer.put(bytes);
        
        in.close();
        out.close();
    }

    /**
     * 通道之间的数据传输（直接缓冲区）
     */
    @Test
    public void test3() throws IOException {
        FileChannel in = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("2.txt"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);
        in.transferTo(0, in.size(), out);
        // 另一种写法： out.transferFrom(in, 0, in.size());
        in.close();
        out.close();
    }

    /**
     * 分散与聚集
     * 分散读取：将通道中的数据分散到各个缓冲区中，按照缓冲区的顺序，从通道中取出数据依次将缓冲区填满
     * 聚集写入：将多个缓冲区中的数据都聚集到通道中，按照缓冲区的顺序，写入position到limit之间的数据到通道中
     */
    @Test
    public void test4() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("1.txt","rw");
        FileChannel rchannel = raf.getChannel();

        RandomAccessFile raf2 = new RandomAccessFile("3.txt","rw");
        FileChannel wchannel = raf2.getChannel();

        ByteBuffer[] byteBuffers = new ByteBuffer[3];
        byteBuffers[0] = ByteBuffer.allocate(10);
        byteBuffers[1] = ByteBuffer.allocate(12);
        byteBuffers[2] = ByteBuffer.allocate(3);
        
        while (rchannel.read(byteBuffers) != -1) {
            Stream.of(byteBuffers).forEach(ByteBuffer::flip);
            wchannel.write(byteBuffers);
            Stream.of(byteBuffers).forEach(ByteBuffer::clear);
        }
        
        rchannel.close();
        wchannel.close();
    }

    /**
     * 字符集
     * 编码：字符串-》字节数组
     * 解码：字节数组-》字符串
     */
    @Test
    public void testcharset() {
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();
        stringCharsetSortedMap.forEach((k,v) -> System.out.println(k+"-"+v));
    }

    /**
     * 获取编码器和解码器
     */
    @Test
    public void testcharset2() throws Exception {
        Charset gbk = Charset.forName("GBK");
        CharsetEncoder encoder = gbk.newEncoder();
        CharsetDecoder decoder = gbk.newDecoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("祝福祖国");
        charBuffer.flip();

        ByteBuffer byteBuffer = encoder.encode(charBuffer);
        for (byte b : byteBuffer.array()) {
            System.out.println(b);
        }

        CharBuffer charBuffer1 = gbk.decode(byteBuffer);

        for (char c : charBuffer1.array()) {
            System.out.println(c);
        }
    }

}
