package main;

import controller.ConnexionController;
import model.UserDAO;
import views.FConnexionView;

public class Main {
    public static void main(String[] args) {
        // Crée la vue de connexion
        FConnexionView connexionView = new FConnexionView();

        // Initialise le DAO pour gérer les utilisateurs
        UserDAO userDAO = new UserDAO();

        // Crée le contrôleur pour gérer la connexion
        new ConnexionController(connexionView, userDAO);

        // Affiche la vue de connexion
        connexionView.setVisible(true);
    }
}
