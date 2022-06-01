package com.calculglucidesinsulineappandroid;

import static com.calculglucidesinsulineappandroid.commun.FnCm.cutAlimentName;
import static com.calculglucidesinsulineappandroid.commun.FnCm.formatageNbre;
import static com.calculglucidesinsulineappandroid.commun.FnCm.formatagePercent;


import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.calculglucidesinsulineappandroid.commun.Aliment;
import com.calculglucidesinsulineappandroid.commun.BasketViewModel;
import com.calculglucidesinsulineappandroid.commun.DataHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//appelée dans Second Activity : view qui supporte la liste des views de chaque aliment contenu dans le panier
//c'est ici qu'est geree la suppression des aliments du panier dans la seconde activité : quand l'user clique sur le bouton "poubelle" de la view d'un aliment
public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {

    //Proprietes
    ArrayList <Aliment> aliments;
    Context context;

    //Constructeur
    public BasketAdapter(Context context, ArrayList <Aliment> aliments) {
        this.context = context;
        this.aliments =  aliments;
    }

    //Association du ViewHolder à notre template xml : row_item_basket
    @NonNull
    @Override
    public BasketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_basket, parent, false);
        BasketAdapter.ViewHolder viewHolder = new BasketAdapter.ViewHolder(view);
        return viewHolder;
    }

    //Initialisation du ViewHolder, et déclaration de ses composants
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        TextView cGlucides;
        EditText editPoids;
        ImageView imageFlag;
        ImageButton basketOut;

        public ViewHolder(View view) {
            super(view);
            label = (TextView) view.findViewById(R.id.altName);
            cGlucides = (TextView) view.findViewById(R.id.cGlucides);
            editPoids=(EditText) view.findViewById(R.id.poids);
            imageFlag = (ImageView) view.findViewById(R.id.imageViewFlag);
            basketOut = (ImageButton) view.findViewById(R.id.basketOut);
        }
    }

    //Definition du contenu et des fonctions de nos composants :
    @Override
    public void onBindViewHolder(@NonNull BasketAdapter.ViewHolder holder, int position) {

        //declaration de nos composants, et mise en place de leur contenu à afficher
        String aName=aliments.get(position).getName(); //nom de l'aliment
        String pGlu=formatagePercent(aliments.get(position).getPglucides()) + " %"; //% de glucides de l'aliment
        String altLabel=cutAlimentName(aName,23) + " : " + pGlu; //nom de l'aliment + son % de glucides : pour le label
        holder.label.setText(altLabel);
        holder.cGlucides.setText(formatGlucidesCalc(formatageNbre(aliments.get(position).getCglucides())));
        holder.editPoids.setText(aliments.get(position).getPoids());
        /*gestion de l'illustration de l'aliment : 2 cas :
        - une image existe dans la base de données de Open Food Facts : on la recupere
        - pas d'image chez Open Food Facts : on met une image par défaut
        */
        String urlImg=aliments.get(position).getImage();
        if(!urlImg.equals("vide")){
            Picasso.get().load(urlImg).resize(50, 50).centerCrop().into(holder.imageFlag);
        }
        else{
            Picasso.get().load(R.drawable.new_alt).into(holder.imageFlag);
        }
        //appel d'une fonction pour gerer la recuperation du poids et des glucides, et leur affichage, quand retour depuis la premiere activite
        handleEditPoids(holder.editPoids, holder.cGlucides, position);
        //===================================================================================
        /*gestion de la suppression d'un aliment dans le ViewHolder : quand l'user clique sur le bouton "poubelle" de la view d'un aliment
        ==> clique de suppression observé dans SecondActivity sur la liste des aliments du panier,
            via notre objet basketViewModel présent aussi là-bas,
            qui permet la maj des aliments affiches dans le panier
        */
        BasketViewModel basketViewModel=new ViewModelProvider((ViewModelStoreOwner)context).get(BasketViewModel.class);
        int pos=position;
        holder.basketOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aliments.get(pos).setInBasket(false);
                basketViewModel.setAltsBasket(aliments);
                //==maj aussi des aliments Open Food Facts affichés en main activity ==> on supprime leur background gris
                String id=aliments.get(pos).getId();
                ArrayList <Aliment> altsOff = DataHolder.getInstance().getAltsOff();
                for(Aliment a : altsOff){
                    if (a.getId().equals(id)){
                        a.setInBasket(false);
                        a.setColorBgd(Color.WHITE);
                        a.setPoids("");
                    }
                }
                DataHolder.getInstance().setAltsOff(altsOff);
            }
        });
        //===================================================================================
    }//FIN de onBindViewHolder() ============================================================

    //renvoie le nombre d'aliments dans le panier
    @Override
    public int getItemCount() {
        return aliments.size();
    }

    //Fonctions ===============================================================================

    //Pour la conservation des poids de l'editText, et du calcul de glucides quand l'user revient sur la seconde activité ; méthode appelée dans onBindViewHolder()
    public void handleEditPoids(EditText editPoids, TextView cGlucides, int position){
        TextWatcher mTextWatcher=new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                aliments.get(position).setPoids(String.valueOf(s));
                aliments.get(position).setCglucides();
                cGlucides.setText(formatGlucidesCalc(formatageNbre(aliments.get(position).getCglucides())));
                DataHolder.getInstance().setBasket(aliments);
            }
        };
        editPoids.addTextChangedListener(mTextWatcher);
    }

    //formatage de l'affichage de la quantité de glucides d'un aliment du panier
    public String formatGlucidesCalc(String data){
        if(!data.equals("")){
            data="glucides : " + data + " g";
        }
        return data;
    }
    //FIN fonctions ==========================================================================

} //FIN ======================================================================================

