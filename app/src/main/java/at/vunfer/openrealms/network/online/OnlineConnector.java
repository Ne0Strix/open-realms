/* Licensed under GNU GPL v3.0 (C) 2024 */
package at.vunfer.openrealms.network.online;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.R;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class OnlineConnector extends Thread {

    private final String address;
    private final int port;
    private final MainActivity mainActivity;
    private Socket relaySocket;
    private ObjectInputStream relayInput;
    private ObjectOutputStream relayOutput;
    private List<OnlineResponseCallback> okCallbacks;
    private List<OnlineResponseCallback> errorCallbacks;

    public interface OnlineResponseCallback {
        void callback();
    }

    private static final String TAG = "OnlineConnector";

    public OnlineConnector(MainActivity mainActivity, String address, int port) {
        this.address = address;
        this.port = port;
        this.mainActivity = mainActivity;
        this.okCallbacks = new ArrayList<>();
        this.errorCallbacks = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            relaySocket = new Socket(address, port);
            relayOutput = new ObjectOutputStream(relaySocket.getOutputStream());
            relayInput = new ObjectInputStream(relaySocket.getInputStream());
            mainActivity.runOnUiThread(mainActivity::actuallySwitchToOnlineMode);

            try {
                while (true) {
                    // READ
                    String response = (String) relayInput.readObject();
                    String[] responseSplit = response.split(";");
                    switch (responseSplit[0]) {
                        case "OK":
                            if (okCallbacks.size() != 0) {
                                okCallbacks.get(0).callback();
                                okCallbacks.remove(0);
                            } else {
                                receivedOk();
                            }
                            break;
                        case "ERROR":
                            if (errorCallbacks.size() != 0) {
                                errorCallbacks.get(0).callback();
                                errorCallbacks.remove(0);
                            } else {
                                receivedError(responseSplit[1]);
                            }
                            break;
                        case "LIST":
                            if (responseSplit.length > 1) refreshLobbyView(responseSplit[1]);
                            else refreshLobbyView("");
                            break;
                        default:
                            Log.e(TAG, "Received message without code: " + response);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        } catch (IOException e) {
            mainActivity.runOnUiThread(
                    () ->
                            Toast.makeText(mainActivity, "Connection Failed", Toast.LENGTH_SHORT)
                                    .show());
            MainActivity.onlineConnector = null;
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void requestLobbyRefresh() {
        sendString("LIST");
    }

    public void refreshLobbyView(String response) {
        if (response.isEmpty()) {
            mainActivity.runOnUiThread(
                    () -> {
                        // Log.d(TAG, "Enabling no lobbies txt");
                        TextView responseView = mainActivity.findViewById(R.id.txtNoLobbies);
                        responseView.setVisibility(View.VISIBLE);

                        RecyclerView lobbyList = mainActivity.findViewById(R.id.recyclerLobbyList);
                        lobbyList.setVisibility(View.INVISIBLE);
                    });
        } else {
            List<Lobby> lobbies = new ArrayList<>();
            for (String lob : response.split(",")) {
                String[] nameAndCount = lob.split(" ");
                boolean isInLobby = nameAndCount.length > 2;
                lobbies.add(
                        new Lobby(nameAndCount[0], Integer.parseInt(nameAndCount[1]), isInLobby));
            }
            mainActivity.runOnUiThread(
                    () -> {
                        // Log.d(TAG, "Enabling lobbies");
                        TextView responseView = mainActivity.findViewById(R.id.txtNoLobbies);
                        responseView.setVisibility(View.INVISIBLE);

                        RecyclerView lobbyList = mainActivity.findViewById(R.id.recyclerLobbyList);
                        lobbyList.setVisibility(View.VISIBLE);

                        ((LobbyAdapter) lobbyList.getAdapter())
                                .updateLobbies(lobbies, mainActivity);
                    });
        }
    }

    public void startGame() {}

    public void createLobby(String lobbyName) {
        Log.v(TAG, "Sending lobby create request");
        sendString("CREATE;" + lobbyName);
        Log.v(TAG, "Waiting for response");
    }

    public void joinLobby(Lobby lobby) {
        String msg = "JOIN;" + lobby.getName();
        sendString(msg);
    }

    public void leaveLobby() {
        sendString("LEAVE");
    }

    public void receivedError(String errorText) {
        mainActivity.runOnUiThread(
                () -> Toast.makeText(mainActivity, errorText, Toast.LENGTH_SHORT).show());
    }

    public void receivedOk() {
        Log.d(TAG, "Received \"OK\"");
    }

    public void sendString(String msg) {
        new Thread(
                        () -> {
                            try {
                                relayOutput.writeObject(msg);
                            } catch (IOException e) {
                                Log.e(TAG, e.getLocalizedMessage());
                            }
                        })
                .start();
    }

    public void close() {
        try {
            relayInput.close();
            relayOutput.close();
            relaySocket.close();
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
