package views;

import model.User;
import utils.WindowManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import rojerusan.RSMaterialButtonRectangle;

public class UsersView extends JFrame {
    private JTable usersTable;
    private JTextField searchField;
    private RSMaterialButtonRectangle addButton;
    private RSMaterialButtonRectangle editButton;
    private RSMaterialButtonRectangle deleteButton;
    private RSMaterialButtonRectangle backButton;
    private RSMaterialButtonRectangle searchButton;
    private JLabel titleLabel;
    private JPanel mainPanel;
    private JScrollPane scrollPane;

    public UsersView() {
        setTitle("Gestion des Utilisateurs");
        WindowManager.setupWindow(this);
        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255, 255, 255));
        
        titleLabel = new JLabel("Gestion des Utilisateurs");
        titleLabel.setFont(new Font("Gill Sans MT", Font.BOLD, 24));
        titleLabel.setForeground(new Color(56, 182, 255));
        
        searchField = new JTextField(20);
        searchButton = new RSMaterialButtonRectangle();
        searchButton.setText("Rechercher");
        searchButton.setBackground(new Color(56, 182, 255));
        
        // Configuration de la table
        usersTable = new JTable();
        usersTable.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID", "Nom", "Prénom", "Email", "Téléphone", "Type d'accès"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        
        usersTable.getTableHeader().setFont(new Font("Gill Sans MT", 0, 14));
        usersTable.getTableHeader().setBackground(new Color(56, 182, 255));
        usersTable.getTableHeader().setForeground(Color.WHITE);
        usersTable.setRowHeight(25);
        usersTable.setFont(new Font("Gill Sans MT", 0, 12));
        
        scrollPane = new JScrollPane(usersTable);
        
        // Boutons d'action
        addButton = new RSMaterialButtonRectangle();
        addButton.setText("Ajouter");
        addButton.setBackground(new Color(56, 182, 255));
        
        editButton = new RSMaterialButtonRectangle();
        editButton.setText("Modifier");
        editButton.setBackground(new Color(255, 145, 77));
        
        deleteButton = new RSMaterialButtonRectangle();
        deleteButton.setText("Supprimer");
        deleteButton.setBackground(new Color(56, 182, 255));
        
        backButton = new RSMaterialButtonRectangle();
        backButton.setText("Retour");
        backButton.setBackground(new Color(255, 145, 77));
        
        // Layout
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addGap(10)
                        .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 800, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(editButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(titleLabel)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                    .addComponent(editButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    
    public void updateTable(List<User> users) {
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setRowCount(0);
        
        for (User user : users) {
            model.addRow(new Object[]{
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getTelephone(),
                user.getTypeAcces()
            });
        }
    }
    
    // Getters pour les composants
    public JTable getUsersTable() { return usersTable; }
    public JTextField getSearchField() { return searchField; }
    public JButton getAddButton() { return addButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getBackButton() { return backButton; }
    public JButton getSearchButton() { return searchButton; }
} 