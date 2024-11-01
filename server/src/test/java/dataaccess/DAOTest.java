package dataaccess;

import models.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DAOTest {
    static DataAccess testDataAccess;
    static UserData userData;
    static UserData existingUserData;

    @BeforeAll
    public static void init() {
        testDataAccess = new MySqlDataAccess();
        userData = new UserData("testUser","password","test@mail");
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
    public void makeUserGood() throws DataAccessException {
        testDataAccess.makeUser(userData.username(),userData);

    }
}
