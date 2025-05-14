package views;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import model.Article;
import rojerusan.RSMaterialButtonRectangle;

public class ModifierArticleDialog extends JDialog {
    private Article article;
    private boolean validated = false;
    
    private JTextField referenceField;
    private JTextField nomField;
    private JTextArea descriptionArea;
    private JComboBox<String> categorieCombo;
    private JFormattedTextField prixVenteField;
    private JFormattedTextField prixAchatField;
    private JSpinner quantiteSpinner;
    private JSpinner seuilAlerteSpinner;
    
    private String reference;
    private String nom;
    private String description;
    private String categorie;
    private double prixVente;
    private double prixAchat;
    private int quantite;
    private int seuilAlerte;
    
    public ModifierArticleDialog(JFrame parent, Article article) {
        super(parent, "Modifier l'article", true);
        this.article = article;
        initComponents();
        loadArticleData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Création des composants
        referenceField = new JTextField(20);
        nomField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        String[] categories = {"Informatique", "Électronique", "Bureautique", "Autre"};
        categorieCombo = new JComboBox<>(categories);
        
        // Configuration des champs numériques
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        
        prixVenteField = new JFormattedTextField(format);
        prixAchatField = new JFormattedTextField(format);
        prixVenteField.setColumns(10);
        prixAchatField.setColumns(10);
        
        SpinnerNumberModel quantiteModel = new SpinnerNumberModel(0, 0, 99999, 1);
        quantiteSpinner = new JSpinner(quantiteModel);
        
        SpinnerNumberModel seuilModel = new SpinnerNumberModel(5, 0, 999, 1);
        seuilAlerteSpinner = new JSpinner(seuilModel);
        
        // Ajout des composants avec GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Référence* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(referenceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Nom* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Description :"), gbc);
        gbc.gridx = 1;
        gbc.gridheight = 2;
        mainPanel.add(descScrollPane, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridheight = 1;
        mainPanel.add(new JLabel("Catégorie :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(categorieCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Prix de vente HT* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prixVenteField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Prix d'achat HT :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prixAchatField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Quantité en stock* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(quantiteSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(new JLabel("Seuil d'alerte :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(seuilAlerteSpinner, gbc);
        
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
        validerButton.addActionListener(e -> valider());
        annulerButton.addActionListener(e -> dispose());
        
        // Ajout des panels au dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadArticleData() {
        referenceField.setText(article.getReference());
        nomField.setText(article.getNom());
        descriptionArea.setText(article.getDescription());
        categorieCombo.setSelectedItem(article.getCategorie());
        prixVenteField.setValue(article.getPrixVente());
        prixAchatField.setValue(article.getPrixAchat());
        quantiteSpinner.setValue(article.getQuantiteEnStock());
        seuilAlerteSpinner.setValue(article.getSeuilAlerte());
    }
    
    private void valider() {
        // Validation des champs obligatoires
        if (referenceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La référence est obligatoire.", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire.", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (prixVenteField.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Le prix de vente est obligatoire.", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        validated = true;
        dispose();
    }
    
    public boolean isValidated() {
        return validated;
    }
    
    public String getReference() {
        return referenceField.getText().trim();
    }
    
    public String getNom() {
        return nomField.getText().trim();
    }
    
    public String getDescription() {
        return descriptionArea.getText().trim();
    }
    
    public String getCategorie() {
        return (String) categorieCombo.getSelectedItem();
    }
    
    public double getPrixVente() {
        return ((Number) prixVenteField.getValue()).doubleValue();
    }
    
    public double getPrixAchat() {
        return prixAchatField.getValue() != null ? ((Number) prixAchatField.getValue()).doubleValue() : 0.0;
    }
    
    public int getQuantiteEnStock() {
        return (Integer) quantiteSpinner.getValue();
    }
    
    public int getSeuilAlerte() {
        return (Integer) seuilAlerteSpinner.getValue();
    }
} 