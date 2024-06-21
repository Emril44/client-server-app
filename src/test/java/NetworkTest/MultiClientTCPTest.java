package NetworkTest;

import org.example.network.tcp.StoreClientTCP;
import org.example.network.tcp.StoreServerTCP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiClientTCPTest {
    private static final int CLIENT_NUM = 1;

    @BeforeEach
    public void setup() throws Exception {
        // Start TCP server in separate thread
        new Thread(() -> {
            try {
                StoreServerTCP.main(null);
            } catch (Exception e) {
                System.err.println("Error starting TCP server in JUnit test!");
            }
        }).start();

        // server needs a moment to move its arse
        Thread.sleep(2000);
    }

    @Test
    public void testMultiClientTCP() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_NUM);

        // Start the clients
        for (int i = 1; i <= CLIENT_NUM; i++) {
            final int clientId = i;
            executorService.submit(() -> {
                try {
                    StoreClientTCP.main(null);
                } catch (Exception e) {
                    System.err.println("Error starting TCP client " + clientId + " in JUnit test!");
                    e.printStackTrace();
                }
            });
        }

        // Close executor service and wait for everything to run
        executorService.shutdown();
        boolean tasksCompleted = executorService.awaitTermination(60, TimeUnit.SECONDS);

        assertTrue(tasksCompleted, "All tasks done within the timeout period");
    }
}
