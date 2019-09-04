package test;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


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
        
        while (in.read(byteBuffer) != -1) {
            byteBuffer.flip();
            out.write(byteBuffer);
            byteBuffer.clear();
        }
        
        in.close();
        out.close();
        fileInputStream.close();
        fileOutputStream.close();
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

}
