package service;
import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public boolean clearAll() throws DataAccessException{
        dataAccess.clearAllUsers();
        dataAccess.clearAllGames();
        dataAccess.clearAllAuth();
        return dataAccess.getAllUsers().isEmpty() & dataAccess.getAllGames().isEmpty() & dataAccess.getAllAuth().isEmpty();
    }

    public AuthData registerUser(UserData newUser) throws ServiceException, DataAccessException {
        AuthData newAuth;
        if (newUser.username() == null | newUser.password() == null | newUser.email() == null) {
            throw new ServiceException("bad request");
        } else if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("already taken");
        } else {
            UserData brandNewUser = new UserData(newUser.username(), BCrypt.hashpw(newUser.password(), BCrypt.gensalt()), newUser.email());
            dataAccess.makeUser(brandNewUser.username(), brandNewUser);
            newAuth = createAuth(newUser.username());
        }

        return newAuth;
    }

    private AuthData createAuth(String userName) throws DataAccessException {
        return dataAccess.makeAuth(UUID.randomUUID().toString(),userName);
    }

    public AuthData loginUser(String username, String password) throws ServiceException, DataAccessException {
        AuthData newAuth = null;
        if (dataAccess.getUser(username) == null) {
            throw new ServiceException("user not found");
        } else if (!BCrypt.checkpw(password, dataAccess.getUser(username).password())) {
            throw new ServiceException("wrong password");
        }
        newAuth = createAuth(username);
        return newAuth;
    }

    public void logoutUser(String authToken) throws ServiceException, DataAccessException {
        if(!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        }
        dataAccess.removeUser(authToken);
    }

    public GameData createGame(String authToken, String gameName,Integer gameID) throws ServiceException, DataAccessException {
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

    public Collection<GameData> getAllGames(String authToken) throws ServiceException, DataAccessException {
        Collection<GameData> allGames;
        if (!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        } else {
            allGames = dataAccess.getGames();
        }
        return allGames;
    }

    public void joinGame(String authToken, String playerColor, Integer gameID) throws ServiceException, DataAccessException {
        if (!dataAccess.validAuth(authToken)) {
            throw new ServiceException("unauthorized");
        } else if (gameID == null | playerColor == null) {
            throw new ServiceException("bad request");
        }
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new ServiceException("no valid game to join");
        }
        boolean blackUserTaken = gameData.blackUsername() != null;
        boolean whiteUserTaken = gameData.whiteUsername() != null;
        if ((playerColor.equals("BLACK") && blackUserTaken) || (playerColor.equals("WHITE") && whiteUserTaken)) {
            throw new ServiceException("already taken");
        } else {
            String userName = dataAccess.getUserName(authToken);
            dataAccess.joinGame(gameID, playerColor, userName);
        }
    }


}
