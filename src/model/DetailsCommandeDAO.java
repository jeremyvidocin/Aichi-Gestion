package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailsCommandeDAO {
    private Connection connection;

    public DetailsCommandeDAO() {
        this.connection = ConnexionDAO.getConnexion();
    }

    public List<DetailsCommande> getAllDetailsCommandes() {
        List<DetailsCommande> detailsCommandes = new ArrayList<>();
        String query = "SELECT * FROM detailscommande";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                detailsCommandes.add(new DetailsCommande(
                        rs.getInt("ID"),
                        rs.getInt("ID_Commande"),
                        rs.getInt("ID_Article"),
                        rs.getInt("Quantite"),
                        rs.getDouble("PrixUnitaire")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detailsCommandes;
    }

    public boolean addDetailsCommande(int idCommande, int idArticle, int quantite, double prixUnitaire) {
        String query = "INSERT INTO detailscommande (ID_Commande, ID_Article, Quantite, PrixUnitaire) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCommande);
            stmt.setInt(2, idArticle);
            stmt.setInt(3, quantite);
            stmt.setDouble(4, prixUnitaire);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDetailsCommande(int id, int idCommande, int idArticle, int quantite, double prixUnitaire) {
        String query = "UPDATE detailscommande SET ID_Commande = ?, ID_Article = ?, Quantite = ?, PrixUnitaire = ? WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCommande);
            stmt.setInt(2, idArticle);
            stmt.setInt(3, quantite);
            stmt.setDouble(4, prixUnitaire);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDetailsCommande(int id) {
        String query = "DELETE FROM detailscommande WHERE ID = ?";

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