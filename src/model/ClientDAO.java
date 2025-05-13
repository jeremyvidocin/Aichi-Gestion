package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ClientDAO {
    private Connection connection;

    public ClientDAO() {
        this.connection = ConnexionDAO.getConnexion();
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                clients.add(createClientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public List<Client> searchClientsByName(String name) {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients WHERE Nom LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clients.add(new Client(
                    rs.getInt("ID"),
                    rs.getString("Nom"),
                    rs.getString("Adresse"),
                    rs.getString("Telephone"),
                    rs.getString("Email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    private Client createClientFromResultSet(ResultSet rs) throws SQLException {
        LocalDateTime dateInscription = rs.getTimestamp("DateInscription") != null ?
                rs.getTimestamp("DateInscription").toLocalDateTime() : null;

        LocalDateTime dateDerniereCommande = rs.getTimestamp("DateDerniereCommande") != null ?
                rs.getTimestamp("DateDerniereCommande").toLocalDateTime() : null;

        return new Client(
                rs.getInt("ID"),
                rs.getString("Nom"),
                rs.getString("Prenom"),
                rs.getString("Societe"),
                rs.getString("Adresse"),
                rs.getString("CodePostal"),
                rs.getString("Ville"),
                rs.getString("Pays"),
                rs.getString("Telephone"),
                rs.getString("Email"),
                dateInscription,
                rs.getBoolean("Actif"),
                dateDerniereCommande
        );
    }

    public boolean addClient(String nom, String prenom, String societe, String adresse,
                             String codePostal, String ville, String pays,
                             String telephone, String email) {
        String query = "INSERT INTO clients (Nom, Prenom, Societe, Adresse, CodePostal, Ville, Pays, Telephone, Email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, societe);
            stmt.setString(4, adresse);
            stmt.setString(5, codePostal);
            stmt.setString(6, ville);
            stmt.setString(7, pays);
            stmt.setString(8, telephone);
            stmt.setString(9, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Pour compatibilité avec le code existant
    public boolean addClient(String nom, String adresse, String telephone, String email) {
        return addClient(nom, null, null, adresse, null, null, "France", telephone, email);
    }

    public boolean updateClient(int id, String nom, String prenom, String societe,
                                String adresse, String codePostal, String ville,
                                String pays, String telephone, String email) {
        String query = "UPDATE clients SET Nom = ?, Prenom = ?, Societe = ?, " +
                "Adresse = ?, CodePostal = ?, Ville = ?, Pays = ?, " +
                "Telephone = ?, Email = ? WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, societe);
            stmt.setString(4, adresse);
            stmt.setString(5, codePostal);
            stmt.setString(6, ville);
            stmt.setString(7, pays);
            stmt.setString(8, telephone);
            stmt.setString(9, email);
            stmt.setInt(10, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Pour compatibilité avec le code existant
    public boolean updateClient(int id, String nom, String adresse, String telephone, String email) {
        Client client = getClientById(id);
        if (client == null) return false;

        return updateClient(
                id, nom, client.getPrenom(), client.getSociete(),
                adresse, client.getCodePostal(), client.getVille(),
                client.getPays(), telephone, email
        );
    }

    public Client getClientById(int id) {
        String query = "SELECT * FROM clients WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createClientFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteClient(int id) {
        String query = "DELETE FROM clients WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
