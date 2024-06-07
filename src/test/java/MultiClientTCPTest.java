import org.example.StoreClientTCP;
import org.example.StoreServerTCP;
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
        Thread.sleep(1000);
    }

    @Test
    public void testMultiClientTCP() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_NUM);

        // Start the clients
        for(int i = 0; i < CLIENT_NUM; i++) {
            executorService.submit(() -> {
                try {
                    StoreClientTCP.main(null);
                } catch (Exception e) {
                    System.err.println("Error starting TCP clients in JUnit test!");
                }
            });
        }

        // Close executor service and wait for everything to run
        executorService.shutdown();
        boolean tasksCompleted = executorService.awaitTermination(30, TimeUnit.SECONDS);

        assertTrue(tasksCompleted, "All tasls done within the timeout period");
    }
}
