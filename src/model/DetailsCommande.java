package model;

public class DetailsCommande {
    private int id;
    private int idCommande;
    private int idArticle;
    private int quantite;
    private double prixUnitaire;

    public DetailsCommande(int id, int idCommande, int idArticle, int quantite, double prixUnitaire) {
        this.id = id;
        this.idCommande = idCommande;
        this.idArticle = idArticle;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public int getId() {
        return id;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }
}