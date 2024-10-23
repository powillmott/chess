package service;
import chess.ChessGame;
import dataaccess.DataAccess;
import models.AuthData;
import models.GameData;
import models.UserData;

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
        return dataAccess.makeAuth(userName, new AuthData(UUID.randomUUID().toString(), userName));
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

    public GameData createGame(String authToken, String gameName) throws ServiceException {
        GameData newGame = null;
        if (authToken == null | gameName == null) {
            throw new ServiceException("bad request");
        } else if (!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        } else {
            newGame = new GameData(new Random().nextInt(10000),null,null,gameName,new ChessGame());
        }
        return newGame;
    }




}
