package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import models.GameData;
import ui.ChessClient;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint{
    Session session;
    NotificationHandler notificationHandler;
    private ChessClient client;

    public WebSocketFacade(ChessClient client, String url) throws Exception {
        this.client = client;
        try {
            url = url.replace("http","ws");
            URI socketURI = new URI(url + "/ws");
//            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this,socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case NOTIFICATION -> {
                            Notification not = new Gson().fromJson(message,Notification.class);
                            System.out.println(not.getMessage());
                        }
                        case LOAD_GAME -> loadGame(new Gson().fromJson(message,LoadGame.class));
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Error connecting to websocket: "+e.getMessage());
        }
    }

    private void loadGame(LoadGame loadGame) {
        GameData gameData = loadGame.getGameData();
        String boardPrintOut;
        if (this.client.getUserName().equals(gameData.blackUsername())) {
            boardPrintOut = this.client.printBoardBlack(gameData.game().getBoard());
        } else {
            boardPrintOut = this.client.printBoardWhite(gameData.game().getBoard());
        }
        System.out.println(boardPrintOut);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void send(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.println("Error sending message: "+e.getMessage());
        }
    }
}
