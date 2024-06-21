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
        lobbies.add(new Lobby("test1"));
        lobbies.add(new Lobby("test2"));
        lobbies.add(new Lobby("test3"));
        lobbies.add(new Lobby("test4"));
        lobbies.add(new Lobby("test5"));
        lobbies.add(new Lobby("test6"));
        lobbies.add(new Lobby("test7"));
        lobbies.add(new Lobby("test8"));
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
    private static int staticId;
    private int id;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public Lobby lobby;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.id = ++staticId;
    }

    @Override
    public void run() {
        System.out.println("Thread " + id + ": Thread started to run");

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Thread " + id + ": Thread got Streams");
            while (true) {
                try {
                    String msg = (String) in.readObject();
                    System.out.println("Thread " + id + ": GOT STRING: " + msg);

                    if (msg.equals("LIST")) {
                        String output = "";
                        for (Lobby l : MainServer.lobbies) {
                            output += l.getName() + " " + l.getNumPlayers();
                            if (l.isClientInLobby(this)) {
                                output += " IN";
                            }
                            output += ";";
                        }
                        sendMessage(output);
                    } else if (msg.startsWith("CREATE")) {
                        if (msg.split(" ").length < 2) {
                            sendError("Invalid name");
                            continue;
                        }
                        String lobbyName = msg.split(" ")[1];

                        if (lobbyName.length() > 20) {
                            sendError("Name is too long");
                            continue;
                        }
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
                            System.out.println("Thread " + id + ": created lobby " + lobbyName + ", now joining");
                            joinLobby(lobbyName);
                        } else {
                            sendError("Name already in use");
                        }
                    } else if (msg.startsWith("JOIN")) {
                        String lobbyName = msg.split(" ")[1];
                        joinLobby(lobbyName);
                    } else if (msg.equals("LEAVE")) {
                        if (lobby != null)
                            lobby.removeClient(this);
                        sendOk();
                    } else if (msg.equals("DISCONNECT")) { //Unused, currently we just let it crash, lol
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
        } catch (EOFException e) {
            System.out.println("Thread " + id + ":EOFException, assuming client disconnected, removing from lobby");
            if (lobby != null)
                lobby.removeClient(this);
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException ex) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinLobby(String lobbyName) {
        if (lobby != null) {
            sendError("You can only join one Lobby at a time...");
        }
        Lobby lobby = null;
        for (Lobby l : MainServer.lobbies) {
            if (l.getName().equals(lobbyName)) {
                lobby = l;
                break;
            }
        }

        if (lobby == null) {
            sendError("No lobby found with this name...");
            return;
        }
        if (lobby.getNumPlayers() >= 2) {
            sendError("Lobby is full");
            return;
        }
        lobby.addClient(this);
        sendOk();
    }

    public void sendError(String message) {
        System.out.println("Sending ERROR with message: " + message);
        sendObject("ERROR;" + message);
    }

    public void sendOk() {
        System.out.println("Thread " + id + ": Sending OK");
        sendObject("OK;");
    }

    public void sendMessage(Object msg) {
        System.out.println("Thread " + id + ": Sending: " + msg);
        sendObject(msg);
    }

    public void sendObject(Object message) {
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

    public boolean isClientInLobby(ClientHandler client) {
        return clients.contains(client);
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