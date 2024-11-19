package client;

import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import models.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {
    static DataAccess testDataAccess;
    private static Server server;
    private static ServerFacade sf = null;

    @BeforeAll
    public static void init() {
        server = new Server();
        testDataAccess = new MySqlDataAccess();
        var port = server.run(0);
        sf = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setUp() throws Exception {
        testDataAccess.clearAllAuth();
        testDataAccess.clearAllUsers();
        testDataAccess.clearAllGames();
        sf.register("testUser","testPassword","test@email");
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void loginGood() throws Exception {
        AuthData res = sf.login("testUser","testPassword");
        Assertions.assertTrue(testDataAccess.validAuth(res.authToken()));
    }

    @Test
    public void loginBad() {
        Assertions.assertThrows(Exception.class, () -> {sf.login("hi","hi");});
    }

    @Test
    public void registerGood() throws Exception {
        AuthData res = sf.register("testUser2","testPassword2","test@email2");
        Assertions.assertTrue(testDataAccess.validAuth(res.authToken()));
    }

    @Test
    public void registerBad() {
        Assertions.assertThrows(Exception.class, () -> {sf.register(null,null,null);});
    }

    @Test
    public void logoutGood() throws Exception {
        AuthData res = sf.login("testUser","testPassword");
        sf.logout(res.authToken());
        Assertions.assertFalse(testDataAccess.validAuth(res.authToken()));
    }

    @Test
    public void logoutBad() {
        Assertions.assertThrows(Exception.class, () -> {sf.logout(null);});
    }

    @Test
    public void createGameGood() throws Exception {
        AuthData auth = sf.login("testUser","testPassword");
        int gameID = sf.createGame(auth.authToken(),"testGame");
        Assertions.assertEquals("testGame",testDataAccess.getGame(gameID).gameName());
    }

    @Test
    public void createGameBad() throws Exception {
        Assertions.assertThrows(Exception.class, () ->  {sf.createGame(null,null);});
    }

    @Test
    public void listGamesGood() throws Exception {
        AuthData authToken = sf.login("testUser","testPassword");
        sf.createGame(authToken.authToken(),"game1");
        sf.createGame(authToken.authToken(),"game2");
        Assertions.assertEquals(2,sf.listGames(authToken.authToken()).size());
    }

    @Test
    public void listGamesBad() {
        Assertions.assertThrows(Exception.class, () -> {sf.listGames(null);});
    }

    @Test
    public void playGameGood() throws Exception {
        AuthData authToken = sf.login("testUser","testPassword");
        int gameID = sf.createGame(authToken.authToken(),"testGame");
        Assertions.assertDoesNotThrow(() -> sf.playGame(authToken.authToken(),"WHITE",gameID));
    }

    @Test
    public void playGameBad() {
        Assertions.assertThrows(Exception.class, () -> {sf.playGame(null,null,0);});
    }

}
