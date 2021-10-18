package com.joe.pattern.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Joe
 * TODO description
 * 2021/8/30 19:33
 */
public class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocket;

    public Reactor(int port) throws IOException {
        // 初始化selector和channel
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        // 绑定selector会监听accept事件
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        // 绑定Acceptor即事件处理器
        sk.attach(new Acceptor());
    }

    @Override
    public void run() {
        // normally in a new Thread
        try {
            while (!Thread.interrupted()) {
                // 当accept事件被触发，select返回
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    // 分发事件
                    dispatch(it.next());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        // attachment获取绑定的事件
        Runnable r = (Runnable) k.attachment();
        if (r != null) {
            // 执行事件方法
            r.run();
        }
    }

    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                // accept_socket，传递给Handler
                SocketChannel c = serverSocket.accept();
                if (c != null) {
                    new Handler(selector, c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
