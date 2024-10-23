package service;
import dataaccess.DataAccess;
import models.AuthData;
import models.UserData;

import java.util.Objects;
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
        if (newUser.username() == null | newUser.password() == null | newUser.email() == null) {
            throw new ServiceException("bad request");
        } else if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("already taken");
        } else {
            dataAccess.makeUser(newUser.username(), newUser);
            newAuth = createAuth(newUser.username());
        }

        return newAuth;
    }

    private AuthData createAuth(String userName){
        return dataAccess.makeAuth(userName, new AuthData(UUID.randomUUID().toString(), userName));
    }

    public AuthData loginUser(String username, String password) throws ServiceException {
        AuthData newAuth = null;
        if (Objects.equals(dataAccess.getUser(username).password(), password)) {
            newAuth = createAuth(username);
        }
        return newAuth;
    }

//    public String createGame(String gameName) throws ServiceException {
//
//    }



}
