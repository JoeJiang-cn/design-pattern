package com.joe.pattern.reactor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Joe
 * TODO description
 * 2021/8/30 20:18
 */
public class Server {
    public static void main(String[] args) throws IOException {
        int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Reactor(port));

        System.out.printf("server running on %s\n", port);
        Client.invoke();
    }
}
