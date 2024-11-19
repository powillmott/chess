package dataaccess;

import chess.ChessGame;
import models.GameData;
import models.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAOTest {
    static DataAccess testDataAccess;
    static UserData userData;
    static UserData existingUserData;
    static GameData gameData;

    @BeforeAll
    public static void init() {
        testDataAccess = new MySqlDataAccess();
        userData = new UserData("testUser","password","test@mail");
        gameData = new GameData(1234,null,null,"game1",new ChessGame());
        existingUserData = new UserData("testUser1","password1","test1@mail");
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        testDataAccess.clearAllAuth();
        testDataAccess.clearAllUsers();
        testDataAccess.clearAllGames();
        testDataAccess.makeUser(existingUserData.username(),existingUserData);
    }

    @Test
    public void getUserGood() throws DataAccessException {
        UserData result = testDataAccess.getUser(existingUserData.username());
        Assertions.assertEquals(existingUserData.username(),result.username());
    }

    @Test
    public void getUserBad() throws DataAccessException {
        Assertions.assertNull(testDataAccess.getUser("badUsername"));
    }

    @Test
    public void makeUserGood() throws DataAccessException {
        testDataAccess.makeUser(userData.username(),userData);
        Assertions.assertEquals(userData.email(),testDataAccess.getUser(userData.username()).email());
    }

    @Test
    public void makeUserBad() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {testDataAccess.makeUser(null,userData);});
    }

    @Test
    public void makeAuthGood() throws DataAccessException {
        testDataAccess.makeAuth("chiasm12",userData.username());
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT userName FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, "chiasm12");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Assertions.assertEquals(rs.getString("userName"),userData.username());
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Test
    public void makeAuthBad() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {testDataAccess.makeAuth(null,null);});
    }

    @Test
    public void clearAllUsersGood() throws DataAccessException {
        testDataAccess.clearAllUsers();
        Assertions.assertEquals(new HashMap<>(),testDataAccess.getAllUsers());
    }

    @Test
    public void clearAllAuthGood() throws DataAccessException {
        testDataAccess.clearAllAuth();
        Assertions.assertEquals(new HashMap<>(),testDataAccess.getAllAuth());
    }

    @Test
    public void clearAllGamesGood() throws DataAccessException {
        testDataAccess.clearAllGames();
        Assertions.assertEquals(new HashMap<>(),testDataAccess.getAllGames());
    }

    @Test
    public void getAllGamesGood() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        HashMap<Integer, GameData> result = new HashMap<Integer, GameData>();
        result.put(gameData.gameID(), gameData);
        testDataAccess.getAllGames();
        Assertions.assertEquals(result,testDataAccess.getAllGames());
    }

    @Test
    public void getAllUsersGood() throws DataAccessException {
        testDataAccess.makeUser(userData.username(),userData);
        Map<String, UserData> result = testDataAccess.getAllUsers();
        Map<String, UserData> expected = new HashMap<String, UserData>();
        expected.put("testUser",userData);
        expected.put("testUser1",existingUserData);
        Assertions.assertEquals(expected.size(),result.size());
    }

    @Test
    public void getAllAuthGood() throws DataAccessException {
        testDataAccess.makeAuth("chiasm12",userData.username());
        Map<String, String> result = new HashMap<String, String>();
        result.put("chiasm12",userData.username());
        Assertions.assertEquals(result,testDataAccess.getAllAuth());
    }

    @Test
    public void validAuthGood() throws DataAccessException {
        testDataAccess.makeAuth("chiasm12",userData.username());
        Assertions.assertTrue(testDataAccess.validAuth("chiasm12"));
    }

    @Test
    public void validAuthBad() throws DataAccessException {
        testDataAccess.makeAuth("chiasm12",userData.username());
        Assertions.assertFalse(testDataAccess.validAuth(null));
    }

    @Test
    public void removeUserGood() throws DataAccessException {
        testDataAccess.makeAuth("chiasm12",userData.username());
        testDataAccess.removeUser("chiasm12");
        Assertions.assertEquals(new HashMap<>(),testDataAccess.getAllAuth());
    }

    @Test
    public void getGamesGood() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        var result = new ArrayList<GameData>();
        result.add(gameData);
        Assertions.assertEquals(result,testDataAccess.getGames());
    }

    @Test
    public void getGameGood() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        Assertions.assertEquals(gameData,testDataAccess.getGame(gameData.gameID()));
    }

    @Test
    public void getGameBad() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {testDataAccess.getGame(null);});
    }

    @Test
    public void joinGameGood() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        testDataAccess.joinGame(gameData.gameID(),"WHITE", userData.username());
        Assertions.assertEquals(userData.username(),testDataAccess.getGame(gameData.gameID()).whiteUsername());
    }

    @Test
    public void joinGameBad() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        Assertions.assertThrows(Exception.class, () -> {testDataAccess.joinGame(null,null, null);});
    }

    @Test
    public void makeGameGood() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        Assertions.assertEquals(gameData,testDataAccess.getGame(gameData.gameID()));
    }

    @Test
    public void makeGameBad() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {testDataAccess.makeGame(null);});
    }

    @Test
    public void getUserNameGood() throws DataAccessException {
        testDataAccess.makeAuth("chiasm12",userData.username());
        Assertions.assertEquals(userData.username(),testDataAccess.getUserName("chiasm12"));
    }

}
