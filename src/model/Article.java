package model;

import java.time.LocalDateTime;

public class Article {
    private int id;
    private String reference;
    private String nom;
    private String description;
    private String categorie;
    private double prixVente;
    private double prixAchat;
    private int quantiteEnStock;
    private int seuilAlerte;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public Article(int id, String reference, String nom, String description, String categorie,
                   double prixVente, double prixAchat, int quantiteEnStock, int seuilAlerte,
                   LocalDateTime dateCreation, LocalDateTime dateModification) {
        this.id = id;
        this.reference = reference;
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.prixVente = prixVente;
        this.prixAchat = prixAchat;
        this.quantiteEnStock = quantiteEnStock;
        this.seuilAlerte = seuilAlerte;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    // Constructeur simplifié pour la compatibilité avec le code existant
    public Article(int id, String nom, String description, double prixVente, int quantiteEnStock) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prixVente = prixVente;
        this.quantiteEnStock = quantiteEnStock;
    }

    public int getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getCategorie() {
        return categorie;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public double getPrix() {
        return prixVente; // Pour compatibilité avec code existant
    }

    public int getQuantiteEnStock() {
        return quantiteEnStock;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }
}
