package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ArticleDAO {
    private Connection connection;

    public ArticleDAO() {
        this.connection = ConnexionDAO.getConnexion();
    }

    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM articles ORDER BY Nom";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                articles.add(new Article(
                    rs.getInt("ID"),
                    rs.getString("Reference"),
                    rs.getString("Nom"),
                    rs.getString("Description"),
                    rs.getString("Categorie"),
                    rs.getDouble("PrixVente"),
                    rs.getDouble("PrixAchat"),
                    rs.getInt("QuantiteEnStock"),
                    rs.getInt("SeuilAlerte"),
                    rs.getTimestamp("DateCreation") != null ? rs.getTimestamp("DateCreation").toLocalDateTime() : null,
                    rs.getTimestamp("DateModification") != null ? rs.getTimestamp("DateModification").toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public List<Article> searchArticles(String keyword) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM articles WHERE Nom LIKE ? OR Reference LIKE ? OR Description LIKE ? ORDER BY Nom";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                articles.add(new Article(
                    rs.getInt("ID"),
                    rs.getString("Reference"),
                    rs.getString("Nom"),
                    rs.getString("Description"),
                    rs.getString("Categorie"),
                    rs.getDouble("PrixVente"),
                    rs.getDouble("PrixAchat"),
                    rs.getInt("QuantiteEnStock"),
                    rs.getInt("SeuilAlerte"),
                    rs.getTimestamp("DateCreation") != null ? rs.getTimestamp("DateCreation").toLocalDateTime() : null,
                    rs.getTimestamp("DateModification") != null ? rs.getTimestamp("DateModification").toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public boolean addArticle(String reference, String nom, String description, String categorie,
                              double prixVente, double prixAchat, int quantiteEnStock, int seuilAlerte) {
        String query = "INSERT INTO articles (Reference, Nom, Description, Categorie, PrixVente, " +
                "PrixAchat, QuantiteEnStock, SeuilAlerte, DateCreation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reference);
            stmt.setString(2, nom);
            stmt.setString(3, description);
            stmt.setString(4, categorie);
            stmt.setDouble(5, prixVente);
            stmt.setDouble(6, prixAchat);
            stmt.setInt(7, quantiteEnStock);
            stmt.setInt(8, seuilAlerte);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateArticle(int id, String reference, String nom, String description,
                               String categorie, double prixVente, double prixAchat,
                               int quantiteEnStock, int seuilAlerte, LocalDateTime dateModification) {
        String query = "UPDATE articles SET Reference = ?, Nom = ?, Description = ?, " +
                      "Categorie = ?, PrixVente = ?, PrixAchat = ?, QuantiteEnStock = ?, " +
                      "SeuilAlerte = ?, DateModification = ? WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reference);
            stmt.setString(2, nom);
            stmt.setString(3, description);
            stmt.setString(4, categorie);
            stmt.setDouble(5, prixVente);
            stmt.setDouble(6, prixAchat);
            stmt.setInt(7, quantiteEnStock);
            stmt.setInt(8, seuilAlerte);
            stmt.setTimestamp(9, java.sql.Timestamp.valueOf(dateModification));
            stmt.setInt(10, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateArticlePartiel(int id, String nom, String description, Double prix, Integer quantite) {
        StringBuilder query = new StringBuilder("UPDATE articles SET DateModification = CURRENT_TIMESTAMP");
        List<Object> params = new ArrayList<>();

        if (nom != null) {
            query.append(", Nom = ?");
            params.add(nom);
        }
        if (description != null) {
            query.append(", Description = ?");
            params.add(description);
        }
        if (prix != null) {
            query.append(", PrixVente = ?");
            params.add(prix);
        }
        if (quantite != null) {
            query.append(", QuantiteEnStock = ?");
            params.add(quantite);
        }

        query.append(" WHERE ID = ?");
        params.add(id);

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Article getArticleById(int id) {
        String query = "SELECT * FROM articles WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Article(
                    rs.getInt("ID"),
                    rs.getString("Reference"),
                    rs.getString("Nom"),
                    rs.getString("Description"),
                    rs.getString("Categorie"),
                    rs.getDouble("PrixVente"),
                    rs.getDouble("PrixAchat"),
                    rs.getInt("QuantiteEnStock"),
                    rs.getInt("SeuilAlerte"),
                    rs.getTimestamp("DateCreation") != null ? rs.getTimestamp("DateCreation").toLocalDateTime() : null,
                    rs.getTimestamp("DateModification") != null ? rs.getTimestamp("DateModification").toLocalDateTime() : null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteArticle(int id) {
        String query = "DELETE FROM articles WHERE Id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Pour compatibilité avec le code existant
    public boolean addArticle(String nom, String description, double prix, int quantiteEnStock) {
        // Générer une référence automatique
        String reference = generateReference(nom);
        return addArticle(reference, nom, description, "Non catégorisé", prix, 0, quantiteEnStock, 5);
    }

    private String generateReference(String nom) {
        // Créer une référence simple basée sur le nom et un timestamp
        String prefix = "ART";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return prefix + timestamp;
    }
}
