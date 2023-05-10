/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import at.vunfer.openrealms.UIUpdateListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientConnectorTest {

    private UIUpdateListener uiUpdater;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private ClientConnector clientConnector;

    @BeforeEach
    public void setUp() {
        clientConnector = new ClientConnector(uiUpdater);
    }

    @Test
    public void setConnectionTargetTest() throws IOException {
        clientConnector.setConnectionTarget("192.168.1.100", 3000);
        assertEquals(
                clientConnector.getConnectionTarget(),
                new InetSocketAddress("192.168.1.100", 3000));
    }
}
