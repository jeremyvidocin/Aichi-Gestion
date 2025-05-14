package controller;

import model.Article;
import model.ArticleDAO;
import model.User;
import views.ArticlesView;
import utils.PermissionManager;
import views.ModifierArticleDialog;
import views.AjouterArticleDialog;
import javax.swing.*;
import java.util.List;
import java.time.LocalDateTime;

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
        articlesView.getSearchButton().addActionListener(e -> searchArticles());
        articlesView.getAddButton().addActionListener(e -> addArticle());
        articlesView.getEditButton().addActionListener(e -> editArticle());
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

    private void searchArticles() {
        String keyword = articlesView.getSearchField().getText().trim();
        List<Article> articles = articleDAO.searchArticles(keyword);
        articlesView.updateTable(articles);
    }

    private void addArticle() {
        if (!PermissionManager.canAddData(currentUser)) {
            JOptionPane.showMessageDialog(articlesView,
                "Vous n'avez pas les permissions nécessaires pour ajouter des articles.",
                "Permission refusée",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        AjouterArticleDialog dialog = new AjouterArticleDialog(articlesView);
        dialog.setVisible(true);

        if (dialog.isValidated()) {
            boolean success = articleDAO.addArticle(
                dialog.getReference(),
                dialog.getNom(),
                dialog.getDescription(),
                dialog.getCategorie(),
                dialog.getPrixVente(),
                dialog.getPrixAchat(),
                dialog.getQuantiteEnStock(),
                dialog.getSeuilAlerte()
            );

            if (success) {
                JOptionPane.showMessageDialog(articlesView,
                    "L'article a été ajouté avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                loadArticles();
            } else {
                JOptionPane.showMessageDialog(articlesView,
                    "Une erreur est survenue lors de l'ajout de l'article.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editArticle() {
        if (!PermissionManager.canModifyData(currentUser)) {
            JOptionPane.showMessageDialog(articlesView,
                "Vous n'avez pas les permissions nécessaires pour modifier des articles.",
                "Permission refusée",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = articlesView.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(articlesView,
                "Veuillez sélectionner un article à modifier.",
                "Sélection requise",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int articleId = articlesView.getArticleIdFromRow(selectedRow);
        Article article = articleDAO.getArticleById(articleId);

        if (article == null) {
            JOptionPane.showMessageDialog(articlesView,
                "L'article sélectionné n'a pas été trouvé.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        ModifierArticleDialog dialog = new ModifierArticleDialog(articlesView, article);
        dialog.setVisible(true);

        if (dialog.isValidated()) {
            boolean success = articleDAO.updateArticle(
                articleId,
                dialog.getReference(),
                dialog.getNom(),
                dialog.getDescription(),
                dialog.getCategorie(),
                dialog.getPrixVente(),
                dialog.getPrixAchat(),
                dialog.getQuantiteEnStock(),
                dialog.getSeuilAlerte(),
                LocalDateTime.now()
            );

            if (success) {
                JOptionPane.showMessageDialog(articlesView,
                    "L'article a été modifié avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                loadArticles();
            } else {
                JOptionPane.showMessageDialog(articlesView,
                    "Une erreur est survenue lors de la modification de l'article.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteArticle() {
        if (!PermissionManager.canDeleteData(currentUser)) {
            JOptionPane.showMessageDialog(articlesView,
                "Vous n'avez pas les permissions nécessaires pour supprimer des articles.",
                "Permission refusée",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = articlesView.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(articlesView,
                "Veuillez sélectionner un article à supprimer.",
                "Sélection requise",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int articleId = articlesView.getArticleIdFromRow(selectedRow);
        String articleNom = articlesView.getArticleNomFromRow(selectedRow);

        int confirmation = JOptionPane.showConfirmDialog(articlesView,
            "Êtes-vous sûr de vouloir supprimer l'article \"" + articleNom + "\" ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = articleDAO.deleteArticle(articleId);

            if (success) {
                JOptionPane.showMessageDialog(articlesView,
                    "L'article a été supprimé avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                loadArticles();
            } else {
                JOptionPane.showMessageDialog(articlesView,
                    "Une erreur est survenue lors de la suppression de l'article.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void backMenu() {
        articlesView.dispose();
    }
}
