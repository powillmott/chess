package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.sql.SQLException;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String userName) {
        return null;
    }

    @Override
    public UserData makeUser(String userName, UserData userData) {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        int id = executeUpdate(statement, userName, userData.password(), userData.email());
        return userData;
    }

    @Override
    public String getAuth(String userName) {
        return "";
    }

    @Override
    public AuthData makeAuth(String authToken, String userName) {
        return null;
    }

    @Override
    public void clearAllUsers() {

    }

    @Override
    public void clearAllAuth() {

    }

    @Override
    public void clearAllGames() {

    }

    @Override
    public Map<Integer, GameData> getAllGames() {
        return Map.of();
    }

    @Override
    public Map<String, UserData> getAllUsers() {
        return Map.of();
    }

    @Override
    public Map<String, String> getAllAuth() {
        return Map.of();
    }

    @Override
    public boolean validAuth(String authToken) {
        return false;
    }

    @Override
    public void removeUser(String authToken) {

    }

    @Override
    public Collection<GameData> getGames() {
        return List.of();
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void joinGame(Integer gameID, String playerColor, String userName) {

    }

    @Override
    public void makeGame(GameData game) {

    }

    @Override
    public String getUserName(String authToken) {
        return "";
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Execute update failed");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                'username' varchar(256) NOT NULL,
                'password' varchar(256) NOT NULL,
                'email' varchar(256) NOT NULL,
                PRIMARY KEY ('username')
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                'gameID' int(11) NOT NULL AUTO_INCREMENT,
                'whiteUsername' varchar(256) NOT NULL,
                'blackUsername' varchar(256) NOT NULL,
                'gameName' varchar(256) NOT NULL,
                'game' longtext NOT NULL,
                PRIMARY KEY ('gameID')
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            'authToken' varchar(256) NOT NULL,
            'userName' varchar(256) NOT NULL,
            PRIMARY KEY ('authToken')
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
