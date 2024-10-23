//package service;
//import dataaccess.DataAccess;
//import models.AuthData;
//import models.UserData;
//import java.util.UUID;
//
//public class UserService {
//    private final DataAccess dataAccess;
//
//    public UserService(DataAccess dataAccess) {
//        this.dataAccess = dataAccess;
//    }
//
//    public AuthData registerUser(UserData newUser) throws ServiceException {
//        AuthData newAuth;
//        if (dataAccess.getUser(newUser.username()) != null) {
//            throw new ServiceException("User already exists");
//        } else {
//            dataAccess.makeUser(newUser.username(), newUser);
//            newAuth = dataAccess.makeAuth(newUser.username(), new AuthData(UUID.randomUUID().toString(), newUser.username()));
//        }
//
//        return newAuth;
//    }
//}