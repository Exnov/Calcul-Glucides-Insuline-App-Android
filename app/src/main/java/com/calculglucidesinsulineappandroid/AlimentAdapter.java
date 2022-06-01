package com.calculglucidesinsulineappandroid;

import static com.calculglucidesinsulineappandroid.commun.FnCm.cutAlimentName;
import static com.calculglucidesinsulineappandroid.commun.FnCm.formatagePercent;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.calculglucidesinsulineappandroid.commun.Aliment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//appelée dans Main Activity : view qui supporte la liste des views de chaque aliment trouvé dans la base de données de Open Food Facts
public class AlimentAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList <Aliment> aliments;

    //Constructeur
    public AlimentAdapter(Activity context, ArrayList <Aliment> aliments) {
        super(context, R.layout.row_item_opf, aliments);
        this.context = context;
        this.aliments=aliments;
    }

    //Declaration des composants pour la view d'un aliment, et definition du contenu à y afficher
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.row_item_opf, null, true);
        //declaration des composants
        TextView label = (TextView) row.findViewById(R.id.altNameOpf); //pour le nom de l'aliment
        TextView pGlucides = (TextView) row.findViewById(R.id.pGlucidesOpf); //pour le % de glucides de l'aliment
        ImageView imageFlag = (ImageView) row.findViewById(R.id.imageOpf); //pour l'illustration de l'aliment
        //definition du contenu des composants
        String aName=aliments.get(position).getName(); //nom de l'aliment
        String pGlu=formatagePercent(aliments.get(position).getPglucides()) + " %"; //% de glucides de l'aliment
        label.setText(cutAlimentName(aName,44));
        pGlucides.setText(pGlu);
        //couleur d'arriere-plan de l'aliment : renseigne si l'aliment est dans le panier
        int color=aliments.get(position).getColorBg();
        row.setBackgroundColor(color);
        /*gestion de l'illustration de l'aliment : 2 cas :
        - une image existe dans la base de données de Open Food Facts : on la recupere
        - pas d'image chez Open Food Facts : on met une image par défaut
        */
        String urlImg=aliments.get(position).getImage();
        if(urlImg !="vide"){
            Picasso.get().load(urlImg).resize(50, 50).centerCrop().into(imageFlag);
        }
        else{
            Picasso.get().load(R.drawable.replace_alt).into(imageFlag);
        }
        return  row;
    }//FIN de getView() =================================================================

} //FIN =================================================================================


