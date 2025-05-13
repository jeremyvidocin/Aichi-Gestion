package views;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Client;
import model.Article;
import model.ArticleDAO;
import rojerusan.RSMaterialButtonRectangle;

public class AddEditCommandeDialog extends JDialog {
    private boolean isNewCommande;
    private JComboBox<Client> clientComboBox;
    private JTextArea commentaireArea;
    private DefaultListModel<Article> articlesListModel;
    private JList<Article> articlesList;
    private JSpinner quantiteSpinner;
    private boolean validated = false;

    public AddEditCommandeDialog(JFrame parent, String title, boolean isNew) {
        super(parent, title, true);
        this.isNewCommande = isNew;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel pour les informations de base
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        infoPanel.add(new JLabel("Client :"));
        clientComboBox = new JComboBox<>();
        infoPanel.add(clientComboBox);
        infoPanel.add(new JLabel("Commentaire :"));
        commentaireArea = new JTextArea(3, 20);
        commentaireArea.setLineWrap(true);
        infoPanel.add(new JScrollPane(commentaireArea));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel pour les articles
        JPanel articlesPanel = new JPanel(new BorderLayout(5, 5));
        articlesPanel.setBorder(BorderFactory.createTitledBorder("Articles"));
        
        articlesListModel = new DefaultListModel<>();
        articlesList = new JList<>(articlesListModel);
        articlesList.setCellRenderer(new ArticleListCellRenderer());
        articlesPanel.add(new JScrollPane(articlesList), BorderLayout.CENTER);

        // Panel pour ajouter des articles
        JPanel addArticlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addArticleButton = new RSMaterialButtonRectangle();
        addArticleButton.setText("Ajouter un article");
        addArticleButton.setBackground(new Color(56, 182, 255));
        quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        addArticlePanel.add(addArticleButton);
        addArticlePanel.add(new JLabel("Quantité :"));
        addArticlePanel.add(quantiteSpinner);
        articlesPanel.add(addArticlePanel, BorderLayout.SOUTH);

        mainPanel.add(articlesPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RSMaterialButtonRectangle saveButton = new RSMaterialButtonRectangle();
        saveButton.setText("Enregistrer");
        saveButton.setBackground(new Color(56, 182, 255));
        
        RSMaterialButtonRectangle cancelButton = new RSMaterialButtonRectangle();
        cancelButton.setText("Annuler");
        cancelButton.setBackground(new Color(255, 145, 77));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Listeners
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                validated = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        addArticleButton.addActionListener(e -> showAddArticleDialog());

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateForm() {
        if (clientComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un client",
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (articlesListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez ajouter au moins un article",
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void showAddArticleDialog() {
        SelectArticleDialog dialog = new SelectArticleDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        
        // Charger la liste des articles
        ArticleDAO articleDAO = new ArticleDAO();
        dialog.updateTable(articleDAO.getAllArticles());
        
        dialog.setVisible(true);

        if (dialog.isValidated()) {
            Article selectedArticle = dialog.getSelectedArticle();
            selectedArticle.setQuantiteSelectionnee((Integer) quantiteSpinner.getValue());
            articlesListModel.addElement(selectedArticle);
        }
    }

    public void setClients(List<Client> clients) {
        clientComboBox.removeAllItems();
        for (Client client : clients) {
            clientComboBox.addItem(client);
        }
    }

    public Client getSelectedClient() {
        return (Client) clientComboBox.getSelectedItem();
    }

    public String getCommentaire() {
        return commentaireArea.getText();
    }

    public List<Article> getSelectedArticles() {
        List<Article> articles = new java.util.ArrayList<>();
        for (int i = 0; i < articlesListModel.size(); i++) {
            articles.add(articlesListModel.getElementAt(i));
        }
        return articles;
    }

    public boolean isValidated() {
        return validated;
    }

    // Méthode pour formater l'affichage des articles dans la JList
    private class ArticleListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Article) {
                Article article = (Article) value;
                setText(String.format("%s - %s (Qté: %d) - %.2f €",
                    article.getReference(),
                    article.getNom(),
                    article.getQuantiteSelectionnee(),
                    article.getPrixVente()));
            }
            return this;
        }
    }
} 