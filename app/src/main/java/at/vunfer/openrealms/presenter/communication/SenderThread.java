/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter.communication;

import java.io.PrintWriter;
import java.util.Random;

public class SenderThread extends Thread {
    private PrintWriter writer;

    public SenderThread(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        Random rand = new Random();
        writer.println("The card " + rand.nextInt(145) + " was played.");
    }
}
