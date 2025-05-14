package model;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String typeAcces;

    public User(int id, String nom, String prenom, String email, String telephone, String typeAcces) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.typeAcces = typeAcces;
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

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getTypeAcces() {
        return typeAcces;
    }

    public String getRole() {
        return typeAcces;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(typeAcces);
    }
}
