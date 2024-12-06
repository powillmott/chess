package websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    public void notify(ServerMessage notification, String message);
}
