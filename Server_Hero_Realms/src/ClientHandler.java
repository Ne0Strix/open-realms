import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Server server;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private char type;          // player 1 or player 2
    private boolean gameOver = false;
    private ClientHandler partner;

    public ClientHandler(Server server, Socket clientSocket, char type) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        this.type = type;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Client connected: " + clientSocket);

    }

    public void setPartner(ClientHandler partner) {
        this.partner = partner;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        // send type
        out.println(type);

        // ask for the name
        send("Please enter your name: ");
        try {
            username = in.readLine();
            System.out.println(username);
        }
        catch (IOException ex) {
            System.out.println("IOException while entering a username");
        }


        // ! Will be changed! It is just a simulation of the game!
        String message;
        int count = 5;
        try {
            while (!gameOver) {
                message = in.readLine();
                System.out.println(message);
                send("Answer from server " + count);
                count--;
                if (count == 0) {
                    gameOver = true;
                    send("end");
                }
            }
        }
        catch (IOException ex) {
            System.out.println("IO Exception!");
        }


        try {
            in.close();
            out.close();
            clientSocket.close();
        }
        catch (IOException ex) {
            System.out.println("IO Exception while closing...");
        }

    }

    public void send(String message) {
        out.println(message);
    }

    public void sendToPartner(String message) {
        partner.send(message);
    }

}
