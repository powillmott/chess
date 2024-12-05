package server;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketSessions {
    private Map<Integer,Set<Session>> sessionMap = new HashMap<>();

    public void addSessionToGame(Integer gameID, Session session) {
        sessionMap.putIfAbsent(gameID,new HashSet<Session>());
        sessionMap.get(gameID).add(session);
    }
    public void removeSessionFromGame(Integer gameID, Session session) {
        sessionMap.get(gameID).remove(session);
    }
    public void removeSession(Session session) {
        session.close();
    }
    public Set<Session> getSessionForGame(Integer gameID) {
        return sessionMap.get(gameID);
    }
}
