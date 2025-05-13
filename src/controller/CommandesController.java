package controller;

import model.Commande;
import model.CommandeDAO;
import model.ConnexionDAO;
import model.LigneCommande;
import model.User;
import views.CommandesView;
import utils.PermissionManager;
import views.AddEditCommandeDialog;
import model.ClientDAO;
import model.Client;
import model.Article;

import javax.swing.*;
import java.sql.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Types;
import javax.swing.table.DefaultTableModel;

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
        commandesView.getSearchButton().addActionListener(e -> searchCommande());
        commandesView.getAddButton().addActionListener(e -> addCommande());
        commandesView.getEditButton().addActionListener(e -> editCommande());
        commandesView.getDeleteButton().addActionListener(e -> deleteCommande());
        commandesView.getBackButton().addActionListener(e -> backMenu());

        // Ajout du listener pour la sélection d'une commande
        commandesView.getCommandesTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadCommandeDetails();
            }
        });

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

    private void searchCommande() {
        String keyword = commandesView.getSearchField().getText();
        List<Commande> commandes = commandeDAO.searchCommandes(keyword);
        commandesView.updateTable(commandes);
    }

    public void addCommande() {
        if (!PermissionManager.canAddData(currentUser)) {
            JOptionPane.showMessageDialog(commandesView, 
                "Vous n'avez pas les permissions nécessaires pour ajouter des commandes.", 
                "Permission refusée", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddEditCommandeDialog dialog = new AddEditCommandeDialog(commandesView, "Nouvelle commande", true);
        
        // Charger la liste des clients
        ClientDAO clientDAO = new ClientDAO();
        dialog.setClients(clientDAO.getAllClients());
        
        dialog.setVisible(true);

        if (dialog.isValidated()) {
            Client selectedClient = dialog.getSelectedClient();
            String commentaire = dialog.getCommentaire();
            List<Article> articles = dialog.getSelectedArticles();

            try {
                connection.setAutoCommit(false); // Début de la transaction

                // Créer la commande
                int newCommandeId = -1;
                String createCommandeQuery = "{call creer_commande(?, ?, ?)}";
                try (CallableStatement cs = connection.prepareCall(createCommandeQuery)) {
                    cs.setInt(1, selectedClient.getId());
                    cs.setString(2, commentaire);
                    cs.registerOutParameter(3, Types.INTEGER);
                    cs.execute();
                    newCommandeId = cs.getInt(3);
                }

                if (newCommandeId > 0) {
                    // Mettre à jour l'utilisateur
                    String updateUserQuery = "UPDATE commandes SET ID_Utilisateur = ? WHERE ID = ?";
                    try (PreparedStatement ps = connection.prepareStatement(updateUserQuery)) {
                        ps.setInt(1, currentUser.getId());
                        ps.setInt(2, newCommandeId);
                        ps.executeUpdate();
                    }

                    // Ajouter les articles
                    boolean success = true;
                    for (Article article : articles) {
                        if (!commandeDAO.ajouterArticleCommande(newCommandeId, article.getId(), article.getQuantiteSelectionnee())) {
                            success = false;
                            break;
                        }
                    }

                    if (success) {
                        connection.commit(); // Valider la transaction
                        JOptionPane.showMessageDialog(commandesView, "Commande ajoutée avec succès !");
                        loadCommandes();
                        
                        // Sélectionner la nouvelle commande dans la table
                        DefaultTableModel model = (DefaultTableModel) commandesView.getCommandesTable().getModel();
                        for (int i = 0; i < model.getRowCount(); i++) {
                            if ((int)model.getValueAt(i, 0) == newCommandeId) {
                                commandesView.getCommandesTable().setRowSelectionInterval(i, i);
                                break;
                            }
                        }
                        
                        // Charger les détails de la nouvelle commande
                        loadCommandeDetails();
                    } else {
                        connection.rollback(); // Annuler la transaction
                        JOptionPane.showMessageDialog(commandesView,
                            "Erreur lors de l'ajout des articles à la commande.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    connection.rollback(); // Annuler la transaction
                    JOptionPane.showMessageDialog(commandesView,
                        "Erreur lors de la création de la commande.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                try {
                    connection.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(commandesView,
                    "Une erreur est survenue : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    connection.setAutoCommit(true); // Rétablir l'auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

    private void backMenu() {
        commandesView.dispose();
    }

    private void loadCommandeDetails() {
        int selectedRow = commandesView.getCommandesTable().getSelectedRow();
        if (selectedRow != -1) {
            int commandeId = (int) commandesView.getCommandesTable().getValueAt(selectedRow, 0);
            List<LigneCommande> lignes = commandeDAO.getLignesCommande(commandeId);
            commandesView.updateDetailsTable(lignes);
        }
    }
}