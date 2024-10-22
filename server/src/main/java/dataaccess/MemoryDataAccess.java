package dataaccess;

import models.*;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    final private Map<String, UserData> users = new HashMap<>();
    final private Map<Integer, GameData> games = new HashMap<>();
    final private Map<String, AuthData> auth = new HashMap<>();

    @Override
    public UserData getUser(String userName) {
        return users.get(userName);
    }

    @Override
    public UserData makeUser(String userName, UserData userData) {
        users.put(userName, userData);
        return userData;
    }

    @Override
    public AuthData getAuth(String userName) {
        return null;
    }

    @Override
    public AuthData makeAuth(String userName, AuthData authData) {
        auth.put(userName, authData);
        return authData;
    }

    @Override
    public void clearAllUsers() {
        users.clear();
    }

    @Override
    public void clearAllAuth() {
        auth.clear();
    }

    @Override
    public void clearAllGames() {
        games.clear();
    }

    @Override
    public Map<Integer, GameData> getAllGames() {
        return games;
    }

    @Override
    public Map<String, UserData> getAllUsers() {
        return users;
    }

    @Override
    public Map<String, AuthData> getAllAuth() {
        return auth;
    }

}
