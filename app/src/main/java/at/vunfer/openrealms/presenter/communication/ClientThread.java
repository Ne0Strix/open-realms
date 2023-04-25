/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter.communication;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private int port;
    private String hostname;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter writer;
    private boolean gameOver = false;
    private char type;
    private String fromServer;

    public ClientThread(int port, String hostname) {
        this.port = port;
        this.hostname = hostname;
    }

    public String getFromServer() {
        return fromServer;
    }

    public void sendStringToServer() {
        SenderThread sender = new SenderThread(writer);
        sender.start();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Log.i(
                    "MyInfo",
                    "Client was started with the hostname " + hostname + " on the port " + port);

            // the first or the second player
            type = in.readLine().charAt(0);

            // receive the notification that you should enter your username and send it
            // textView.setText(in.readLine());

            // TODO: will be changed!
            String msg = in.readLine();
            writer.println("Julia");

            while (!gameOver) {
                fromServer = in.readLine();

                if (fromServer.equalsIgnoreCase("end")) {
                    gameOver = true;
                }
            }

            Log.i("Info", "Game is over!");
            // TODO: notificate the user that the game is over
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }
    }
}
