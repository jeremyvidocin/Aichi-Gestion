package model;

import java.sql.*;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = ConnexionDAO.getConnexion();
    }

    public User authenticateUser(String username, String password) {
        User user = null;
        String query = "SELECT * FROM utilisateurs WHERE nom = ? AND MotDePasse = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("MotDePasse"),
                        rs.getString("TypeAcces")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification : " + e.getMessage());
        }
        return user;
    }

    public boolean validateUser(String user, String password) {
        try {
            String query = "SELECT * FROM utilisateurs WHERE nom = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // Retourne true si un utilisateur est trouvé
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
