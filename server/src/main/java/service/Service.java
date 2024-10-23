package service;
import chess.ChessGame;
import dataaccess.DataAccess;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public boolean clearAll() {
        dataAccess.clearAllUsers();
        dataAccess.clearAllGames();
        dataAccess.clearAllAuth();
        return dataAccess.getAllUsers().isEmpty() & dataAccess.getAllGames().isEmpty() & dataAccess.getAllAuth().isEmpty();
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        AuthData newAuth;
        if (newUser.username() == null | newUser.password() == null | newUser.email() == null) {
            throw new ServiceException("bad request");
        } else if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("already taken");
        } else {
            dataAccess.makeUser(newUser.username(), newUser);
            newAuth = createAuth(newUser.username());
        }

        return newAuth;
    }

    private AuthData createAuth(String userName){
        return dataAccess.makeAuth(UUID.randomUUID().toString(),userName);
    }

    public AuthData loginUser(String username, String password) throws ServiceException {
        AuthData newAuth = null;
        if (dataAccess.getUser(username) == null) {
            throw new ServiceException("user not found");
        } else if (!Objects.equals(dataAccess.getUser(username).password(), password)) {
            throw new ServiceException("wrong password");
        }
        newAuth = createAuth(username);
        return newAuth;
    }

    public void logoutUser(String authToken) throws ServiceException {
        if(!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        }
//        String newUsername = dataAccess.getUser(authToken).username();
        dataAccess.removeUser(authToken);
    }

    public GameData createGame(String authToken, String gameName,Integer gameID) throws ServiceException {
        GameData newGame = null;
        if (authToken == null | gameName == null) {
            throw new ServiceException("bad request");
        } else if (!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        } else {
            if (gameID == null) {
                gameID = new Random().nextInt(10000);
            }
            newGame = new GameData(gameID,null,null,gameName,new ChessGame());
        }
        dataAccess.makeGame(newGame);
        return newGame;
    }

    public Collection<GameData> getAllGames(String authToken) throws ServiceException {
        Collection<GameData> allGames;
        if (!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        } else {
            allGames = dataAccess.getGames();
        }
        return allGames;
    }

    public void joinGame(String authToken, String playerColor, Integer gameID) throws ServiceException {
        if (!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        } else if (gameID == null | playerColor == null) {
            throw new ServiceException("bad request");
        }
        if (dataAccess.getGame(gameID) == null) {
            createGame(authToken, playerColor, gameID);
        }
        if ((playerColor.equals("BLACK") & dataAccess.getGame(gameID).blackUsername() != null) | (playerColor.equals("WHITE") & dataAccess.getGame(gameID).whiteUsername() != null)) {
            throw new ServiceException("already taken");
        } else {
            String userName = dataAccess.getUserName(authToken);
            dataAccess.joinGame(gameID, playerColor, userName);
        }
    }


}
