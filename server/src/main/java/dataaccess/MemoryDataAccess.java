package dataaccess;

import models.*;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    final private Map<String, UserData> users = new HashMap<>();
    final private Map<Integer, GameData> games = new HashMap<>();
    final private Map<String, String> auth = new HashMap<>();

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
    public String getAuth(String userName) {
        return null;
    }

    @Override
    public AuthData makeAuth(String authToken, String userName) {
        auth.put(authToken, userName);
        return new AuthData(authToken,userName);
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
    public Map<String, String> getAllAuth() {
        return auth;
    }

    @Override
    public boolean validAuth(String authToken) {
        return auth.containsKey(authToken);
    }

    @Override
    public void removeUser(String authToken) {
        auth.remove(authToken);
    }

}
