package com.calculglucidesinsulineappandroid.commun;

import java.util.ArrayList;

/* Classe qui permet de partager des données entre les 2 activités :
==> pratique pour la conservation des données et leur affichage quand on passe d'une activité à une autre :
- le ratio
- le dernier aliment recherché sur Open Food Facts
- la liste des derniers aliments trouvés dans la base de données de Open Food Facts : ex: toutes les bananes recensées sur Open Food Facts
- la liste des aliments contenus dans le panier de l'user
 */

public class DataHolder {

    //Déclaration des variables appelées sur les 2 activités
    private String ratio; //quantité d'insuline rapide necessaire à l'user pour couvrir ses besoins pour 10 grammes de glucides : change à chaque repas (petit-déjeuner, déjeuner, et dîner)
    private ArrayList <Aliment> alimentsBasket; //aliments contenus dans le panier
    private ArrayList <Aliment> alimentsOff; //aliments de la derniere requete sur Open Food Facts
    private String aliment; //dernier aliment recherche sur Open Food Facts

    //Declaration des getters et setters de nos variables
    public String getRatio() {return ratio;}
    public void setRatio(String r) {this.ratio = r;}

    public ArrayList <Aliment> getBasket(){
        return alimentsBasket;
    }
    public void setBasket(ArrayList <Aliment> alts){ //setBasket
        this.alimentsBasket=alts;
    }

    public ArrayList <Aliment> getAltsOff(){
        return alimentsOff;
    }
    public void setAltsOff(ArrayList <Aliment> alts){ //setBasket
        this.alimentsOff=alts;
    }

    public String getAliment() {return aliment;}
    public void setAliment(String a) {this.aliment = a;}

    //Déclaration de l'objet pour communiquer dans nos activités avec nos variables déclarées au-dessus et leurs méthodes
    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

}//FIN =====================================================================

