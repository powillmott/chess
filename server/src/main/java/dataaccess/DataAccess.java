package dataaccess;
import chess.ChessGame;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.Collection;
import java.util.Map;

public interface DataAccess {
    UserData getUser(String userName) throws DataAccessException;
    UserData makeUser(String userName, UserData userData) throws DataAccessException ;
    AuthData makeAuth(String authToken, String userName) throws DataAccessException;
    void clearAllUsers() throws DataAccessException;
    void clearAllAuth() throws DataAccessException;
    void clearAllGames() throws DataAccessException;
    Map<Integer, GameData> getAllGames() throws DataAccessException;
    Map<String, UserData> getAllUsers() throws DataAccessException;
    Map<String, String> getAllAuth() throws DataAccessException;
    boolean validAuth(String authToken) throws DataAccessException;
    void removeUser(String authToken) throws DataAccessException;
    Collection<GameData> getGames() throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    void joinGame(Integer gameID, String playerColor, String userName) throws DataAccessException;
    void makeGame(GameData game) throws DataAccessException;
    String getUserName(String authToken) throws DataAccessException;
    void updateGame(ChessGame chess, Integer gameID) throws DataAccessException;
}
