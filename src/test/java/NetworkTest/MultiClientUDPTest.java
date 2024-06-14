package NetworkTest;

import org.example.network.udp.StoreClientUDP;
import org.example.network.udp.StoreServerUDP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiClientUDPTest {
    private static final int CLIENT_NUM = 20;
    private static final int PORT = 2078;
    private static final String KEY = "1234567812345678";
    private StoreServerUDP server;

    @BeforeEach
    public void setUp() throws Exception {
        new Thread(() -> {
            try {
                server = new StoreServerUDP(PORT, KEY.getBytes());
                server.start();
            } catch (Exception e) {
                System.err.println("Error starting server!");
            }
        }).start();

        // server needs to wake up
        Thread.sleep(1000);
    }

    @Test
    public void testMultiClientUDP() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_NUM);

        for (int i = 1; i <= CLIENT_NUM; i++) {
            final int clientId = i;
            executorService.submit(() -> {
                try {
                    StoreClientUDP client = new StoreClientUDP("localhost", KEY.getBytes());
                    client.sendRequest("Hello from client " + clientId);
                } catch (Exception e) {
                    System.err.println("Error running multi-client UDP!");
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}
