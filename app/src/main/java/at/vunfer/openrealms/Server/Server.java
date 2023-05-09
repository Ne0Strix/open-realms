/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
        startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("The server was started! Port number: " + port);

            while (true) {

                Socket clientSocket1 = serverSocket.accept();
                ClientHandler player1 = new ClientHandler(this, clientSocket1, '1');
                player1.start();

                Socket clientSocket2 = serverSocket.accept();
                ClientHandler player2 = new ClientHandler(this, clientSocket2, '2');
                player1.setPartner(player2);
                player2.setPartner(player1);

                // TODO: start the game just when there are 2 players

                player2.start();
            }

        } catch (IOException ex) {
            System.out.println("IO Exception on Server!");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // the server needs a port number
        if (args.length != 1) {
            throw new IllegalArgumentException("Please provide 1 argument: the number of the port");
        }

        int port = Integer.parseInt(args[0]);

        new Server(port);
    }
}
