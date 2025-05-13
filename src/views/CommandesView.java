package views;

import model.Commande;
import utils.WindowManager;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import rojerusan.RSMaterialButtonRectangle;

public class CommandesView extends JFrame {
    private JTable commandesTable;
    private JTable detailsTable;
    private JTextField barreRecherche;
    private RSMaterialButtonRectangle boutonAjouter;
    private RSMaterialButtonRectangle boutonModifier;
    private RSMaterialButtonRectangle boutonRetour;
    private RSMaterialButtonRectangle boutonSupprimer;
    private RSMaterialButtonRectangle boutonRechercher;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel labelDetails;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JSplitPane splitPane;

    public CommandesView() {
        setTitle("Gestion des Commandes");
        WindowManager.setupWindow(this);

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelDetails = new javax.swing.JLabel("Détails de la commande:");
        barreRecherche = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        commandesTable = new javax.swing.JTable();
        detailsTable = new javax.swing.JTable();
        boutonAjouter = new rojerusan.RSMaterialButtonRectangle();
        boutonModifier = new rojerusan.RSMaterialButtonRectangle();
        boutonSupprimer = new rojerusan.RSMaterialButtonRectangle();
        boutonRetour = new rojerusan.RSMaterialButtonRectangle();
        boutonRechercher = new rojerusan.RSMaterialButtonRectangle();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Recherche :");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/views/LogoAichi2.png")));

        commandesTable.getTableHeader().setFont(new Font("Gill Sans MT", 0, 14));
        commandesTable.getTableHeader().setOpaque(false);
        commandesTable.getTableHeader().setBackground(new Color(56, 182, 255));
        commandesTable.getTableHeader().setForeground(new Color(255,255,255));
        commandesTable.setRowHeight(25);
        commandesTable.setAutoCreateRowSorter(true);
        commandesTable.setFont(new java.awt.Font("Gill Sans MT", 0, 12));
        commandesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Numéro", "Client", "Date", "Statut", "Montant HT", "Montant TVA", "Montant TTC", "Utilisateur"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        detailsTable.getTableHeader().setFont(new Font("Gill Sans MT", 0, 14));
        detailsTable.getTableHeader().setOpaque(false);
        detailsTable.getTableHeader().setBackground(new Color(255, 145, 77));
        detailsTable.getTableHeader().setForeground(new Color(255,255,255));
        detailsTable.setRowHeight(25);
        detailsTable.setAutoCreateRowSorter(true);
        detailsTable.setFont(new java.awt.Font("Gill Sans MT", 0, 12));
        detailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID", "Référence", "Désignation", "Quantité", "Prix unitaire HT", "Montant HT", "Montant TVA", "Montant TTC"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        commandesTable.setFocusable(false);
        commandesTable.setShowGrid(false);
        commandesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(commandesTable);
        jScrollPane2.setViewportView(detailsTable);

        boutonAjouter.setBackground(new java.awt.Color(56, 182, 255));
        boutonAjouter.setText("Ajouter");

        boutonSupprimer.setBackground(new java.awt.Color(56, 182, 255));
        boutonSupprimer.setText("Supprimer");

        boutonRetour.setBackground(new java.awt.Color(255,145,77));
        boutonRetour.setText("Retour");

        boutonRechercher.setBackground(new java.awt.Color(56, 182, 255));
        boutonRechercher.setText("Rechercher");
        boutonRechercher.setFont(new java.awt.Font("Roboto Medium", 0, 10));

        boutonModifier.setBackground(new java.awt.Color(255,145,77));
        boutonModifier.setText("Modifier");

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(jScrollPane1);
        
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.add(labelDetails, BorderLayout.NORTH);
        detailsPanel.add(jScrollPane2, BorderLayout.CENTER);
        
        splitPane.setBottomComponent(detailsPanel);
        splitPane.setResizeWeight(0.7);

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
                            .addComponent(splitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 852, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(splitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    public JTable getCommandesTable() {
        return commandesTable;
    }

    public JTable getDetailsTable() {
        return detailsTable;
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

    public void updateTable(List<Commande> commandes) {
        DefaultTableModel model = (DefaultTableModel) commandesTable.getModel();
        model.setRowCount(0);

        for (Commande commande : commandes) {
            String montantHT = String.format("%.2f €", commande.getMontantHT());
            String montantTVA = String.format("%.2f €", commande.getMontantTVA());
            String montantTTC = String.format("%.2f €", commande.getMontantTTC());

            model.addRow(new Object[]{
                commande.getId(),
                commande.getNumero(),
                commande.getNomClient(),
                commande.getDateCommande(),
                commande.getStatut(),
                montantHT,
                montantTVA,
                montantTTC,
                commande.getNomUtilisateur() != null ? commande.getNomUtilisateur() : "-"
            });
        }
    }

    public void updateDetailsTable(List<model.LigneCommande> lignes) {
        DefaultTableModel model = (DefaultTableModel) detailsTable.getModel();
        model.setRowCount(0);

        for (model.LigneCommande ligne : lignes) {
            double montantHT = ligne.getPrixUnitaireHT() * ligne.getQuantite();
            double montantTVA = montantHT * (ligne.getTauxTVA() / 100);
            double montantTTC = montantHT + montantTVA;

            model.addRow(new Object[]{
                ligne.getId(),
                ligne.getReference(),
                ligne.getDesignation(),
                ligne.getQuantite(),
                String.format("%.2f €", ligne.getPrixUnitaireHT()),
                String.format("%.2f €", montantHT),
                String.format("%.2f €", montantTVA),
                String.format("%.2f €", montantTTC)
            });
        }
    }
}