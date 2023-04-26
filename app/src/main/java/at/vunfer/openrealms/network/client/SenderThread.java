/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class SenderThread extends Thread {
    private ObjectOutputStream outputStream;

    public SenderThread(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        Random rand = new Random();
        try {
            //the message will be sent here
            outputStream.writeObject("The card " + rand.nextInt(145) + " was played.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
