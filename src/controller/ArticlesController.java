package controller;

import model.Article;
import model.ArticleDAO;
import model.User;
import views.ArticlesView;
import utils.PermissionManager;

import javax.swing.*;
import java.util.List;

public class ArticlesController {
    private ArticlesView articlesView;
    private ArticleDAO articleDAO;
    private User currentUser;

    public ArticlesController(User user) {
        this.currentUser = user;
        this.articlesView = new ArticlesView();
        this.articleDAO = new ArticleDAO();

        loadArticles();

        // Ajout des listeners pour les boutons
        articlesView.getSearchButton().addActionListener(e -> searchArticle());
        articlesView.getEditButton().addActionListener(e -> editArticle());
        articlesView.getAddButton().addActionListener(e -> addArticle());
        articlesView.getDeleteButton().addActionListener(e -> deleteArticle());
        articlesView.getBackButton().addActionListener(e -> backMenu());

        // Désactiver les boutons si l'utilisateur n'a pas les permissions
        if (!PermissionManager.canModifyData(currentUser)) {
            articlesView.getEditButton().setEnabled(false);
            articlesView.getAddButton().setEnabled(false);
            articlesView.getDeleteButton().setEnabled(false);
        }
    }

    public void showArticlesView() {
        articlesView.setVisible(true);
    }

    private void loadArticles() {
        List<Article> articles = articleDAO.getAllArticles();
        articlesView.updateTable(articles);
    }

    private void searchArticle() {
        String keyword = articlesView.getSearchField().getText();
        List<Article> articles = articleDAO.searchArticlesByName(keyword);
        articlesView.updateTable(articles);
    }

    private void addArticle() {
        if (!PermissionManager.canAddData(currentUser)) {
            JOptionPane.showMessageDialog(articlesView, "Vous n'avez pas les permissions nécessaires pour ajouter des articles.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nom = JOptionPane.showInputDialog("Nom de l'article :");
        String description = JOptionPane.showInputDialog("Description de l'article :");
        double prix = Double.parseDouble(JOptionPane.showInputDialog("Prix de l'article :"));
        int quantite = Integer.parseInt(JOptionPane.showInputDialog("Quantité en stock :"));

        if (articleDAO.addArticle(nom, description, prix, quantite)) {
            JOptionPane.showMessageDialog(articlesView, "Article ajouté !");
            loadArticles();
        } else {
            JOptionPane.showMessageDialog(articlesView, "Erreur lors de l'ajout.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void editArticle() {
        if (!PermissionManager.canModifyData(currentUser)) {
            JOptionPane.showMessageDialog(articlesView, "Vous n'avez pas les permissions nécessaires pour modifier des articles.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = articlesView.getArticlesTable().getSelectedRow();
        if (row != -1) {
            int id = (int) articlesView.getArticlesTable().getValueAt(row, 0);
            String newName = JOptionPane.showInputDialog("Nouveau nom de l'article :");
            String newDescription = JOptionPane.showInputDialog("Nouvelle description de l'article :");
            String newPrix = JOptionPane.showInputDialog("Nouveau prix de l'article :");
            String newQuantite = JOptionPane.showInputDialog("Nouvelle quantité de l'article:");

            if (articleDAO.updateArticle(id, newName, newDescription, newPrix, newQuantite)) {
                JOptionPane.showMessageDialog(articlesView, "Article modifié !");
                loadArticles();
            } else {
                JOptionPane.showMessageDialog(articlesView, "Erreur lors de la modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(articlesView, "Veuillez sélectionner un Article.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteArticle() {
        if (!PermissionManager.canDeleteData(currentUser)) {
            JOptionPane.showMessageDialog(articlesView, "Vous n'avez pas les permissions nécessaires pour supprimer des articles.", "Permission refusée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = articlesView.getArticlesTable().getSelectedRow();
        if (row != -1) {
            int id = (int) articlesView.getArticlesTable().getValueAt(row, 0);
            if (articleDAO.deleteArticle(id)) {
                JOptionPane.showMessageDialog(articlesView, "Article supprimé !");
                loadArticles();
            } else {
                JOptionPane.showMessageDialog(articlesView, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(articlesView, "Veuillez sélectionner un article.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void backMenu() {
        articlesView.dispose();
    }
}
