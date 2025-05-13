package views;

import model.Commande;
import utils.WindowManager;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CommandesView extends JFrame {
    private JTable commandesTable;
    private rojerusan.RSMaterialButtonRectangle boutonAjouter;
    private rojerusan.RSMaterialButtonRectangle boutonModifier;
    private rojerusan.RSMaterialButtonRectangle boutonRetour;
    private rojerusan.RSMaterialButtonRectangle boutonSupprimer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;

    public CommandesView() {
        setTitle("Gestion des Commandes");
        WindowManager.setupWindow(this);

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commandesTable = new javax.swing.JTable();
        boutonAjouter = new rojerusan.RSMaterialButtonRectangle();
        boutonModifier = new rojerusan.RSMaterialButtonRectangle();
        boutonSupprimer = new rojerusan.RSMaterialButtonRectangle();
        boutonRetour = new rojerusan.RSMaterialButtonRectangle();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/views/LogoAichi2.png")));

        commandesTable.getTableHeader().setFont(new Font("Gill Sans MT", 0, 14));
        commandesTable.getTableHeader().setOpaque(false);
        commandesTable.getTableHeader().setBackground(new Color(56, 182, 255));
        commandesTable.getTableHeader().setForeground(new Color(255, 255, 255));
        commandesTable.setRowHeight(25);
        commandesTable.setAutoCreateRowSorter(true);
        commandesTable.setFont(new java.awt.Font("Gill Sans MT", 0, 12));
        commandesTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null}
                },
                new String [] {
                        "Id", "Numero", "Client", "Date", "Statut", "Montant HT", "TVA", "Montant TTC"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        commandesTable.setFocusable(false);
        commandesTable.setRowHeight(25);
        commandesTable.setSelectionBackground(new java.awt.Color(255, 145, 77));
        commandesTable.setShowGrid(false);
        commandesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(commandesTable);

        boutonAjouter.setBackground(new java.awt.Color(56, 182, 255));
        boutonAjouter.setText("Ajouter");

        boutonModifier.setBackground(new java.awt.Color(255, 145, 77));
        boutonModifier.setText("Modifier");

        boutonSupprimer.setBackground(new java.awt.Color(56, 182, 255));
        boutonSupprimer.setText("Supprimer");

        boutonRetour.setBackground(new java.awt.Color(255, 145, 77));
        boutonRetour.setText("Retour");
        boutonRetour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boutonRetourActionPerformed(evt);
            }
        });

        // Layout amélioré
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jScrollPane1)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(boutonAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(boutonModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(boutonSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(boutonRetour, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(20, 20, 20)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    public void updateTable(List<Commande> commandes) {
        DefaultTableModel model = (DefaultTableModel) commandesTable.getModel();
        model.setRowCount(0); // Effacer les lignes existantes

        for (Commande commande : commandes) {
            // Format pour les montants avec 2 décimales
            String montantHT = String.format("%.2f €", commande.getMontantHT());
            String montantTVA = String.format("%.2f €", commande.getMontantTVA());
            String montantTTC = String.format("%.2f €", commande.getMontantTTC());

            model.addRow(new Object[]{
                    commande.getId(),
                    commande.getNumero(),
                    commande.getIdClient(),
                    commande.getDateCommande(),
                    commande.getStatut(),
                    montantHT,
                    montantTVA,
                    montantTTC
            });
        }
    }

    private void boutonRetourActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // Getters pour les boutons
    public JButton getAddButton() {
        return boutonAjouter;
    }

    public JButton getEditButton() {
        return boutonModifier;
    }

    public JButton getDeleteButton() {
        return boutonSupprimer;
    }

    public JTable getCommandesTable() {
        return commandesTable;
    }
}