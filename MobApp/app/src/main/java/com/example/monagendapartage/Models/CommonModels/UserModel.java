package com.example.monagendapartage.Models.CommonModels;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;

    private String numeroVoie;
    private String nomVoie;
    private String typeVoie;
    private String codePostal;
    private String nom;
    private String prenom;
    private String ville;
    private String telephone;
    private String email;

    private String availableDates;


    public UserModel(String id, String username, String imageURL, String status, String search,
                     String numeroVoie, String nomVoie, String typeVoie,
                     String codePostal, String nom, String prenom, String ville, String telephone,
                     String email, String availableDates) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.numeroVoie = numeroVoie;
        this.nomVoie = nomVoie;
        this.typeVoie = typeVoie;
        this.codePostal = codePostal;
        this.nom = nom;
        this.prenom = prenom;
        this.ville = ville;
        this.telephone = telephone;
        this.email = email;
        this.availableDates = availableDates;
    }

    public UserModel(String id, String username, String imageURL, String status, String search,
                     String numeroVoie, String nomVoie, String typeVoie, String codePostal,
                     String nom, String prenom, String ville, String telephone, String email) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;

        this.numeroVoie = numeroVoie;
        this.nomVoie = nomVoie;
        this.typeVoie = typeVoie;
        this.codePostal = codePostal;
        this.nom = nom;
        this.prenom = prenom;
        this.ville = ville;
        this.telephone = telephone;
        this.email = email;

    }

    public UserModel() {

    }

    protected UserModel(Parcel in) {
        id = in.readString();
        username = in.readString();
        imageURL = in.readString();
        status = in.readString();
        search = in.readString();
        numeroVoie = in.readString();
        nomVoie = in.readString();
        typeVoie = in.readString();
        codePostal = in.readString();
        nom = in.readString();
        prenom = in.readString();
        ville = in.readString();
        telephone = in.readString();
        email = in.readString();
        availableDates = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getNumeroVoie() {
        return numeroVoie;
    }

    public void setNumeroVoie(String numeroVoie) {
        this.numeroVoie = numeroVoie;
    }

    public String getNomVoie() {
        return nomVoie;
    }

    public void setNomVoie(String nomVoie) {
        this.nomVoie = nomVoie;
    }

    public String getTypeVoie() {
        return typeVoie;
    }

    public void setTypeVoie(String typeVoie) {
        this.typeVoie = typeVoie;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(String availableDates) {
        this.availableDates = availableDates;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", status='" + status + '\'' +
                ", search='" + search + '\'' +
                ", numeroVoie='" + numeroVoie + '\'' +
                ", nomVoie='" + nomVoie + '\'' +
                ", typeVoie='" + typeVoie + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", ville='" + ville + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", availableDates='" + availableDates + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(imageURL);
        parcel.writeString(status);
        parcel.writeString(search);
        parcel.writeString(numeroVoie);
        parcel.writeString(nomVoie);
        parcel.writeString(typeVoie);
        parcel.writeString(codePostal);
        parcel.writeString(nom);
        parcel.writeString(prenom);
        parcel.writeString(ville);
        parcel.writeString(telephone);
        parcel.writeString(email);
        parcel.writeString(availableDates);
    }
}
