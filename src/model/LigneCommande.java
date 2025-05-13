package model;

public class LigneCommande {
    private int id;
    private int idCommande;
    private int idArticle;
    private String reference;
    private String designation;
    private int quantite;
    private double prixUnitaireHT;
    private double tauxTVA;
    private double montantHT;  // Calculé: prixUnitaireHT * quantite
    private double montantTVA; // Calculé: prixUnitaireHT * quantite * tauxTVA / 100
    private double montantTTC; // Calculé: prixUnitaireHT * quantite * (1 + tauxTVA / 100)

    public LigneCommande(int id, int idCommande, int idArticle, String reference,
                         String designation, int quantite, double prixUnitaireHT, double tauxTVA) {
        this.id = id;
        this.idCommande = idCommande;
        this.idArticle = idArticle;
        this.reference = reference;
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaireHT = prixUnitaireHT;
        this.tauxTVA = tauxTVA;

        // Calculer les montants
        this.montantHT = prixUnitaireHT * quantite;
        this.montantTVA = prixUnitaireHT * quantite * tauxTVA / 100;
        this.montantTTC = prixUnitaireHT * quantite * (1 + tauxTVA / 100);
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

    public String getReference() {
        return reference;
    }

    public String getDesignation() {
        return designation;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnitaireHT() {
        return prixUnitaireHT;
    }

    public double getTauxTVA() {
        return tauxTVA;
    }

    public double getMontantHT() {
        return montantHT;
    }

    public double getMontantTVA() {
        return montantTVA;
    }

    public double getMontantTTC() {
        return montantTTC;
    }
}
