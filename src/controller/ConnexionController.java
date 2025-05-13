package controller;

import model.User;
import model.UserDAO;
import views.AccueilView;
import views.FConnexionView;

import javax.swing.*;

public class ConnexionController {
    private FConnexionView connexionView;
    private UserDAO userDAO;

    public ConnexionController(FConnexionView connexionView, UserDAO userDAO) {
        this.connexionView = connexionView;
        this.userDAO = userDAO;

        // Ajout d'un listener pour le bouton de connexion
        connexionView.getConnexionButton().addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String username = connexionView.getUserField().getText();
        String password = new String(connexionView.getPasswordField().getPassword());

        User user = userDAO.authenticateUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(connexionView, "Connexion réussie !");
            connexionView.dispose(); // Ferme la fenêtre de connexion
            new AccueilView(user).setVisible(true); // Ouvre la vue d'accueil
        } else {
            JOptionPane.showMessageDialog(connexionView, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
