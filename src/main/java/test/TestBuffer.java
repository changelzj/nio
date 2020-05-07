package test;

import org.junit.Test;

import java.nio.ByteBuffer;

public class TestBuffer {
    /**
     * 缓冲区，底层是数组，负责数据的存储
     * 基本数据类型除布尔型，都有对应类型的缓冲区
     * 都是alloc方法创建指定大小的缓冲区
     * 
     * 缓冲区的四个核心属性
     *      mark
     *      capacity：缓冲区最大存储的容量，一旦声明不能改变
     *      limit 界限，缓冲区中可以操作数据的大小，limit后不能操作(读写)
     *      position 缓冲区中正在操作数据的位置
     *      
     *      
     *      
     *      0 <= mark <= position <= limit <= capacity
     */
    @Test
    public void test() {
        // 分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        System.out.println("--------init---------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        
        byteBuffer.put((byte)'6');
        byteBuffer.put("abce".getBytes());

        System.out.println("---------put--------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        
        // 切换到读模式
        byteBuffer.flip();

        System.out.println("---------flip--------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        // 利用get读取数据，get后position位置移动
        byte[] bytes = new byte[byteBuffer.limit()];
        // 读取到数组
        byteBuffer.get(bytes);

        System.out.println("get: " + new String(bytes, 0, bytes.length));

        System.out.println("--------get---------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        
        // 继续回到读模式，可重复读取
        byteBuffer.rewind();

        System.out.println("---------rewind--------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        
        // 清空缓冲区（数据并没有清空，但处于被遗忘状态，位置界限都被重置）
        byteBuffer.clear();

        System.out.println("---------clear--------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println("get: " + new String(bytes, 0, bytes.length));
    }


    /**
     * mark:标记，当前position的位置，通过reset恢复到mark的位置
     */
    @Test
    public void test2() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("ABCDE".getBytes());

        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes, 0, 2);
        System.out.println("get: " + new String(bytes, 0, 2));
        byteBuffer.mark();
        
        byteBuffer.get(bytes, 2, 2);
        System.out.println("get: " + new String(bytes, 2, 2));
        byteBuffer.reset();
        
        System.out.println(byteBuffer.position());
        // 如果缓冲区内还有剩余的数据
        if (byteBuffer.hasRemaining()) {
            // 获取可以操作的数量
            System.out.println(byteBuffer.remaining());
        }
        
        while (byteBuffer.hasRemaining()) {
            System.out.println(byteBuffer.get());
        }
    }

    /**
     *  直接缓冲区和非直接缓冲区
     *  非直接缓冲区：allocate方法分配大小，将缓冲区建立在JVM内存中
     *  直接缓冲区：allocateDirect方法分配大小, 将缓冲区建立在操作系统的物理内存中，一定情况下可以提升效率，
     *  但是消耗资源较大，不易控制
     *  只有ByteBuffer支持直接缓冲区
     */
    @Test
    public void test3() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println(byteBuffer.isDirect());
    }

    /**
     * BufferUnderflowException
     * 存取顺序和类型不符，会BufferUnderflowException
     */
    @Test
    public void test4() {
        ByteBuffer buffer = ByteBuffer.allocate(10000);
        buffer.putInt(100);
        buffer.putChar('猪');
        buffer.putLong(100000L);
        
        buffer.flip();

        System.out.println(buffer.getLong());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getLong());
    }

    /**
     * 只读的
     */
    @Test
    public void test5() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put("武汉加油".getBytes());
        buffer.flip();
        buffer = buffer.asReadOnlyBuffer();
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
        buffer.rewind();
        buffer.put("武汉加油".getBytes());
    }
    
    
    
    
}
