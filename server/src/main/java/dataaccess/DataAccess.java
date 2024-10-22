package dataaccess;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.Map;

public interface DataAccess {
    UserData getUser(String userName);
    UserData makeUser(String userName, UserData userData);
    AuthData getAuth(String userName);
    AuthData makeAuth(String userName, AuthData authData);
    void clearAllUsers();
    void clearAllAuth();
    void clearAllGames();
    Map<Integer, GameData> getAllGames();
    Map<String, UserData> getAllUsers();
    Map<String, AuthData> getAllAuth();
}
