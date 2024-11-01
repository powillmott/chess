package dataaccess;

import models.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DAOTest {
    static DataAccess testDataAccess;
    static UserData userData;

    @BeforeAll
    public static void init() {
        testDataAccess = new MySqlDataAccess();
        userData = new UserData("testUser","password","test@mail");
    }

    @BeforeEach
    public static void setUp() throws DataAccessException {

    }

    @Test
    public void makeUserGood() throws DataAccessException {
        testDataAccess.makeUser(userData.username(),userData);

    }
}
