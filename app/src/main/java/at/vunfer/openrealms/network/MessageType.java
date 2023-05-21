/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

public enum MessageType {
    // Client -> Server
    TOUCHED,
    CHOICE,
    END_TURN,
    BUY_CARD_CONFIRMATION,
    BUY_CARD_ERROR,

    // Server -> Client
    ADD_CARD,
    REMOVE_CARD,
    BUY_CARD,
    CHOOSE_OPTION,
    UPDATE_PLAYER_STATS, // used to update the player stats of a player
    SENSOR_ACTIVATION, // New message type for sensor activation
    FULL_CARD_DECK, // used to send the full card deck to the client
    TURN_NOTIFICATION // used to notify the client that it is his turn
}
