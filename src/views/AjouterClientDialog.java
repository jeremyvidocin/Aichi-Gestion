package views;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.regex.Pattern;
import rojerusan.RSMaterialButtonRectangle;

public class AjouterClientDialog extends JDialog {
    private boolean validated = false;
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField societeField;
    private JTextField adresseField;
    private JFormattedTextField codePostalField;
    private JTextField villeField;
    private JComboBox<String> paysCombo;
    private JFormattedTextField telephoneField;
    private JTextField emailField;
    
    // Patterns de validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile(
        "^(\\+33|0)[1-9](\\d{2}){4}$"
    );
    private static final Pattern CODE_POSTAL_PATTERN = Pattern.compile(
        "^\\d{5}$"
    );
    
    public AjouterClientDialog(JFrame parent) {
        super(parent, "Ajouter un client", true);
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(600, 550);
        setLocationRelativeTo(null);
        
        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Création des composants
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        societeField = new JTextField(20);
        adresseField = new JTextField(20);
        
        // Masque pour le code postal
        try {
            javax.swing.text.MaskFormatter cpFormatter = new javax.swing.text.MaskFormatter("#####");
            cpFormatter.setPlaceholderCharacter('_');
            codePostalField = new JFormattedTextField(cpFormatter);
        } catch (ParseException e) {
            codePostalField = new JFormattedTextField();
        }
        codePostalField.setColumns(5);
        
        villeField = new JTextField(20);
        
        // Liste des pays
        String[] pays = {"France", "Belgique", "Suisse", "Luxembourg", "Allemagne", "Espagne", "Italie", "Royaume-Uni"};
        paysCombo = new JComboBox<>(pays);
        
        // Masque pour le téléphone
        try {
            javax.swing.text.MaskFormatter telFormatter = new javax.swing.text.MaskFormatter("##.##.##.##.##");
            telFormatter.setPlaceholderCharacter('_');
            telephoneField = new JFormattedTextField(telFormatter);
        } catch (ParseException e) {
            telephoneField = new JFormattedTextField();
        }
        telephoneField.setColumns(14);
        
        emailField = new JTextField(20);
        
        // Ajout des composants avec GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nom* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prenomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Société :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(societeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Adresse* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(adresseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Code postal* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(codePostalField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Ville* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(villeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Pays :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(paysCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Téléphone* :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(telephoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(new JLabel("Email :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
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
    
    private void valider() {
        StringBuilder erreurs = new StringBuilder();
        
        // Validation des champs obligatoires
        if (nomField.getText().trim().isEmpty()) {
            erreurs.append("- Le nom est obligatoire\n");
        }
        
        if (adresseField.getText().trim().isEmpty()) {
            erreurs.append("- L'adresse est obligatoire\n");
        }
        
        String codePostal = codePostalField.getText().replaceAll("_", "");
        if (!CODE_POSTAL_PATTERN.matcher(codePostal).matches()) {
            erreurs.append("- Le code postal doit contenir 5 chiffres\n");
        }
        
        if (villeField.getText().trim().isEmpty()) {
            erreurs.append("- La ville est obligatoire\n");
        }
        
        String telephone = telephoneField.getText().replaceAll("[. _]", "");
        if (!TELEPHONE_PATTERN.matcher(telephone).matches()) {
            erreurs.append("- Le numéro de téléphone n'est pas valide\n");
        }
        
        // Validation de l'email si renseigné
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            erreurs.append("- L'adresse email n'est pas valide\n");
        }
        
        if (erreurs.length() > 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez corriger les erreurs suivantes :\n" + erreurs.toString(),
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        validated = true;
        dispose();
    }
    
    public boolean isValidated() {
        return validated;
    }
    
    public String getNom() {
        return nomField.getText().trim();
    }
    
    public String getPrenom() {
        return prenomField.getText().trim();
    }
    
    public String getSociete() {
        return societeField.getText().trim();
    }
    
    public String getAdresse() {
        return adresseField.getText().trim();
    }
    
    public String getCodePostal() {
        return codePostalField.getText().replaceAll("_", "");
    }
    
    public String getVille() {
        return villeField.getText().trim();
    }
    
    public String getPays() {
        return (String) paysCombo.getSelectedItem();
    }
    
    public String getTelephone() {
        return telephoneField.getText().replaceAll("[. _]", "");
    }
    
    public String getEmail() {
        return emailField.getText().trim();
    }
} 