package dataaccess;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.Collection;
import java.util.Map;

public interface DataAccess {
    UserData getUser(String userName);
    UserData makeUser(String userName, UserData userData);
    String getAuth(String userName);
    AuthData makeAuth(String authToken, String userName);
    void clearAllUsers();
    void clearAllAuth();
    void clearAllGames();
    Map<Integer, GameData> getAllGames();
    Map<String, UserData> getAllUsers();
    Map<String, String> getAllAuth();
    boolean validAuth(String authToken);
    void removeUser(String authToken);
    Collection<GameData> getGames();
}
