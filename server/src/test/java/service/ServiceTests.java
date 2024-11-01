package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    private static DataAccess dataAccess;
    private static Service serv;
    private static UserData user1;
    AuthData authTest;
    @BeforeAll
    public static void init() {
        dataAccess = new MemoryDataAccess();
        serv = new Service(dataAccess);
        user1 = new UserData("testUser","testPassword","test@cs");

    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        serv.clearAll();
        authTest = serv.registerUser(user1);
    }

    @Test
    public void clearAllGood() throws DataAccessException {

        serv.createGame(authTest.authToken(),"testGame",null);
        Assertions.assertTrue(serv.clearAll());
    }

    @Test
    public void registerUserGood() throws DataAccessException {
        serv.clearAll();
        authTest = serv.registerUser(user1);
        Assertions.assertTrue(dataAccess.validAuth(authTest.authToken()));
    }

    @Test
    public void registerUserBad() {
        Assertions.assertThrows(ServiceException.class, () -> {serv.registerUser(new UserData(null,null,null));});
    }

    @Test
    public void loginUserGood() throws DataAccessException {
        AuthData authTest = serv.loginUser(user1.username(), user1.password());
        Assertions.assertTrue(dataAccess.validAuth(authTest.authToken()));
    }

    @Test
    public void loginUserBad() {
        Assertions.assertThrows(ServiceException.class, () -> {serv.loginUser(null, null);});
    }

    @Test
    public void logoutUserGood() throws DataAccessException {
        AuthData authTest = serv.loginUser(user1.username(), user1.password());
        serv.logoutUser(authTest.authToken());
        Assertions.assertFalse(dataAccess.validAuth(authTest.authToken()));
    }

    @Test
    public void logoutUserBad() throws DataAccessException {
        AuthData authTest = serv.loginUser(user1.username(), user1.password());
        Assertions.assertThrows(ServiceException.class, () -> {serv.logoutUser("notRealAuthToken");});
    }

    @Test
    public void createGameGood() throws DataAccessException {
        GameData gameTest = serv.createGame(authTest.authToken(),"testGame",null);
        Assertions.assertEquals(dataAccess.getGame(gameTest.gameID()),gameTest);
    }

    @Test
    public void createGameBad() {
        Assertions.assertThrows(ServiceException.class,() -> {serv.createGame(null,"testGame",null);});
    }

    @Test
    public void getAllGamesGood() throws DataAccessException {
        serv.createGame(authTest.authToken(),"testGame1",null);
        serv.createGame(authTest.authToken(),"testGame2",null);
        serv.createGame(authTest.authToken(),"testGame3",null);
        Assertions.assertEquals(serv.getAllGames(authTest.authToken()),dataAccess.getGames()) ;
    }

    @Test
    public void getAllGamesBad() throws DataAccessException {
        serv.createGame(authTest.authToken(),"testGame1",null);
        serv.createGame(authTest.authToken(),"testGame2",null);
        serv.createGame(authTest.authToken(),"testGame3",null);
        Assertions.assertThrows(ServiceException.class, () -> {serv.getAllGames("notRealAuthToken");});
    }

    @Test
    public void joinGameGood() throws DataAccessException {
        GameData gameTest = serv.createGame(authTest.authToken(),"testGame1",null);
        serv.joinGame(authTest.authToken(),"WHITE", gameTest.gameID());
        Assertions.assertEquals(authTest.username(),gameTest.whiteUsername());
    }

    @Test
    public void joinGameBad() throws DataAccessException {
        GameData gameTest = serv.createGame(authTest.authToken(),"testGame1",null);
        Assertions.assertThrows(ServiceException.class,() -> {serv.joinGame(null,"BLACK", gameTest.gameID());});
    }
}
