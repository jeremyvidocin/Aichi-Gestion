package model;

import java.time.LocalDateTime;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String societe;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;
    private String telephone;
    private String email;
    private LocalDateTime dateInscription;
    private boolean actif;
    private LocalDateTime dateDerniereCommande;

    public Client(int id, String nom, String prenom, String societe, String adresse,
                  String codePostal, String ville, String pays, String telephone,
                  String email, LocalDateTime dateInscription, boolean actif,
                  LocalDateTime dateDerniereCommande) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.societe = societe;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
        this.telephone = telephone;
        this.email = email;
        this.dateInscription = dateInscription;
        this.actif = actif;
        this.dateDerniereCommande = dateDerniereCommande;
    }

    // Constructeur simplifié pour la compatibilité avec le code existant
    public Client(int id, String nom, String adresse, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.pays = "France";
        this.actif = true;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getSociete() {
        return societe;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getVille() {
        return ville;
    }

    public String getPays() {
        return pays;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public boolean isActif() {
        return actif;
    }

    public LocalDateTime getDateDerniereCommande() {
        return dateDerniereCommande;
    }

    // Méthode pour obtenir le nom complet
    public String getNomComplet() {
        if (prenom != null && !prenom.isEmpty()) {
            return prenom + " " + nom;
        }
        return nom;
    }
}
