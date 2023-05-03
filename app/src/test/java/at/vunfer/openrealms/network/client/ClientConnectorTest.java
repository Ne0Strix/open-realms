package at.vunfer.openrealms.network.client;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        assertEquals(clientConnector.getConnectionTarget(), new InetSocketAddress("192.168.1.100", 3000));
    }
}
