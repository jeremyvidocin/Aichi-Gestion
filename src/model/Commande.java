package model;

import java.util.Date;

public class Commande {
    private int id;
    private String numero;
    private String reference;
    private int idClient;
    private Date dateCommande;
    private Date dateValidation;
    private Date dateExpedition;
    private Date dateLivraison;
    private String statut;
    private int idStatut;
    private double montantHT;
    private double tauxTVA;
    private double montantTVA;
    private double montantTTC;
    private String commentaire;
    private String adresseLivraison;
    private int idUtilisateur;

    public Commande(int id, String numero, String reference, int idClient, Date dateCommande,
                    Date dateValidation, Date dateExpedition, Date dateLivraison,
                    String statut, int idStatut, double montantHT, double tauxTVA,
                    double montantTVA, double montantTTC, String commentaire,
                    String adresseLivraison, int idUtilisateur) {
        this.id = id;
        this.numero = numero;
        this.reference = reference;
        this.idClient = idClient;
        this.dateCommande = dateCommande;
        this.dateValidation = dateValidation;
        this.dateExpedition = dateExpedition;
        this.dateLivraison = dateLivraison;
        this.statut = statut;
        this.idStatut = idStatut;
        this.montantHT = montantHT;
        this.tauxTVA = tauxTVA;
        this.montantTVA = montantTVA;
        this.montantTTC = montantTTC;
        this.commentaire = commentaire;
        this.adresseLivraison = adresseLivraison;
        this.idUtilisateur = idUtilisateur;
    }

    // Constructeur simplifié pour la compatibilité avec le code existant
    public Commande(int id, int idUtilisateur, int idClient, Date date, String statut) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idClient = idClient;
        this.dateCommande = date;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public String getReference() {
        return reference;
    }

    public int getIdClient() {
        return idClient;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public Date getDateValidation() {
        return dateValidation;
    }

    public Date getDateExpedition() {
        return dateExpedition;
    }

    public Date getDateLivraison() {
        return dateLivraison;
    }

    public String getStatut() {
        return statut;
    }

    public int getIdStatut() {
        return idStatut;
    }

    public double getMontantHT() {
        return montantHT;
    }

    public double getTauxTVA() {
        return tauxTVA;
    }

    public double getMontantTVA() {
        return montantTVA;
    }

    public double getMontantTTC() {
        return montantTTC;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    // Pour compatibilité avec le code existant
    public Date getDate() {
        return dateCommande;
    }
}
