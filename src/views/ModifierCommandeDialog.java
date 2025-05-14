package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.Commande;
import model.LigneCommande;
import model.Client;
import model.Article;
import model.ArticleDAO;
import rojerusan.RSMaterialButtonRectangle;

public class ModifierCommandeDialog extends JDialog {
    private Commande commande;
    private List<LigneCommande> lignesCommande;
    private boolean validated = false;
    
    private JTextField numeroField;
    private JTextField clientField;
    private JTextField statutField;
    private JTextField commentaireField;
    private JTextField adresseLivraisonField;
    private JTable lignesTable;
    private JLabel totalHTLabel;
    private JLabel totalTVALabel;
    private JLabel totalTTCLabel;
    
    private JCheckBox modifierClient;
    private JCheckBox modifierStatut;
    private JCheckBox modifierCommentaire;
    private JCheckBox modifierAdresseLivraison;
    
    private DefaultTableModel lignesModel;
    private Client selectedClient;
    
    public ModifierCommandeDialog(JFrame parent, Commande commande, List<LigneCommande> lignes) {
        super(parent, "Modifier la commande", true);
        this.commande = commande;
        this.lignesCommande = lignes;
        initComponents();
        loadCommandeData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec GridBagLayout pour les informations générales
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Création des composants
        numeroField = new JTextField(20);
        numeroField.setEnabled(false); // Le numéro n'est pas modifiable
        clientField = new JTextField(20);
        statutField = new JTextField(20);
        commentaireField = new JTextField(20);
        adresseLivraisonField = new JTextField(20);
        
        modifierClient = new JCheckBox("Modifier");
        modifierStatut = new JCheckBox("Modifier");
        modifierCommentaire = new JCheckBox("Modifier");
        modifierAdresseLivraison = new JCheckBox("Modifier");
        
        // Désactiver les champs par défaut
        clientField.setEnabled(false);
        statutField.setEnabled(false);
        commentaireField.setEnabled(false);
        adresseLivraisonField.setEnabled(false);
        
        // Ajout des listeners
        modifierClient.addActionListener(e -> {
            clientField.setEnabled(modifierClient.isSelected());
            if (modifierClient.isSelected()) {
                selectClient();
            }
        });
        modifierStatut.addActionListener(e -> statutField.setEnabled(modifierStatut.isSelected()));
        modifierCommentaire.addActionListener(e -> commentaireField.setEnabled(modifierCommentaire.isSelected()));
        modifierAdresseLivraison.addActionListener(e -> adresseLivraisonField.setEnabled(modifierAdresseLivraison.isSelected()));
        
        // Ajout des composants d'information générale
        addField(mainPanel, gbc, "Numéro :", numeroField, null, 0);
        addField(mainPanel, gbc, "Client :", clientField, modifierClient, 1);
        addField(mainPanel, gbc, "Statut :", statutField, modifierStatut, 2);
        addField(mainPanel, gbc, "Commentaire :", commentaireField, modifierCommentaire, 3);
        addField(mainPanel, gbc, "Adresse de livraison :", adresseLivraisonField, modifierAdresseLivraison, 4);
        
        // Panel pour les lignes de commande
        JPanel lignesPanel = new JPanel(new BorderLayout());
        lignesPanel.setBorder(BorderFactory.createTitledBorder("Articles de la commande"));
        
        // Création du tableau des lignes
        lignesModel = new DefaultTableModel(
            new Object[]{"ID", "Référence", "Désignation", "Quantité", "Prix unitaire HT", "TVA", "Total HT", "Total TTC"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Seule la quantité est modifiable
            }
        };
        lignesTable = new JTable(lignesModel);
        lignesTable.getColumnModel().getColumn(0).setMinWidth(0);
        lignesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        
        // Boutons pour gérer les lignes
        JPanel lignesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        RSMaterialButtonRectangle ajouterLigneButton = new RSMaterialButtonRectangle();
        ajouterLigneButton.setText("Ajouter article");
        ajouterLigneButton.setBackground(new Color(56, 182, 255));
        ajouterLigneButton.addActionListener(e -> ajouterLigne());
        
        RSMaterialButtonRectangle supprimerLigneButton = new RSMaterialButtonRectangle();
        supprimerLigneButton.setText("Supprimer article");
        supprimerLigneButton.setBackground(new Color(255, 145, 77));
        supprimerLigneButton.addActionListener(e -> supprimerLigne());
        
        lignesButtonPanel.add(ajouterLigneButton);
        lignesButtonPanel.add(supprimerLigneButton);
        
        lignesPanel.add(new JScrollPane(lignesTable), BorderLayout.CENTER);
        lignesPanel.add(lignesButtonPanel, BorderLayout.NORTH);
        
        // Panel pour les totaux
        JPanel totauxPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        totauxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        totalHTLabel = new JLabel("Total HT: 0.00 €");
        totalTVALabel = new JLabel("Total TVA: 0.00 €");
        totalTTCLabel = new JLabel("Total TTC: 0.00 €");
        
        totauxPanel.add(totalHTLabel);
        totauxPanel.add(totalTVALabel);
        totauxPanel.add(totalTTCLabel);
        
        lignesPanel.add(totauxPanel, BorderLayout.SOUTH);
        
        // Panel pour les boutons de validation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        RSMaterialButtonRectangle validerButton = new RSMaterialButtonRectangle();
        validerButton.setText("Valider");
        validerButton.setBackground(new Color(56, 182, 255));
        validerButton.addActionListener(e -> validerModification());
        
        RSMaterialButtonRectangle annulerButton = new RSMaterialButtonRectangle();
        annulerButton.setText("Annuler");
        annulerButton.setBackground(new Color(255, 145, 77));
        annulerButton.addActionListener(e -> dispose());
        
        buttonPanel.add(validerButton);
        buttonPanel.add(annulerButton);
        
        // Assemblage final
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(mainPanel, BorderLayout.NORTH);
        topPanel.add(lignesPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getOwner());
        setSize(800, 600);
    }
    
    private void addField(JPanel panel, GridBagConstraints gbc, String label, 
                         JTextField field, JCheckBox checkBox, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
        
        if (checkBox != null) {
            gbc.gridx = 2;
            panel.add(checkBox, gbc);
        }
    }
    
    private void loadCommandeData() {
        numeroField.setText(commande.getNumero());
        clientField.setText(commande.getNomClient());
        statutField.setText(commande.getStatut());
        commentaireField.setText(commande.getCommentaire());
        adresseLivraisonField.setText(commande.getAdresseLivraison());
        
        // Charger les lignes de commande
        updateLignesTable();
        updateTotaux();
    }
    
    private void updateLignesTable() {
        lignesModel.setRowCount(0);
        for (LigneCommande ligne : lignesCommande) {
            lignesModel.addRow(new Object[]{
                ligne.getId(),
                ligne.getReference(),
                ligne.getDesignation(),
                ligne.getQuantite(),
                String.format("%.2f", ligne.getPrixUnitaireHT()),
                String.format("%.2f", ligne.getTauxTVA()),
                String.format("%.2f", ligne.getMontantHT()),
                String.format("%.2f", ligne.getMontantTTC())
            });
        }
    }
    
    private void updateTotaux() {
        double totalHT = 0;
        double totalTVA = 0;
        double totalTTC = 0;
        
        for (LigneCommande ligne : lignesCommande) {
            totalHT += ligne.getMontantHT();
            totalTVA += ligne.getMontantTVA();
            totalTTC += ligne.getMontantTTC();
        }
        
        totalHTLabel.setText(String.format("Total HT: %.2f €", totalHT));
        totalTVALabel.setText(String.format("Total TVA: %.2f €", totalTVA));
        totalTTCLabel.setText(String.format("Total TTC: %.2f €", totalTTC));
    }
    
    private void selectClient() {
        // Cette méthode sera implémentée pour ouvrir une boîte de dialogue de sélection de client
        // Pour l'instant, on utilise un simple JOptionPane
        String clientId = JOptionPane.showInputDialog("ID du nouveau client :");
        if (clientId != null && !clientId.isEmpty()) {
            try {
                int id = Integer.parseInt(clientId);
                // TODO: Récupérer le client et mettre à jour le champ
                clientField.setText("Client " + id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID client invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void ajouterLigne() {
        ArticleDAO articleDAO = new ArticleDAO();
        List<Article> articles = articleDAO.getAllArticles();
        
        SelectionArticleDialog dialog = new SelectionArticleDialog((JFrame)getOwner(), articles);
        dialog.setVisible(true);
        
        if (dialog.isValidated()) {
            Article article = dialog.getSelectedArticle();
            int quantite = dialog.getQuantiteSelectionnee();
            
            // Créer une nouvelle ligne de commande
            LigneCommande ligne = new LigneCommande(
                0, // ID temporaire
                commande.getId(),
                article.getId(),
                article.getReference(),
                article.getNom(),
                quantite,
                article.getPrixVente(),
                20.0 // TVA fixe pour l'instant
            );
            
            // Ajouter la ligne à la liste
            lignesCommande.add(ligne);
            
            // Mettre à jour l'affichage
            updateLignesTable();
            updateTotaux();
        }
    }
    
    private void supprimerLigne() {
        int selectedRow = lignesTable.getSelectedRow();
        if (selectedRow != -1) {
            // Supprimer la ligne de la liste
            lignesCommande.remove(selectedRow);
            
            // Mettre à jour l'affichage
            updateLignesTable();
            updateTotaux();
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne à supprimer", 
                                        "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void validerModification() {
        try {
            // Validation des données
            if (modifierClient.isSelected() && selectedClient == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client valide", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (modifierStatut.isSelected() && statutField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le statut ne peut pas être vide", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            validated = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la validation : " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isValidated() {
        return validated;
    }
    
    public Integer getNewClientId() {
        return modifierClient.isSelected() && selectedClient != null ? selectedClient.getId() : null;
    }
    
    public String getNewStatut() {
        return modifierStatut.isSelected() ? statutField.getText() : null;
    }
    
    public String getNewCommentaire() {
        return modifierCommentaire.isSelected() ? commentaireField.getText() : null;
    }
    
    public String getNewAdresseLivraison() {
        return modifierAdresseLivraison.isSelected() ? adresseLivraisonField.getText() : null;
    }
    
    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }
} 