package DAO;

import Model.User;
import database.DatabaseConnection;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class UserDAO {
    private static final String ALGORITHM = "AES";
    private static final String default_key = "group01";
    private static SecretKeySpec secretKey;

    private static void prepareSecretKey() {
        try {
            var key = default_key.getBytes(StandardCharsets.UTF_8);
            var sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // mã hóa password
    public static String encryptPassword(String password)  {
        try {
            prepareSecretKey();
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    // giải mã mã hóa
    public static String decryptpassword(String encrypted_password) {
        prepareSecretKey();
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted_password)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> selectAll()  {
        // list chứa user
        var list = new ArrayList<User>();
        // query of sql
        var query = "select * from users";
        try (var statement = DatabaseConnection.getConnectionInstance().createStatement()) {
            // return resultSet
            var resultSet = statement.executeQuery(query);
            // duyệt qua tất cả các dòng thêm user mới vào list
            while (resultSet.next()) {
                list.add(
                        new User(
                                resultSet.getString("user_id"),
                                resultSet.getString("full_name"),
                                resultSet.getString("password_hash"),
                                resultSet.getBoolean("is_host")
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static User selectByAccount(String username, String password) {
        var user = new User();
        var query = " select * from users where user_id=? and password_hash=?";
        try (var ps = DatabaseConnection.getConnectionInstance().prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            var resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user.setUser_id(resultSet.getString("user_id"));
                user.setFull_name(resultSet.getString("full_name"));
                user.setPassword(resultSet.getString("password_hash"));
                user.setHost(resultSet.getBoolean("is_host"));
                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static User selectByID(String userID) {
        var user = new User();
        var query = " select * from users where user_id=?";
        try
                (var ps = DatabaseConnection.getConnectionInstance().prepareStatement(query)) {
            ps.setString(1, userID);
            var resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user.setUser_id(resultSet.getString("user_id"));
                user.setFull_name(resultSet.getString("full_name"));
                user.setPassword(resultSet.getString("password_hash"));
                user.setHost(resultSet.getBoolean("is_host"));
                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static boolean insert(User user)  {
        var query = "insert into users values(?,?,?,?)";
        var password_encrypted = encryptPassword(user.getPassword());
        try (var ps = DatabaseConnection.getConnectionInstance().prepareStatement(query)) {
            ps.setString(1, user.getUser_id());

            ps.setString(2, user.getFull_name());
            ps.setString(3, password_encrypted);
            ps.setBoolean(4, user.isHost());
            var count = ps.executeUpdate();
            return count != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean update(User user) {
        var query = "update users set full_name = ?, password_hash = ?, is_host = ? where user_id = ?";
        try (var ps = DatabaseConnection.getConnectionInstance().prepareStatement(query)) {
            ps.setString(1, user.getFull_name());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isHost());
            ps.setString(4, user.getUser_id());
            var count = ps.executeUpdate();
            return count != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean delete(String user_id) {
        var query = "delete from users where user_id = ?";
        try (var ps = DatabaseConnection.getConnectionInstance().prepareStatement(query)) {
            ps.setString(1, user_id);
            var count = ps.executeUpdate();
            return count != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
     String test = "test";
     String encrypt = encryptPassword(test);
        System.out.println(encrypt);
     String decrypt = decryptpassword(encrypt);
        System.out.println(decrypt);
    }

}
