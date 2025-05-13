package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDAO {
    private static Connection connexion = null;
    // Modification pour XAMPP qui utilise le port 3306 par défaut
    private static final String URL = "jdbc:mysql://localhost:3306/aichi_box?serverTimezone=UTC&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Par défaut, pas de mot de passe sur XAMPP

    public static Connection getConnexion() {
        try {
            if (connexion == null || connexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connexion = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connexion réussie à la base de données.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
        return connexion;
    }
}