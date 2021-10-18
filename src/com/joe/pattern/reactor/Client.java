package com.joe.pattern.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Joe
 * TODO description
 * 2021/8/30 20:17
 */
public class Client {
    public static void invoke() {
        String host = "127.0.0.1";
        int port = 8080;
        SocketAddress socketAddress = new InetSocketAddress(host, port);

        Runnable runnable = () -> {
            try {
                SocketChannel socketChannel = SocketChannel.open(socketAddress);
                socketChannel.configureBlocking(true);
                // write
                String msg = String.format(Handler.SEND, Thread.currentThread().getName());
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                socketChannel.write(buffer);
                // read
                buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer);
                if (buffer.position() > 0) {
                    buffer.flip();
                    msg = Charset.defaultCharset().newDecoder().decode(buffer).toString();
                    System.out.printf(Handler.RECEIVE + "\n", Thread.currentThread().getName(), msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < 8; i++) {
            new Thread(runnable).start();
        }
    }
}
