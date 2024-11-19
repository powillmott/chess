package client;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
    public void registerGood() {
    }

    @Test
    public void registerBad() {
    }

    @Test
    public void logoutGood() {
    }

    @Test
    public void logoutBad() {
    }

    @Test
    public void createGameGood() {
    }

    @Test
    public void createGameBad() {
    }

    @Test
    public void listGamesGood() {
    }

    @Test
    public void listGamesBad() {
    }

    @Test
    public void playGameGood() {
    }

    @Test
    public void playGameBad() {
    }

}
