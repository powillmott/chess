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
import java.sql.SQLException;
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
        Assertions.assertEquals(existingUserData,result);
    }

    @Test
    public void getUserBad() throws DataAccessException {
        Assertions.assertNull(testDataAccess.getUser("badUsername"));
    }

    @Test
    public void makeUserGood() throws DataAccessException {
        testDataAccess.makeUser(userData.username(),userData);
        Assertions.assertEquals(userData,testDataAccess.getUser(userData.username()));
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

    }

    @Test
    public void getAllGamesBad() throws DataAccessException {

    }

    @Test
    public void getAllUsersGood() throws DataAccessException {
        testDataAccess.makeUser(userData.username(),userData);
        Map<String, UserData> result = testDataAccess.getAllUsers();
        Map<String, UserData> expected = new HashMap<String, UserData>();
        expected.put("testUser",userData);
        expected.put("testUser1",existingUserData);
        Assertions.assertEquals(expected,result);
    }

    @Test
    public void getAllUsersBad() throws DataAccessException {

    }

    @Test
    public void getAllAuthGood() throws DataAccessException {

    }

    @Test
    public void getAllAuthBad() throws DataAccessException {

    }

    @Test
    public void validAuthGood() throws DataAccessException {

    }

    @Test
    public void validAuthBad() throws DataAccessException {

    }

    @Test
    public void removeUserGood() throws DataAccessException {

    }

    @Test
    public void removeUserBad() throws DataAccessException {

    }

    @Test
    public void getGamesGood() throws DataAccessException {

    }

    @Test
    public void getGamesBad() throws DataAccessException {

    }

    @Test
    public void getGameGood() throws DataAccessException {

    }

    @Test
    public void getGameBad() throws DataAccessException {

    }

    @Test
    public void joinGameGood() throws DataAccessException {

    }

    @Test
    public void joinGameBad() throws DataAccessException {

    }

    @Test
    public void makeGameGood() throws DataAccessException {
        testDataAccess.makeGame(gameData);
        Assertions.assertEquals(gameData,testDataAccess.getGame(gameData.gameID()));
    }

    @Test
    public void makeGameBad() throws DataAccessException {

    }

    @Test
    public void getUserNameGood() throws DataAccessException {

    }

    @Test
    public void getUserNameBad() throws DataAccessException {

    }

}
