package at.vunfer.openrealms.relay;

import com.sun.tools.javac.Main;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;

public class MainServer {
    public static List<Lobby> lobbies;

    private static ServerSocket serverSocket;
    private static final int PORT = 25567;

    public static void main(String[] args) {
        lobbies = new ArrayList<>();
        System.out.println("Listening on " + PORT);
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted someone");
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public Lobby lobby;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Thread started to run");

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Thread got Streams");
            while (true) {
                try {
                    String msg = (String) in.readObject();
                    System.out.println("GOT STRING: " + msg);

                    if (msg.equals("LIST")) {
                        String output = "";
                        for (Lobby l : MainServer.lobbies) {
                            output += l.getName() + " " + l.getNumPlayers() + ";";
                        }
                        out.writeObject(output);
                    } else if (msg.equals("STATUS")) {
                        if (lobby == null)
                            out.writeObject("");
                        else
                            out.writeObject(lobby.getName());
                    } else if (msg.startsWith("CREATE")) {
                        if (msg.split(" ").length < 2) {
                            sendError("Invalid name");
                            continue;
                        }
                        String lobbyName = msg.split(" ")[1];

                        boolean duplicateName = false;
                        for (Lobby lob : MainServer.lobbies) {
                            if (lob.getName().equals(lobbyName)) {
                                duplicateName = true;
                                break;
                            }
                        }
                        if (!duplicateName) {
                            Lobby newLobby = new Lobby(lobbyName);
                            MainServer.lobbies.add(newLobby);
                            joinLobby(lobbyName);
                        } else {
                            sendError("Name already in use");
                        }
                    } else if (msg.startsWith("JOIN")) {
                        String lobbyName = msg.split(" ")[1];
                        joinLobby(lobbyName);
                    } else if (msg.equals("LEAVE")) {
                        lobby.removeClient(this);
                        sendOk();
                    } else if (msg.equals("DISCONNECT")) {
                        if (lobby != null)
                            lobby.removeClient(this);

                        out.close();
                        in.close();
                        socket.close();
                        break;
                    }
                } catch (ClassNotFoundException | ClassCastException e) {
                    System.out.println("ClassNotFound or Class not Sting, message is not for me, relaying...");
                    //TODO: use simple InputStream to read the socket again and send it out the OutputStream
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinLobby(String lobbyName) {
        Lobby lobby = null;
        for (Lobby l : MainServer.lobbies) {
            if (l.getName().equals(lobbyName)) {
                lobby = l;
                break;
            }
        }

        if (lobby == null) {
            sendMessage("ERROR;No lobby found with this name...");
            return;
        }
        if (lobby.getNumPlayers() >= 2) {
            sendMessage("ERROR;Lobby is full");
            return;
        }
        lobby.addClient(this);
        sendOk();
    }

    public void sendMessage(byte[] message) {

    }

    public void sendError(String message) {
        System.out.println("Sending ERROR with message: " + message);
        sendMessage("ERROR;" + message);
    }

    public void sendOk() {
        System.out.println("Sending OK");
        sendMessage("OK;");
    }

    public void sendOk(String message) {
        System.out.println("Sending OK");
        sendMessage("OK;" + message);
    }

    public void sendMessage(String message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Lobby {
    private List<ClientHandler> clients;
    private String name;

    public Lobby(String name) {
        this.clients = new ArrayList<>();
        this.name = name;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        client.lobby = this;
        // broadcastLobbyStatus();
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        client.lobby = null;
        // broadcastLobbyStatus();

        if (getNumPlayers() == 0) {
            MainServer.lobbies.remove(this);
        }
    }

    public String getName() {
        return name;
    }

    public int getNumPlayers() {
        return clients.size();
    }

    public void broadcast(byte[] message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }

        }
    }

    public void broadcastLobbyStatus() {
        for (ClientHandler client : clients) {
            client.sendMessage("STATUS;" + name + ";" + getNumPlayers());
        }
    }
}