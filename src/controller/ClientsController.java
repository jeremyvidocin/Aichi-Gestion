package controller;

import model.Client;
import model.ClientDAO; // Vous devez créer ce modèle pour interagir avec la base de données
import model.User;
import views.ClientsView;
import utils.PermissionManager;

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

    public void addClient() {
        if (!PermissionManager.canAddData(currentUser)) {
            JOptionPane.showMessageDialog(clientsView, "Vous n'avez pas les permissions nécessaires pour ajouter des clients.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Logique pour ajouter un client
        String name = JOptionPane.showInputDialog("Nom du client :");
        String adresse = JOptionPane.showInputDialog("Adresse du client :");
        String tel = JOptionPane.showInputDialog("Numéro de téléphone du client :");
        String email = JOptionPane.showInputDialog("Email du client :");


        if (clientDAO.addClient(name, adresse, tel, email)) {
            JOptionPane.showMessageDialog(clientsView, "Client ajouté !");
            loadClients(); // Recharge les clients
        } else {
            JOptionPane.showMessageDialog(clientsView, "Erreur lors de l'ajout.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void editClient() {
        if (!PermissionManager.canModifyData(currentUser)) {
            JOptionPane.showMessageDialog(clientsView, "Vous n'avez pas les permissions nécessaires pour modifier des clients.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Logique pour modifier un client
        int row = clientsView.getClientsTable().getSelectedRow();
        if (row != -1) {
            int id = (int) clientsView.getClientsTable().getValueAt(row, 0); // Récupère l'ID du client sélectionné
            // Modifier les détails du client
            String newName = JOptionPane.showInputDialog("Nouveau nom du client :");
            String newAdr = JOptionPane.showInputDialog("Nouvelle Adresse du client :");
            String newTel = JOptionPane.showInputDialog("Nouveau numéro de Téléphone du client :");
            String newEmail = JOptionPane.showInputDialog("Nouvel email du client :");

            if (clientDAO.updateClient(id,newName, newAdr, newTel, newEmail)) {
                JOptionPane.showMessageDialog(clientsView, "Client modifié !");
                loadClients(); // Recharge les clients
            } else {
                JOptionPane.showMessageDialog(clientsView, "Erreur lors de la modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
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