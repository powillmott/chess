package dataaccess;

import models.*;

import java.util.*;

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

    @Override
    public Collection<GameData> getGames() {
        return games.values();
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

    @Override
    public void joinGame(Integer gameID, String playerColor, String userName) {
        if (playerColor.equals("WHITE")) {
            getGame(gameID).setWhiteUsername(userName);
        } else {
            getGame(gameID).setBlackUsername(userName);
        }

    }

    @Override
    public void makeGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public String getUserName(String authToken) {
        return auth.get(authToken);
    }

}
