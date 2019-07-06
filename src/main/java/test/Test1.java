package test;

import org.junit.Test;

import java.nio.ByteBuffer;

public class Test1 {
    /**
     * 缓冲区，底层是数组，负责数据的存储
     * 基本数据类型除布尔型，都有对应类型的缓冲区
     * 都是alloc方法创建指定大小的缓冲区
     * 
     * 缓冲区的四个核心属性
     *      capacity：最大存储的容量，一旦声明不能改变
     *      limit 界限，缓冲区中可以操作数据的大小，limit后不能操作
     *      position 缓冲区中正在操作数据的位置
     *      
     *      
     *      
     *      position <= limit <= capacity
     */
    @Test
    public void test() {
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
    }


    /**
     * 
     */
    @Test
    public void test2() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("ABCDE".getBytes());
        byteBuffer.flip();
        
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes, 0, 2);
    }
}
