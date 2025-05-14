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
import views.ModifierCommandeDialog;

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
            Commande commande = commandeDAO.getCommandeById(id);
            
            if (commande != null) {
                List<LigneCommande> lignes = commandeDAO.getLignesCommande(id);
                ModifierCommandeDialog dialog = new ModifierCommandeDialog(commandesView, commande, lignes);
                dialog.setVisible(true);
                
                if (dialog.isValidated()) {
                    Integer newClientId = dialog.getNewClientId();
                    String newStatut = dialog.getNewStatut();
                    String newCommentaire = dialog.getNewCommentaire();
                    String newAdresseLivraison = dialog.getNewAdresseLivraison();
                    List<LigneCommande> newLignes = dialog.getLignesCommande();
                    
                    if (commandeDAO.updateCommandePartielle(id, newClientId, newStatut, 
                            newCommentaire, newAdresseLivraison, newLignes)) {
                        JOptionPane.showMessageDialog(commandesView, "Commande modifiée avec succès !");
                        loadCommandes();
                        loadCommandeDetails(); // Recharger les détails
                    } else {
                        JOptionPane.showMessageDialog(commandesView, "Erreur lors de la modification.", 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
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