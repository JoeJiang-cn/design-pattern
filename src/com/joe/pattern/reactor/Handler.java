package com.joe.pattern.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Joe
 * TODO description
 * 2021/8/30 19:41
 */
public final class Handler implements Runnable {
    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final String SEND = "i am %s";
    static final String RECEIVE = "%s receive '%s'";
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector sel, SocketChannel c) throws IOException {
        // 这个socket是accept返回的socket
        socket = c;
        socket.configureBlocking(false);

        sk = socket.register(sel, 0);
        sk.attach(this);
        // 设置sk对read事件感兴趣
        sk.interestOps(SelectionKey.OP_READ);
        // 当内核事件触发时(read/write)立刻执行attach的线程
        sel.wakeup();
    }

    boolean inputIsComplete() {
        return input.position() > 2;
    }

    boolean outputIsComplete() {
        return !output.hasRemaining();
    }

    void process() {
        try {
            input.flip();
            String msg = Charset.defaultCharset().newDecoder().decode(input).toString();
            System.out.printf(RECEIVE + "\n", Thread.currentThread().getName(), msg);

            // consuming
            Thread.sleep(1000);

            msg = String.format(SEND, Thread.currentThread().getName());
            output.put(ByteBuffer.wrap(msg.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read() throws IOException {
        // 数据从socket读取到input缓冲区
        socket.read(input);
        if (inputIsComplete()) {
            // 处理
            process();
            // 修改状态
            state = SENDING;
            // 设置sk对write事件感兴趣
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        output.flip();
        socket.write(output);
        if (outputIsComplete()) {
            sk.cancel();
        }
    }
}
