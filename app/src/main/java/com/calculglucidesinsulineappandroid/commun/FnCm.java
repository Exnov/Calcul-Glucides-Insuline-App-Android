package com.calculglucidesinsulineappandroid.commun;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

//Méthodes appelées dans les 2 activités : Fn pour fonction et Cm pour commun :
//essentiellement pour du formatage de nombres et de texte :

public class FnCm {

    //FORMATAGE NOMBRES ============================================================================
    //si pourcentage avec .0, on supprime le .0
    public static String formatagePercent(String i){
        String data=i;
        double n=Double.valueOf(i);
        //pourcentage avec .0, on supprime le .0
        if(n%1==0){
            data=String.valueOf((int) n);
        }
        return data;
    }

    //si calcul de glucides avec .0, on supprime le .0, et si trop long, on coupe à 2 chiffres après la virgule
    public static String formatageNbre(String i){
        String data=i;
        if(!data.equals("")) {
            double n = Double.valueOf(i);
            //pourcentage avec .0, on supprime le .0
            if (n % 1 == 0) {
                data = String.valueOf((int) n);
            } else { //si chiffre après virgule, on limite à 2
                DecimalFormat df = new DecimalFormat("#.##");
                DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(dfs);
                data = String.valueOf(df.format(n));
            }
        }
        return data;
    }

    //FORMATAGE TEXTE ==============================================================================
    //pour couper les noms affichés trop longs des aliments, et qui empêchent l'affichage des quantités de glucides dans le basket
    public static String cutAlimentName(String name,int limit){
        String nameUp=name;
        if(name.length()>limit){ //23
            nameUp=name.substring(0,limit-1); //22
        }
        return nameUp;
    }

} //FIN ============================================================================================


