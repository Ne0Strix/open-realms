/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import at.vunfer.openrealms.UIUpdateListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientConnectorTest {

    private UIUpdateListener uiUpdater;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private ClientConnector clientConnector;

    @BeforeEach
    public void setUp() {
        clientConnector = new ClientConnector(uiUpdater);
    }

    // @Test TODO: setConnectionTarget was changed to save the ipAdress and port
    // no longer immediately instantiating the InetSocketAddress Object
    public void setConnectionTargetTest() throws IOException {
        clientConnector.setConnectionTarget("192.168.1.100", 3000);
        assertEquals(
                clientConnector.getConnectionTarget(),
                new InetSocketAddress("192.168.1.100", 3000));
    }

    // @Test
    // TODO: The Constructor of ObjectInputStream is blocking for some reason
    public void testRun() throws IOException {
        System.out.println("A");
        Socket mockedSocked = mock(Socket.class);
        System.out.println("A");
        clientConnector.setSocket(mockedSocked);
        System.out.println("A");
        doNothing().when(mockedSocked).connect(any());
        System.out.println("A");
        doReturn(mock(OutputStream.class)).when(mockedSocked).getOutputStream();
        System.out.println("A");
        doReturn(mock(InputStream.class)).when(mockedSocked).getInputStream();
        System.out.println("A");
        clientConnector.run();
        System.out.println("A");
        verify(mockedSocked.getOutputStream());
        verify(mockedSocked.getInputStream());
    }

    @Test
    void testRunError() throws IOException {
        Socket mockedSocked = mock(Socket.class);
        clientConnector.setSocket(mockedSocked);
        doThrow(new RuntimeException("Unable to create client connection."))
                .when(mockedSocked)
                .getOutputStream();

        assertThrows(RuntimeException.class, () -> clientConnector.run());
    }
}
