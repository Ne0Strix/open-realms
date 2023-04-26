/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private int port;

    public ServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Log.i("Info", "The server was started! Port number: " + port);

            while (true) {

                Socket clientSocket = serverSocket.accept();
                ClientHandler player1 = new ClientHandler(clientSocket);
                //player1.start();


                /*
                Socket clientSocket2 = serverSocket.accept();
                ClientHandler player2 = new ClientHandler();

                //TODO: start the game just when there are 2 players

                player2.start();

                 */

            }

        }
        catch (IOException ex) {
            System.out.println("IO Exception on Server!");
            ex.printStackTrace();
        }
    }

}


