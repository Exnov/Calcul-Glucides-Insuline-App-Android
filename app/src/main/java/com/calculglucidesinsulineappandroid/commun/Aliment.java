package com.calculglucidesinsulineappandroid.commun;

import android.graphics.Color;

import java.io.Serializable;

/* Classe qui represente les aliments :
- ceux recuperes dans la base de donnes Open Food Facts,
- et ceux mis dans le panier
*/
public class Aliment implements Serializable {

    //Declaration des proprietes
    String name; //nom de l'aliment
    String poids; //poids de l'aliment
    String pglucides; //% de glucides de l'aliment
    String cglucides; //calcul de glucides de l'aliment ==> calculer en fonction de son % de glucides et de son poids dans l'assiette indique par l'user
    Boolean inBasket; //renseigne si l'aliment est dans le panier
    String image; //url de l'image qui illustre l'aliment
    String id; //l'id de l'aliment dans la base de données Open Food Facts ; l'aliment ajouté par l'user dans le panier qui ne vient de Open Food Facts n'a pas d'id
    int colorBgd; //couleur de l'arriere-plan de la view de l'aliment dans la premiere activite : renseigne si l'user a mis l'aliment dans le panier : blanc si pas dans le panier, gris si dans le panier

    //Constructeur
    public Aliment() {
        this.name = "";
        this.poids = "";
        this.pglucides = "";
        this.cglucides= "";
        this.inBasket=false;
        this.image="vide";
        this.id="";
        this.colorBgd= Color.WHITE;
    }

    //Getters et setters
    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPoids(){
        return this.poids;
    }
    public void setPoids(String p){
        this.poids=p;
    }

    public String getPglucides(){
        return this.pglucides;
    }
    public void setPglucides(String p){
        this.pglucides=p;
    }

    public String getCglucides(){
        return this.cglucides;
    }
    public void setCglucides(){ //avec calcul de la quantité
        String resultat="";
        if(this.pglucides.length()>0 && this.poids.length()>0){
            double percent=Double.valueOf(this.pglucides);
            double qtt=Double.valueOf(this.poids);
            resultat=String.valueOf(qtt*percent/100);
        }
        this.cglucides=resultat;
    }

    public Boolean getInBasket(){
        return this.inBasket;
    }
    public void setInBasket(Boolean bool){ //pour sortir l'aliment du basket
        this.inBasket=bool;
    }

    public String getImage(){
        return this.image;
    }
    public void setImage(String img) {
        this.image = img;
    }

    public String getId(){
        return this.id;
    }
    public void setId(String i) {
        this.id = i;
    }

    public int getColorBg(){
        return this.colorBgd;
    }
    public void setColorBgd(int c) {
        this.colorBgd = c;
    }

}//FIN =============================================================================================

