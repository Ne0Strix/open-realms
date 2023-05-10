/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CommunicationTest {

    ObjectInputStream input;

    ObjectOutputStream output;

    IHandleMessage messageHandler;

    Communication communication;

    @BeforeEach
    void setUp() {
        input = Mockito.mock(ObjectInputStream.class);
        output = Mockito.mock(ObjectOutputStream.class);
        messageHandler = Mockito.mock(IHandleMessage.class);
        communication = new Communication(input, output, messageHandler);
    }

    @Test
    void sendMessageTest() throws IOException {
        Message msg = new Message(MessageType.TOUCHED);
        communication.sendMessage(msg);
        verify(output).writeObject(msg);
    }

    // TODO listen for message test; cannot verify(messageHandler).handleMessage(msg) easily because
    // of the executor service
}
