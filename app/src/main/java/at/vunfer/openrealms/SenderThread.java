package at.vunfer.openrealms;
import java.io.BufferedReader;
import java.io.IOException;
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
