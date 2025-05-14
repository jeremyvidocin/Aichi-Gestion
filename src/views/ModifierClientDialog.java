package views;

import javax.swing.*;
import java.awt.*;
import model.Client;
import rojerusan.RSMaterialButtonRectangle;

public class ModifierClientDialog extends JDialog {
    private Client client;
    private boolean validated = false;
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField societeField;
    private JTextField adresseField;
    private JTextField codePostalField;
    private JTextField villeField;
    private JTextField paysField;
    private JTextField telephoneField;
    private JTextField emailField;
    
    private JCheckBox modifierNom;
    private JCheckBox modifierPrenom;
    private JCheckBox modifierSociete;
    private JCheckBox modifierAdresse;
    private JCheckBox modifierCodePostal;
    private JCheckBox modifierVille;
    private JCheckBox modifierPays;
    private JCheckBox modifierTelephone;
    private JCheckBox modifierEmail;
    
    public ModifierClientDialog(JFrame parent, Client client) {
        super(parent, "Modifier le client", true);
        this.client = client;
        initComponents();
        loadClientData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Création des composants
        modifierNom = new JCheckBox("Modifier");
        modifierPrenom = new JCheckBox("Modifier");
        modifierSociete = new JCheckBox("Modifier");
        modifierAdresse = new JCheckBox("Modifier");
        modifierCodePostal = new JCheckBox("Modifier");
        modifierVille = new JCheckBox("Modifier");
        modifierPays = new JCheckBox("Modifier");
        modifierTelephone = new JCheckBox("Modifier");
        modifierEmail = new JCheckBox("Modifier");
        
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        societeField = new JTextField(20);
        adresseField = new JTextField(20);
        codePostalField = new JTextField(20);
        villeField = new JTextField(20);
        paysField = new JTextField(20);
        telephoneField = new JTextField(20);
        emailField = new JTextField(20);
        
        // Désactiver les champs par défaut
        nomField.setEnabled(false);
        prenomField.setEnabled(false);
        societeField.setEnabled(false);
        adresseField.setEnabled(false);
        codePostalField.setEnabled(false);
        villeField.setEnabled(false);
        paysField.setEnabled(false);
        telephoneField.setEnabled(false);
        emailField.setEnabled(false);
        
        // Ajout des listeners pour activer/désactiver les champs
        modifierNom.addActionListener(e -> nomField.setEnabled(modifierNom.isSelected()));
        modifierPrenom.addActionListener(e -> prenomField.setEnabled(modifierPrenom.isSelected()));
        modifierSociete.addActionListener(e -> societeField.setEnabled(modifierSociete.isSelected()));
        modifierAdresse.addActionListener(e -> adresseField.setEnabled(modifierAdresse.isSelected()));
        modifierCodePostal.addActionListener(e -> codePostalField.setEnabled(modifierCodePostal.isSelected()));
        modifierVille.addActionListener(e -> villeField.setEnabled(modifierVille.isSelected()));
        modifierPays.addActionListener(e -> paysField.setEnabled(modifierPays.isSelected()));
        modifierTelephone.addActionListener(e -> telephoneField.setEnabled(modifierTelephone.isSelected()));
        modifierEmail.addActionListener(e -> emailField.setEnabled(modifierEmail.isSelected()));
        
        // Ajout des composants avec GridBagLayout
        addField(mainPanel, gbc, "Nom :", nomField, modifierNom, 0);
        addField(mainPanel, gbc, "Prénom :", prenomField, modifierPrenom, 1);
        addField(mainPanel, gbc, "Société :", societeField, modifierSociete, 2);
        addField(mainPanel, gbc, "Adresse :", adresseField, modifierAdresse, 3);
        addField(mainPanel, gbc, "Code Postal :", codePostalField, modifierCodePostal, 4);
        addField(mainPanel, gbc, "Ville :", villeField, modifierVille, 5);
        addField(mainPanel, gbc, "Pays :", paysField, modifierPays, 6);
        addField(mainPanel, gbc, "Téléphone :", telephoneField, modifierTelephone, 7);
        addField(mainPanel, gbc, "Email :", emailField, modifierEmail, 8);
        
        // Panel pour les boutons
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
        
        // Ajout des panels au dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getOwner());
    }
    
    private void addField(JPanel panel, GridBagConstraints gbc, String label, 
                         JTextField field, JCheckBox checkBox, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
        
        gbc.gridx = 2;
        panel.add(checkBox, gbc);
    }
    
    private void loadClientData() {
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        societeField.setText(client.getSociete());
        adresseField.setText(client.getAdresse());
        codePostalField.setText(client.getCodePostal());
        villeField.setText(client.getVille());
        paysField.setText(client.getPays());
        telephoneField.setText(client.getTelephone());
        emailField.setText(client.getEmail());
    }
    
    private void validerModification() {
        try {
            // Validation de l'email si modifié
            if (modifierEmail.isSelected() && !emailField.getText().isEmpty()) {
                if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    JOptionPane.showMessageDialog(this, "L'adresse email n'est pas valide", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Validation du code postal si modifié
            if (modifierCodePostal.isSelected() && !codePostalField.getText().isEmpty()) {
                if (!codePostalField.getText().matches("\\d{5}")) {
                    JOptionPane.showMessageDialog(this, "Le code postal doit contenir 5 chiffres", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Validation du téléphone si modifié
            if (modifierTelephone.isSelected() && !telephoneField.getText().isEmpty()) {
                if (!telephoneField.getText().matches("^\\d{10}$")) {
                    JOptionPane.showMessageDialog(this, "Le numéro de téléphone doit contenir 10 chiffres", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
    
    public String getNom() {
        return modifierNom.isSelected() ? nomField.getText() : null;
    }
    
    public String getPrenom() {
        return modifierPrenom.isSelected() ? prenomField.getText() : null;
    }
    
    public String getSociete() {
        return modifierSociete.isSelected() ? societeField.getText() : null;
    }
    
    public String getAdresse() {
        return modifierAdresse.isSelected() ? adresseField.getText() : null;
    }
    
    public String getCodePostal() {
        return modifierCodePostal.isSelected() ? codePostalField.getText() : null;
    }
    
    public String getVille() {
        return modifierVille.isSelected() ? villeField.getText() : null;
    }
    
    public String getPays() {
        return modifierPays.isSelected() ? paysField.getText() : null;
    }
    
    public String getTelephone() {
        return modifierTelephone.isSelected() ? telephoneField.getText() : null;
    }
    
    public String getEmail() {
        return modifierEmail.isSelected() ? emailField.getText() : null;
    }
} 