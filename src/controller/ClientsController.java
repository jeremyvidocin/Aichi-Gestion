package controller;

import model.Client;
import model.ClientDAO; // Vous devez créer ce modèle pour interagir avec la base de données
import model.User;
import views.ClientsView;
import utils.PermissionManager;
import views.ModifierClientDialog;
import views.AjouterClientDialog;

import javax.swing.*;
import java.util.List;

public class ClientsController {
    private ClientsView clientsView;
    private ClientDAO clientDAO;
    private User currentUser;

    public ClientsController(User user) {
        this.currentUser = user;
        this.clientsView = new ClientsView();
        this.clientDAO = new ClientDAO();

        loadClients(); // Charge les clients lors de l'initialisation

        // Ajout des listeners pour les boutons
        clientsView.getSearchButton().addActionListener(e -> searchClient());
        clientsView.getAddButton().addActionListener(e -> addClient());
        clientsView.getEditButton().addActionListener(e -> editClient());
        clientsView.getDeleteButton().addActionListener(e -> deleteClient());
        clientsView.getBackButton().addActionListener(e -> backMenu());

        // Désactiver les boutons si l'utilisateur n'a pas les permissions
        if (!PermissionManager.canModifyData(currentUser)) {
            clientsView.getEditButton().setEnabled(false);
            clientsView.getAddButton().setEnabled(false);
            clientsView.getDeleteButton().setEnabled(false);
        }
    }

    public void showClientsView() {
        clientsView.setVisible(true);
    }

    private void loadClients() {
        List<Client> clients = clientDAO.getAllClients();
        clientsView.updateTable(clients); // Vous devez créer cette méthode dans ClientsView
    }

    private void searchClient() {
        String keyword = clientsView.getSearchField().getText();
        List<Client> clients = clientDAO.searchClientsByName(keyword);
        clientsView.updateTable(clients);
    }

    public void addClient() {
        if (!PermissionManager.canAddData(currentUser)) {
            JOptionPane.showMessageDialog(clientsView, "Vous n'avez pas les permissions nécessaires pour ajouter des clients.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AjouterClientDialog dialog = new AjouterClientDialog(clientsView);
        dialog.setVisible(true);

        if (dialog.isValidated()) {
            String nom = dialog.getNom();
            String prenom = dialog.getPrenom();
            String societe = dialog.getSociete();
            String adresse = dialog.getAdresse();
            String codePostal = dialog.getCodePostal();
            String ville = dialog.getVille();
            String pays = dialog.getPays();
            String telephone = dialog.getTelephone();
            String email = dialog.getEmail();

            if (clientDAO.addClient(nom, prenom, societe, adresse, codePostal, ville, pays, telephone, email)) {
                JOptionPane.showMessageDialog(clientsView, "Client ajouté avec succès !");
                loadClients(); // Recharge les clients
            } else {
                JOptionPane.showMessageDialog(clientsView, "Erreur lors de l'ajout du client.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void editClient() {
        if (!PermissionManager.canModifyData(currentUser)) {
            JOptionPane.showMessageDialog(clientsView, "Vous n'avez pas les permissions nécessaires pour modifier des clients.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = clientsView.getClientsTable().getSelectedRow();
        if (row != -1) {
            int id = (int) clientsView.getClientsTable().getValueAt(row, 0);
            Client client = clientDAO.getClientById(id);
            
            if (client != null) {
                ModifierClientDialog dialog = new ModifierClientDialog(clientsView, client);
                dialog.setVisible(true);
                
                if (dialog.isValidated()) {
                    String nom = dialog.getNom();
                    String prenom = dialog.getPrenom();
                    String societe = dialog.getSociete();
                    String adresse = dialog.getAdresse();
                    String codePostal = dialog.getCodePostal();
                    String ville = dialog.getVille();
                    String pays = dialog.getPays();
                    String telephone = dialog.getTelephone();
                    String email = dialog.getEmail();
                    
                    if (clientDAO.updateClientPartiel(id, nom, prenom, societe, adresse, 
                            codePostal, ville, pays, telephone, email)) {
                        JOptionPane.showMessageDialog(clientsView, "Client modifié avec succès !");
                        loadClients();
                    } else {
                        JOptionPane.showMessageDialog(clientsView, "Erreur lors de la modification.", 
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(clientsView, "Veuillez sélectionner un client.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void deleteClient() {
        if (!PermissionManager.canDeleteData(currentUser)) {
            JOptionPane.showMessageDialog(clientsView, "Vous n'avez pas les permissions nécessaires pour supprimer des clients.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Logique pour supprimer un client
        int row = clientsView.getClientsTable().getSelectedRow();
        if (row != -1) {
            int id = (int) clientsView.getClientsTable().getValueAt(row, 0); // Récupère l'ID du client sélectionné
            if (clientDAO.deleteClient(id)) {
                JOptionPane.showMessageDialog(clientsView, "Client supprimé !");
                loadClients(); // Recharge les clients
            } else {
                JOptionPane.showMessageDialog(clientsView, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(clientsView, "Veuillez sélectionner un client.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void backMenu() {
    	clientsView.dispose();
    }
}