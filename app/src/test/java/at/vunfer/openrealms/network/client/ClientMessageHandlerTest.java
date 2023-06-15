/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ClientMessageHandlerTest {

    @Mock private UIUpdateListener mockUIUpdater;

    private ClientMessageHandler messageHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler = new ClientMessageHandler(mockUIUpdater);
    }

    @Test
    void testHandleMessage() {
        // Arrange
        Message message = mock(Message.class);

        // Act
        messageHandler.handleMessage(message);

        // Assert
        verify(mockUIUpdater).updateUI(message);
    }
}
