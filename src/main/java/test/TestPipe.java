package test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * 管道：两个线程之间的单向数据连接
 * pipe有一个source通道和一个sink通道，数据会被写到sink通道，从source通道读取
 */
public class TestPipe {
    public static void main(String[] args) throws Exception {
        Pipe pipe = Pipe.open();
        
        Pipe.SinkChannel sinkChannel = pipe.sink();
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("hellloworld".getBytes());
        byteBuffer.flip();
        sinkChannel.write(byteBuffer);
        
        Pipe.SourceChannel sourceChannel = pipe.source();
        byteBuffer.flip();
        int len = sourceChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array(), 0, len));
        
        sinkChannel.close();
        sourceChannel.close();
    }
}
