package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = ConnexionDAO.getConnexion();
    }

    public User authenticateUser(String email, String password) {
        try {
            // D'abord on récupère l'utilisateur par son email
            String query = "SELECT * FROM utilisateurs WHERE Email = ? AND Actif = 1";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // On récupère le hash stocké
                String storedHash = rs.getString("MotDePasse");
                // On hash le mot de passe saisi
                String hashedPassword = hashPassword(password);
                
                // Si les hashs correspondent
                if (storedHash.equals(hashedPassword)) {
                    return new User(
                        rs.getInt("ID"),
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getString("Email"),
                        rs.getString("Telephone"),
                        rs.getString("TypeAcces")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            // On utilise une requête pour obtenir le hash SHA-256
            String query = "SELECT SHA2(?, 256) as hash";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(int userId, String newPassword) {
        try {
            String hashedPassword = hashPassword(newPassword);
            String query = "UPDATE utilisateurs SET MotDePasse = ? WHERE ID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(String nom, String prenom, String email, String telephone, String password, String typeAcces) {
        try {
            String hashedPassword = hashPassword(password);
            String query = "INSERT INTO utilisateurs (Nom, Prenom, Email, Telephone, MotDePasse, TypeAcces, DateCreation, Actif) VALUES (?, ?, ?, ?, ?, ?, NOW(), 1)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setString(4, telephone);
            ps.setString(5, hashedPassword);
            ps.setString(6, typeAcces);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        try {
            String query = "UPDATE utilisateurs SET Actif = 0 WHERE ID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int id, String nom, String prenom, String email, String telephone, String typeAcces) {
        try {
            String query = "UPDATE utilisateurs SET Nom = ?, Prenom = ?, Email = ?, Telephone = ?, TypeAcces = ? WHERE ID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setString(4, telephone);
            ps.setString(5, typeAcces);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT * FROM utilisateurs WHERE Actif = 1 ORDER BY Nom";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("ID"),
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getString("Email"),
                    rs.getString("Telephone"),
                    rs.getString("TypeAcces")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT * FROM utilisateurs WHERE Actif = 1 AND (Nom LIKE ? OR Prenom LIKE ? OR Email LIKE ?) ORDER BY Nom";
            PreparedStatement ps = connection.prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("ID"),
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getString("Email"),
                    rs.getString("Telephone"),
                    rs.getString("TypeAcces")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
