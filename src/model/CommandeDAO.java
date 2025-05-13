package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    private Connection connection;

    public CommandeDAO() {
        this.connection = ConnexionDAO.getConnexion();
    }

    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT c.*, cl.Nom as NomClient " +
                      "FROM commandes c " +
                      "LEFT JOIN clients cl ON c.ID_Client = cl.ID " +
                      "ORDER BY c.DateCommande DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                commandes.add(new Commande(
                    rs.getInt("ID"),
                    rs.getString("Numero"),
                    rs.getInt("ID_Client"),
                    rs.getTimestamp("DateCommande"),
                    rs.getString("Statut"),
                    rs.getDouble("MontantHT"),
                    rs.getDouble("MontantTVA"),
                    rs.getDouble("MontantTTC"),
                    rs.getString("NomClient"),
                    null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Nombre de commandes récupérées : " + commandes.size());
        return commandes;
    }

    private Commande createCommandeFromResultSet(ResultSet rs) throws SQLException {
        return new Commande(
                rs.getInt("ID"),
                rs.getString("Numero"),
                rs.getString("Reference"),
                rs.getInt("ID_Client"),
                rs.getTimestamp("DateCommande"),
                rs.getTimestamp("DateValidation"),
                rs.getTimestamp("DateExpedition"),
                rs.getTimestamp("DateLivraison"),
                rs.getString("Statut"),
                rs.getInt("ID_Statut"),
                rs.getDouble("MontantHT"),
                rs.getDouble("TauxTVA"),
                rs.getDouble("MontantTVA"),
                rs.getDouble("MontantTTC"),
                rs.getString("Commentaire"),
                rs.getString("AdresseLivraison"),
                rs.getInt("ID_Utilisateur")
        );
    }

    public boolean addCommande(int clientId, String commentaire, int utilisateurId) {
        try {
            // On utilise une transaction pour s'assurer que tout est cohérent
            connection.setAutoCommit(false);

            // Appel à la procédure stockée creer_commande
            CallableStatement cs = connection.prepareCall("{call creer_commande(?, ?, ?)}");
            cs.setInt(1, clientId);
            cs.setString(2, commentaire);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();

            // Récupérer l'ID de la commande créée
            int idCommande = cs.getInt(3);

            // Si l'utilisateur est fourni, on met à jour la commande
            if (utilisateurId > 0) {
                String updateQuery = "UPDATE commandes SET ID_Utilisateur = ? WHERE ID = ?";
                PreparedStatement ps = connection.prepareStatement(updateQuery);
                ps.setInt(1, utilisateurId);
                ps.setInt(2, idCommande);
                ps.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Pour compatibilité avec le code existant
    public boolean addCommande(int clientId, int articleId, Date date, String statut) {
        // Créer la commande
        if (addCommande(clientId, null, 0)) {
            // Récupérer l'ID de la dernière commande créée
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID() as last_id")) {
                if (rs.next()) {
                    int commandeId = rs.getInt("last_id");
                    // Ajouter l'article à la commande
                    return ajouterArticleCommande(commandeId, articleId, 1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean ajouterArticleCommande(int idCommande, int idArticle, int quantite) {
        try {
            // Appel à la procédure stockée ajouter_article_commande
            CallableStatement cs = connection.prepareCall("{call ajouter_article_commande(?, ?, ?)}");
            cs.setInt(1, idCommande);
            cs.setInt(2, idArticle);
            cs.setInt(3, quantite);
            cs.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changerStatutCommande(int idCommande, String codeStatut, String commentaire, int idUtilisateur) {
        try {
            // Appel à la procédure stockée changer_statut_commande
            CallableStatement cs = connection.prepareCall("{call changer_statut_commande(?, ?, ?, ?)}");
            cs.setInt(1, idCommande);
            cs.setString(2, codeStatut);
            cs.setString(3, commentaire);
            cs.setInt(4, idUtilisateur);
            cs.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Pour compatibilité avec le code existant
    public boolean updateCommande(int id, int newClientId, int newArticleId, Date date, String newStatut) {
        // Mettre à jour le client et le statut
        String query = "UPDATE commandes SET ID_Client = ?, Statut = ? WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newClientId);
            stmt.setString(2, newStatut);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            // Supprime les lignes de commande existantes
            String deleteQuery = "DELETE FROM lignes_commande WHERE ID_Commande = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();
            }

            // Ajoute le nouvel article
            return ajouterArticleCommande(id, newArticleId, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCommande(int id) {
        String query = "DELETE FROM commandes WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clientExists(int clientId) {
        String query = "SELECT COUNT(*) FROM clients WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<LigneCommande> getLignesCommande(int idCommande) {
        List<LigneCommande> lignes = new ArrayList<>();
        String query = "SELECT * FROM lignes_commande WHERE ID_Commande = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lignes.add(new LigneCommande(
                        rs.getInt("ID"),
                        rs.getInt("ID_Commande"),
                        rs.getInt("ID_Article"),
                        rs.getString("Reference"),
                        rs.getString("Designation"),
                        rs.getInt("Quantite"),
                        rs.getDouble("PrixUnitaireHT"),
                        rs.getDouble("TauxTVA")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lignes;
    }

    public List<Commande> searchCommandes(String keyword) {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT c.*, cl.Nom as NomClient " +
                      "FROM commandes c " +
                      "LEFT JOIN clients cl ON c.ID_Client = cl.ID " +
                      "WHERE c.Numero LIKE ? " +
                      "OR cl.Nom LIKE ? " +
                      "OR c.Statut LIKE ? " +
                      "ORDER BY c.DateCommande DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                commandes.add(new Commande(
                    rs.getInt("ID"),
                    rs.getString("Numero"),
                    rs.getInt("ID_Client"),
                    rs.getTimestamp("DateCommande"),
                    rs.getString("Statut"),
                    rs.getDouble("MontantHT"),
                    rs.getDouble("MontantTVA"),
                    rs.getDouble("MontantTTC"),
                    rs.getString("NomClient"),
                    null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
}
