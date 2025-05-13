package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.Client;
import utils.WindowManager;

public class ClientsView extends JFrame {
    private JTable clientsTable;
 
    private rojerusan.RSMaterialButtonRectangle boutonAjouter;
    private rojerusan.RSMaterialButtonRectangle boutonModifier;
    private rojerusan.RSMaterialButtonRectangle boutonRetour;
    private rojerusan.RSMaterialButtonRectangle boutonSupprimer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    
    public ClientsView() {
        setTitle("Gestion des Clients");
        WindowManager.setupWindow(this);

        setSize(892, 735);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre à l'écran
        
        
          jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clientsTable = new javax.swing.JTable();
        boutonAjouter = new rojerusan.RSMaterialButtonRectangle();
        boutonSupprimer = new rojerusan.RSMaterialButtonRectangle();
        boutonRetour = new rojerusan.RSMaterialButtonRectangle();
        boutonModifier = new rojerusan.RSMaterialButtonRectangle();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/views/LogoAichi2.png"))); // NOI18N

                  clientsTable.getTableHeader().setFont(new Font("Gill Sans MT", 0, 14));
        clientsTable.getTableHeader().setOpaque(false);
        clientsTable.getTableHeader().setBackground(new Color(56, 182, 255));
        clientsTable.getTableHeader().setForeground(new Color(255,255,255));
        clientsTable.setRowHeight(25);
        clientsTable.setAutoCreateRowSorter(true);
        clientsTable.setFont(new java.awt.Font("Gill Sans MT", 0, 12)); // NOI18N
        clientsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Nom", "Adresse", "Téléphone", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        clientsTable.setFocusable(false);
        clientsTable.setRowHeight(25);
        clientsTable.setSelectionBackground(new java.awt.Color(255, 145, 77));
        clientsTable.setShowGrid(false);
        clientsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(clientsTable);

        boutonAjouter.setBackground(new java.awt.Color(56, 182, 255));
        boutonAjouter.setText("Ajouter");
      

        boutonSupprimer.setBackground(new java.awt.Color(56, 182, 255));
        boutonSupprimer.setText("Supprimer");
      

        boutonRetour.setBackground(new java.awt.Color(255,145,77));
        boutonRetour.setText("Retour");
       

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 20, Short.MAX_VALUE)
                                .addComponent(boutonAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(boutonModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(boutonSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(boutonRetour, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(21, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 852, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boutonAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonRetour, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boutonModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        
        
    }

    // Méthode pour styliser un bouton
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(200, 40)); // Taille uniforme pour les boutons
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public JTable getClientsTable() {
        return clientsTable;
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

    // Méthode pour mettre à jour la table avec les nouvelles données
    public void updateTable(List<Client> clients) {
        DefaultTableModel model = (DefaultTableModel) clientsTable.getModel();
        model.setRowCount(0); // Effacer les lignes existantes

        for (Client client : clients) {
            model.addRow(new Object[]{
                client.getId(),
                client.getNom(),
                client.getAdresse(),
                client.getTelephone(),
                client.getEmail()
            });
        }
    }
}
