package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.Article;
import rojerusan.RSMaterialButtonRectangle;
import model.ArticleDAO;

public class SelectArticleDialog extends JDialog {
    private JTable articlesTable;
    private JTextField searchField;
    private Article selectedArticle;
    private boolean validated = false;
    private ArticleDAO articleDAO;

    public SelectArticleDialog(JFrame parent) {
        super(parent, "Sélectionner un article", true);
        this.articleDAO = new ArticleDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher :"));
        searchField = new JTextField(20);
        RSMaterialButtonRectangle searchButton = new RSMaterialButtonRectangle();
        searchButton.setText("Rechercher");
        searchButton.setBackground(new Color(56, 182, 255));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table des articles
        articlesTable = new JTable();
        articlesTable.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID", "Référence", "Nom", "Prix HT", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                Integer.class, String.class, String.class, Double.class, Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        articlesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(articlesTable);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RSMaterialButtonRectangle selectButton = new RSMaterialButtonRectangle();
        selectButton.setText("Sélectionner");
        selectButton.setBackground(new Color(56, 182, 255));
        
        RSMaterialButtonRectangle cancelButton = new RSMaterialButtonRectangle();
        cancelButton.setText("Annuler");
        cancelButton.setBackground(new Color(255, 145, 77));

        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);

        // Listeners
        selectButton.addActionListener(e -> {
            int selectedRow = articlesTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedArticle = getArticleFromRow(selectedRow);
                validated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un article",
                    "Erreur de sélection",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        // Ajouter le listener pour la recherche
        searchButton.addActionListener(e -> searchArticles());
        searchField.addActionListener(e -> searchArticles());  // Pour la recherche avec Enter

        // Layout
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void searchArticles() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            updateTable(articleDAO.getAllArticles());
        } else {
            updateTable(articleDAO.searchArticles(keyword));
        }
    }

    private Article getArticleFromRow(int row) {
        DefaultTableModel model = (DefaultTableModel) articlesTable.getModel();
        return new Article(
            (Integer) model.getValueAt(row, 0),
            (String) model.getValueAt(row, 1),
            (String) model.getValueAt(row, 2),
            (Double) model.getValueAt(row, 3),
            (Integer) model.getValueAt(row, 4)
        );
    }

    public void updateTable(List<Article> articles) {
        DefaultTableModel model = (DefaultTableModel) articlesTable.getModel();
        model.setRowCount(0);

        for (Article article : articles) {
            model.addRow(new Object[]{
                article.getId(),
                article.getReference(),
                article.getNom(),
                article.getPrixVente(),
                article.getQuantiteEnStock()
            });
        }
    }

    public Article getSelectedArticle() {
        return selectedArticle;
    }

    public boolean isValidated() {
        return validated;
    }

    public JTextField getSearchField() {
        return searchField;
    }
} 