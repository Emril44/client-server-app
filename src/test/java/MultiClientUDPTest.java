import org.example.StoreClientUDP;
import org.example.StoreServerUDP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiClientUDPTest {
    private static final int CLIENT_NUM = 1;
    private StoreServerUDP server;
    private StoreClientUDP client;

    @BeforeEach
    public void setUp() throws Exception {
        new Thread(() -> {
            try {
                server = new StoreServerUDP(2078, "1234567812345678".getBytes());
                server.start();
            } catch (Exception e) {
                System.err.println("Error starting server!");
            }
        }).start();

        // server needs to wake up
        Thread.sleep(1000);

        client = new StoreClientUDP("localhost", 2078, "1234567812345678".getBytes());
    }

    @Test
    public void testMultiClientUDP() throws Exception {
        for(int i = 1; i <= CLIENT_NUM; i++) {
            client.sendRequest("hieieeieaienafefaedferga");

            Thread.sleep(1000);
        }
    }
}
