package service;
import dataaccess.DataAccess;
import models.AuthData;
import models.UserData;
import java.util.UUID;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public boolean clearAll() {
        dataAccess.clearAllUsers();
        dataAccess.clearAllGames();
        dataAccess.clearAllAuth();
        return dataAccess.getAllUsers().isEmpty() & dataAccess.getAllGames().isEmpty() & dataAccess.getAllAuth().isEmpty();
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        AuthData newAuth;
        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("User already exists");
        } else {
            dataAccess.makeUser(newUser.username(), newUser);
            newAuth = dataAccess.makeAuth(newUser.username(), new AuthData(UUID.randomUUID().toString(), newUser.username()));
        }

        return newAuth;
    }



}
