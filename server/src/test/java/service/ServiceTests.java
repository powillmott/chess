package service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;

public class ServiceTests {
    @BeforeAll
    public static void init() {
        existingUser = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");

        newUser = new TestUser("NewUser", "newUserPassword", "nu@mail.com");

        createRequest = new TestCreateRequest("testGame");
    }

    @Test
    public void ClearAllGood() {

    }

    @Test
    public void ClearAllBad() {}

    @Test
    public void RegisterUserGood() {}

    @Test
    public void RegisterUserBad() {}

    @Test
    public void LoginUserGood() {}

    @Test
    public void LoginUserBad() {}

    @Test
    public void LogoutUserGood() {}

    @Test
    public void LogoutUserBad() {}

    @Test
    public void CreateGameGood() {}

    @Test
    public void CreateGameBad() {}

    @Test
    public void GetAllGamesGood() {}

    @Test
    public void GetAllGamesBad() {}

    @Test
    public void JoinGameGood() {}

    @Test
    public void JoinGameBad() {}
}
