package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.Article;
import rojerusan.RSMaterialButtonRectangle;
import utils.WindowManager;

public class ArticlesView extends JFrame {
    private JTable articlesTable;
    private JTextField barreRecherche;
    private JButton searchButton, addButton, deleteButton, backButton;

    private RSMaterialButtonRectangle boutonAjouter;
    private RSMaterialButtonRectangle boutonModifier;
    private RSMaterialButtonRectangle boutonRetour;
    private RSMaterialButtonRectangle boutonSupprimer;
    private RSMaterialButtonRectangle boutonRechercher;

    private JLabel jLabel1;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;

    public ArticlesView() {
        setTitle("Gestion des Articles");
        WindowManager.setupWindow(this);

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        barreRecherche = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        articlesTable = new javax.swing.JTable();
        boutonAjouter = new rojerusan.RSMaterialButtonRectangle();
        boutonModifier = new rojerusan.RSMaterialButtonRectangle();
        boutonSupprimer = new rojerusan.RSMaterialButtonRectangle();
        boutonRetour = new rojerusan.RSMaterialButtonRectangle();
        boutonRechercher = new rojerusan.RSMaterialButtonRectangle();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Recherche :");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/views/LogoAichi2.png"))); // NOI18N

        articlesTable.getTableHeader().setFont(new Font("Gill Sans MT", 0, 14));
        articlesTable.getTableHeader().setOpaque(false);
        articlesTable.getTableHeader().setBackground(new Color(56, 182, 255));
        articlesTable.getTableHeader().setForeground(new Color(255,255,255));
        articlesTable.setRowHeight(25);
        articlesTable.setAutoCreateRowSorter(true);
        articlesTable.setFont(new java.awt.Font("Gill Sans MT", 0, 12)); // NOI18N
        articlesTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                },
                new String [] {
                        "ID", "Nom", "Description", "Prix", "Quantité en stock"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        articlesTable.setFocusable(false);
        articlesTable.setRowHeight(25);
        articlesTable.setSelectionBackground(new java.awt.Color(255, 145, 77));
        articlesTable.setShowGrid(false);
        articlesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(articlesTable);

        boutonAjouter.setBackground(new java.awt.Color(56, 182, 255));
        boutonAjouter.setText("Ajouter");

        boutonSupprimer.setBackground(new java.awt.Color(56, 182, 255));
        boutonSupprimer.setText("Supprimer");

        boutonRetour.setBackground(new java.awt.Color(255,145,77));
        boutonRetour.setText("Retour");

        boutonRechercher.setBackground(new java.awt.Color(56, 182, 255));
        boutonRechercher.setText("Rechercher");
        boutonRechercher.setFont(new java.awt.Font("Roboto Medium", 0, 10)); // NOI18N

        boutonModifier.setBackground(new java.awt.Color(255,145,77));
        boutonModifier.setText("Modifier");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(barreRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(boutonRechercher, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(boutonAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(boutonModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(boutonSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(boutonRetour, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 852, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 10, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(barreRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel1)
                                                .addComponent(boutonRechercher, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(boutonAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(boutonModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(boutonSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(boutonRetour, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    public JTable getArticlesTable() {
        return articlesTable;
    }

    public JTextField getSearchField() {
        return barreRecherche;
    }

    public JButton getSearchButton() {
        return boutonRechercher;
    }

    public JButton getAddButton() {
        return boutonAjouter;
    }

    public JButton getEditButton() {
        return boutonModifier;
    }

    public JButton getDeleteButton() {
        return boutonSupprimer;
    }

    public JButton getBackButton() {
        return boutonRetour;
    }

    public void updateTable(List<Article> articles) {
        DefaultTableModel model = (DefaultTableModel) articlesTable.getModel();
        model.setRowCount(0); // Effacer les lignes existantes

        for (Article article : articles) {
            model.addRow(new Object[]{
                    article.getId(),
                    article.getNom(),
                    article.getDescription(),
                    String.format("%.2f €", article.getPrixVente()),
                    article.getQuantiteEnStock()
            });
        }
    }

    public int getSelectedRow() {
        return articlesTable.getSelectedRow();
    }

    public int getArticleIdFromRow(int row) {
        DefaultTableModel tableModel = (DefaultTableModel) articlesTable.getModel();
        return (Integer) tableModel.getValueAt(row, 0);
    }

    public String getArticleNomFromRow(int row) {
        DefaultTableModel tableModel = (DefaultTableModel) articlesTable.getModel();
        return (String) tableModel.getValueAt(row, 2);
    }
}