package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.Article;
import rojerusan.RSMaterialButtonRectangle;

public class SelectionArticleDialog extends JDialog {
    private JTable articlesTable;
    private DefaultTableModel tableModel;
    private JSpinner quantiteSpinner;
    private JTextField searchField;
    private Article selectedArticle = null;
    private boolean validated = false;
    
    public SelectionArticleDialog(JFrame parent, List<Article> articles) {
        super(parent, "Sélection d'article", true);
        initComponents();
        updateTable(articles);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        searchField = new JTextField(20);
        RSMaterialButtonRectangle searchButton = new RSMaterialButtonRectangle();
        searchButton.setText("Rechercher");
        searchButton.setBackground(new Color(56, 182, 255));
        
        searchPanel.add(new JLabel("Rechercher :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Création de la table
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Référence", "Désignation", "Prix HT", "Stock", "Seuil d'alerte"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;
                    case 3: return Double.class;
                    case 4:
                    case 5: return Integer.class;
                    default: return String.class;
                }
            }
        };
        
        articlesTable = new JTable(tableModel);
        articlesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        articlesTable.getColumnModel().getColumn(0).setMinWidth(0);
        articlesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        articlesTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Personnalisation de l'apparence de la table
        articlesTable.setRowHeight(25);
        articlesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        articlesTable.getTableHeader().setBackground(new Color(56, 182, 255));
        articlesTable.getTableHeader().setForeground(Color.WHITE);
        articlesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        articlesTable.setShowGrid(true);
        articlesTable.setGridColor(new Color(234, 234, 234));
        
        // Ajustement des colonnes
        articlesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Référence
        articlesTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Désignation
        articlesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Prix
        articlesTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Stock
        articlesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Seuil
        
        JScrollPane scrollPane = new JScrollPane(articlesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Panel pour la quantité
        JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        quantitePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 9999, 1);
        quantiteSpinner = new JSpinner(spinnerModel);
        ((JSpinner.DefaultEditor) quantiteSpinner.getEditor()).getTextField().setColumns(5);
        
        quantitePanel.add(new JLabel("Quantité :"));
        quantitePanel.add(quantiteSpinner);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        RSMaterialButtonRectangle validerButton = new RSMaterialButtonRectangle();
        validerButton.setText("Valider");
        validerButton.setBackground(new Color(56, 182, 255));
        
        RSMaterialButtonRectangle annulerButton = new RSMaterialButtonRectangle();
        annulerButton.setText("Annuler");
        annulerButton.setBackground(new Color(255, 59, 59));
        
        buttonPanel.add(validerButton);
        buttonPanel.add(annulerButton);
        
        // Ajout des listeners
        searchButton.addActionListener(e -> fireSearchEvent());
        validerButton.addActionListener(e -> valider());
        annulerButton.addActionListener(e -> dispose());
        
        articlesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = articlesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int stock = (Integer) tableModel.getValueAt(selectedRow, 4);
                    ((SpinnerNumberModel) quantiteSpinner.getModel()).setMaximum(stock);
                }
            }
        });
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(quantitePanel, BorderLayout.SOUTH);
        
        // Assemblage final
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void fireSearchEvent() {
        // À implémenter dans le contrôleur
        String searchText = searchField.getText().trim();
        // Notifier le contrôleur pour effectuer la recherche
    }
    
    public void updateTable(List<Article> articles) {
        tableModel.setRowCount(0);
        for (Article article : articles) {
            tableModel.addRow(new Object[]{
                article.getId(),
                article.getReference(),
                article.getNom(),
                article.getPrixVente(),
                article.getQuantiteEnStock(),
                article.getSeuilAlerte()
            });
        }
    }
    
    private void valider() {
        int selectedRow = articlesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un article.",
                "Sélection requise",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantite = (Integer) quantiteSpinner.getValue();
        int stock = (Integer) tableModel.getValueAt(selectedRow, 4);
        
        if (quantite > stock) {
            JOptionPane.showMessageDialog(this,
                "La quantité demandée n'est pas disponible en stock.",
                "Stock insuffisant",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        validated = true;
        dispose();
    }
    
    public boolean isValidated() {
        return validated;
    }
    
    public Article getSelectedArticle() {
        int selectedRow = articlesTable.getSelectedRow();
        if (selectedRow != -1) {
            return new Article(
                (Integer) tableModel.getValueAt(selectedRow, 0),
                (String) tableModel.getValueAt(selectedRow, 1),
                (String) tableModel.getValueAt(selectedRow, 2),
                "",  // Description
                "",  // Catégorie
                (Double) tableModel.getValueAt(selectedRow, 3),
                0.0, // Prix d'achat
                (Integer) tableModel.getValueAt(selectedRow, 4),
                (Integer) tableModel.getValueAt(selectedRow, 5),
                null, // Date création
                null  // Date modification
            );
        }
        return null;
    }
    
    public int getQuantiteSelectionnee() {
        return (Integer) quantiteSpinner.getValue();
    }
    
    public String getSearchText() {
        return searchField.getText().trim();
    }
} 