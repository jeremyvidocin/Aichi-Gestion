package controller;

import model.Commande;
import model.CommandeDAO;
import model.ConnexionDAO;
import model.LigneCommande;
import model.User;
import views.CommandesView;
import utils.PermissionManager;

import javax.swing.*;
import java.sql.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandesController {
    private CommandesView commandesView;
    private CommandeDAO commandeDAO;
    private Connection connection;
    private User currentUser;

    public CommandesController(User user) {
        this.currentUser = user;
        this.commandesView = new CommandesView();
        this.commandeDAO = new CommandeDAO();
        this.connection = ConnexionDAO.getConnexion();

        loadCommandes();

        // Ajout des listeners pour les boutons
        commandesView.getAddButton().addActionListener(e -> addCommande());
        commandesView.getEditButton().addActionListener(e -> editCommande());
        commandesView.getDeleteButton().addActionListener(e -> deleteCommande());

        // Désactiver les boutons si l'utilisateur n'a pas les permissions
        if (!PermissionManager.canModifyData(currentUser)) {
            commandesView.getEditButton().setEnabled(false);
            commandesView.getAddButton().setEnabled(false);
            commandesView.getDeleteButton().setEnabled(false);
        }
    }

    public void showCommandesView() {
        commandesView.setVisible(true);
    }

    private void loadCommandes() {
        List<Commande> commandes = commandeDAO.getAllCommandes();
        commandesView.updateTable(commandes);
    }

    public void addCommande() {
        if (!PermissionManager.canAddData(currentUser)) {
            JOptionPane.showMessageDialog(commandesView, "Vous n'avez pas les permissions nécessaires pour ajouter des commandes.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String clientIdStr = JOptionPane.showInputDialog("ID du client :");
        String commentaire = JOptionPane.showInputDialog("Commentaire :");

        try {
            int clientId = Integer.parseInt(clientIdStr);
            if (commandeDAO.addCommande(clientId, commentaire, 0)) {
                JOptionPane.showMessageDialog(commandesView, "Commande ajoutée !");

                int ajouterArticle = JOptionPane.showConfirmDialog(commandesView,
                        "Voulez-vous ajouter un article à cette commande?",
                        "Ajouter un article",
                        JOptionPane.YES_NO_OPTION);

                if (ajouterArticle == JOptionPane.YES_OPTION) {
                    String articleIdStr = JOptionPane.showInputDialog("ID de l'article :");
                    String quantiteStr = JOptionPane.showInputDialog("Quantité :");

                    try {
                        int articleId = Integer.parseInt(articleIdStr);
                        int quantite = Integer.parseInt(quantiteStr);

                        List<Commande> commandes = commandeDAO.getAllCommandes();
                        int lastId = commandes.get(commandes.size() - 1).getId();

                        if (commandeDAO.ajouterArticleCommande(lastId, articleId, quantite)) {
                            JOptionPane.showMessageDialog(commandesView, "Article ajouté à la commande !");
                        } else {
                            JOptionPane.showMessageDialog(commandesView,
                                    "Erreur lors de l'ajout de l'article.",
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(commandesView,
                                "Veuillez entrer des nombres valides.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }

                loadCommandes();
            } else {
                JOptionPane.showMessageDialog(commandesView, "Erreur lors de l'ajout.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(commandesView, "Veuillez entrer un ID de client valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void editCommande() {
        if (!PermissionManager.canModifyData(currentUser)) {
            JOptionPane.showMessageDialog(commandesView, "Vous n'avez pas les permissions nécessaires pour modifier des commandes.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = commandesView.getCommandesTable().getSelectedRow();
        if (row != -1) {
            int id = (int) commandesView.getCommandesTable().getValueAt(row, 0);

            String[] options = {"Changer le statut", "Modifier le client", "Gérer les articles", "Annuler"};
            int choice = JOptionPane.showOptionDialog(commandesView,
                    "Quelle modification souhaitez-vous effectuer ?",
                    "Modifier la commande",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0: // Changer le statut
                    String[] statutsCode = {"RECUE", "EN_ATTENTE", "VALIDEE", "EN_PREPARATION", "EXPEDIEE", "EN_COURS", "LIVREE", "ANNULEE"};
                    String[] statutsLibelle = {"Reçue", "En attente", "Validée", "En préparation", "Expédiée", "En cours de livraison", "Livrée", "Annulée"};
                    int statutIndex = JOptionPane.showOptionDialog(commandesView,
                            "Choisissez le nouveau statut :",
                            "Changer le statut",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            statutsLibelle,
                            statutsLibelle[0]);

                    if (statutIndex >= 0) {
                        String commentaire = JOptionPane.showInputDialog("Commentaire sur le changement de statut :");
                        if (commandeDAO.changerStatutCommande(id, statutsCode[statutIndex], commentaire, 1)) {
                            JOptionPane.showMessageDialog(commandesView, "Statut de la commande modifié !");
                            loadCommandes();
                        } else {
                            JOptionPane.showMessageDialog(commandesView,
                                    "Erreur lors du changement de statut.",
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case 1: // Modifier le client
                    String newClientIdStr = JOptionPane.showInputDialog("Nouvel ID client :");
                    try {
                        int newClientId = Integer.parseInt(newClientIdStr);
                        if (commandeDAO.clientExists(newClientId)) {
                            String updateQuery = "UPDATE commandes SET ID_Client = ? WHERE ID = ?";
                            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                                stmt.setInt(1, newClientId);
                                stmt.setInt(2, id);
                                stmt.executeUpdate();
                                JOptionPane.showMessageDialog(commandesView, "Client de la commande modifié !");
                                loadCommandes();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(commandesView,
                                        "Erreur lors de la modification du client.",
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(commandesView,
                                    "Ce client n'existe pas.",
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(commandesView,
                                "Veuillez entrer un ID de client valide.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                    break;

                case 2: // Gérer les articles
                    List<LigneCommande> lignes = commandeDAO.getLignesCommande(id);
                    StringBuilder sb = new StringBuilder("Articles de la commande:\n\n");

                    for (LigneCommande ligne : lignes) {
                        sb.append(ligne.getDesignation())
                                .append(" (").append(ligne.getReference()).append(")")
                                .append(" - Quantité: ").append(ligne.getQuantite())
                                .append(" - Prix: ").append(ligne.getPrixUnitaireHT())
                                .append("€ HT\n");
                    }

                    String[] optionsArticles = {"Ajouter un article", "Supprimer un article", "Retour"};
                    int choixArticle = JOptionPane.showOptionDialog(commandesView,
                            sb.toString(),
                            "Gestion des articles",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            optionsArticles,
                            optionsArticles[0]);

                    if (choixArticle == 0) { // Ajouter un article
                        String articleIdStr = JOptionPane.showInputDialog("ID de l'article à ajouter :");
                        String quantiteStr = JOptionPane.showInputDialog("Quantité :");

                        try {
                            int articleId = Integer.parseInt(articleIdStr);
                            int quantite = Integer.parseInt(quantiteStr);

                            if (commandeDAO.ajouterArticleCommande(id, articleId, quantite)) {
                                JOptionPane.showMessageDialog(commandesView, "Article ajouté à la commande !");
                                loadCommandes();
                            } else {
                                JOptionPane.showMessageDialog(commandesView,
                                        "Erreur lors de l'ajout de l'article.",
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(commandesView,
                                    "Veuillez entrer des nombres valides.",
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choixArticle == 1) { // Supprimer un article
                        String ligneIdStr = JOptionPane.showInputDialog("ID de la ligne à supprimer :");
                        try {
                            int ligneId = Integer.parseInt(ligneIdStr);
                            String deleteQuery = "DELETE FROM lignes_commande WHERE ID = ? AND ID_Commande = ?";
                            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                                stmt.setInt(1, ligneId);
                                stmt.setInt(2, id);
                                int rowsAffected = stmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(commandesView, "Article supprimé de la commande !");
                                    loadCommandes();
                                } else {
                                    JOptionPane.showMessageDialog(commandesView,
                                            "Aucun article trouvé avec cet ID pour cette commande.",
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(commandesView,
                                        "Erreur lors de la suppression de l'article.",
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(commandesView,
                                    "Veuillez entrer un ID de ligne valide.",
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case 3: // Annuler
                default:
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(commandesView, "Veuillez sélectionner une commande.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void deleteCommande() {
        if (!PermissionManager.canDeleteData(currentUser)) {
            JOptionPane.showMessageDialog(commandesView, "Vous n'avez pas les permissions nécessaires pour supprimer des commandes.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = commandesView.getCommandesTable().getSelectedRow();
        if (row != -1) {
            int id = (int) commandesView.getCommandesTable().getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(commandesView,
                    "Êtes-vous sûr de vouloir supprimer cette commande?\nCette action est irréversible.",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (commandeDAO.deleteCommande(id)) {
                    JOptionPane.showMessageDialog(commandesView, "Commande supprimée !");
                    loadCommandes();
                } else {
                    JOptionPane.showMessageDialog(commandesView, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(commandesView, "Veuillez sélectionner une commande.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }
}