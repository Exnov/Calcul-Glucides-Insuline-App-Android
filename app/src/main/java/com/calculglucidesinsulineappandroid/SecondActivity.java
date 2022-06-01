package com.calculglucidesinsulineappandroid;

import static com.calculglucidesinsulineappandroid.commun.FnCm.formatageNbre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.calculglucidesinsulineappandroid.commun.Aliment;
import com.calculglucidesinsulineappandroid.commun.BasketViewModel;
import com.calculglucidesinsulineappandroid.commun.DataHolder;

import java.util.ArrayList;


//2ème activite où l'user calcule ses glucides et son insuline rapide : à partir des données aliments de Open Food Facts ou de données aliments qu'il renseigne
public class SecondActivity extends AppCompatActivity {

    ArrayList <Aliment> altsBasket; //liste des aliments contenus dans le panier de l'user
    //recyclerView =================
    BasketAdapter adapter; //liste des views de chaque aliment contenu dans le panier de l'user
    RecyclerView recyclerView; //view qui supporte BasketAdapter
    //==============================

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        /*Construction de l'interface graphique de l'activite :
         * - mise en place des views
         * - affichage des donnees conservees des views quand retour depuis la seconde activite
         */
        Button previous=(Button)findViewById(R.id.previous);
        ImageButton addItem= (ImageButton) findViewById(R.id.addCustomAlt);
        ImageButton empty= (ImageButton) findViewById(R.id.emptyBasket);
        EditText eRatio=(EditText)findViewById(R.id.ratio);
        Button calcul=(Button)findViewById(R.id.calcul);
        TextView resultats=(TextView)findViewById(R.id.resutats);
        //==instanciation de la liste des aliments contenus dans le panier
        altsBasket= DataHolder.getInstance().getBasket();
        if(altsBasket==null) {
            altsBasket=new ArrayList<Aliment>();
        }
        //=============================================================
        //instanciation de la view qui supporte altsBasket
        // Getting reference of recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // Setting the layout as linear
        // layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        // Sending reference and data to Adapter
        adapter = new BasketAdapter(SecondActivity.this, altsBasket);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
        //=============================================================
        /*creation d'un objet basketViewModel : nous permet :
        - de gerer la maj de l'affichage de la liste des aliments contenus dans le panier (quand l'user supprime un aliment),
        - et la liste des aliments à renvoyer à la premiere activite :
        ==> l'objet basketViewModel met donc a jour altsBasket pour la view recyclerView, et le distributeur de données entre les 2 activités, DataHolder
        */
        BasketViewModel basketViewModel=new ViewModelProvider(this).get(BasketViewModel.class);
        //=============================================================
        //affichage du ratio :
        String r=DataHolder.getInstance().getRatio();
        eRatio.setText(r);
        /*Gestion du clique sur le bouton 'previous', bouton bleu en haut de l'activite
         * - conservation des donnees de l'activite : quand retour depuis la seconde activite
         * - basculement vers la premiere activite
         */
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //conservation du ratio :
                DataHolder.getInstance().setRatio(eRatio.getText().toString());
                //basculement vers la premiere activite
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /* Gestion du clique sur le bouton "CALCULER INSULINE", bouton violet en bas de l'activité
        * Calcul de la dose d'insuline rapide en fonction :
        * - du ratio indiqué par l'user
        * - et du poid de chaque aliment que l'user va manger : donnée renseignée par l'user pour calculer la quantité de glucides pour chaque aliment
        */
        calcul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Données nécessaires au calcul de la dose d'insuline rapide :
                - le ratio de l'user : c'est-à-dire ses besoins en insuline rapide pour 10 grammes de glucides : différent d'un repas à l'autre (petit-déjeuner, déjeuner et dîner)
                - la quantité de glucides des aliments == somme de la quantité de glucides de chaque aliment
                ==> On affiche la quantité de glucides contenus dans le panier, et la dose de rapide adaptée d'après le calcul
                */
                String ratio=eRatio.getText().toString();
                double glucides=0;
                double rapide=0;
                for(Aliment a:altsBasket){
                    if(a.getCglucides().length()>0){
                        glucides+=Double.valueOf(a.getCglucides());
                    }
                }
                if(ratio.length()>0 && glucides>0){
                    double r=Double.valueOf(ratio);
                    rapide=r/10*glucides;
                    resultats.setText(formatageNbre(String.valueOf(rapide))+" u. de rapide\npour "+ formatageNbre(String.valueOf(glucides))+" g. de glucides");
                }
                else{
                    Toast.makeText(SecondActivity.this, "Renseignez le ratio et le poids des aliments", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //basketViewModel observe les changements sur la liste de type ArrayList<Aliment>, et "reveille" les variables/views à mettre à jour quand l'user supprime un aliment de cette liste
        basketViewModel.getAltsBasket().observe(this, new Observer<ArrayList<Aliment>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Aliment> basket) {
                ArrayList<Aliment> altsUp = new ArrayList<Aliment>();
                for (Aliment a : basket) {
                    if (a.getInBasket()) {
                        altsUp.add(a);
                    }
                }
                //maj de la liste des aliments du panier
                altsBasket=altsUp;
                //c'est via les 2 lignes suivantes que la maj des Aliments dans le basket,
                //après suppression d'un Aliment, est prise en compte dans la listView, et affichée à l'écran !
                adapter = new BasketAdapter(SecondActivity.this, altsBasket);
                recyclerView.setAdapter(adapter);
                //on efface les resultats qui seraient affichés, et donc plus à jour
                resultats.setText("");
                //maj aussi pour la DataHolder qui conserve les aliments pour SecondActivity, et les recupere à l'ouverture avec getBasket()
                DataHolder.getInstance().setBasket(altsBasket);
            }
        });

        /*Gestion du clique sur le bouton 'poubelle' principal, bouton rouge en haut de l'activite : pour vider le panier :
        - on vide les variables concernées
        - on efface les views concernées
        * */
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CHECK
                ArrayList <Aliment> altsOffCheck = DataHolder.getInstance().getAltsOff();
                for (Aliment alt : altsOffCheck) {
                    System.out.println(alt.getName());
                    System.out.println(alt.getInBasket());
                }
                //FIN CHECK
                if(altsBasket.size()>0){
                    /*
                    On commence par maj les aliments Open Food Facts qui sont dans le panier, pour mettre en blanc et en false en main activity,
                    et on supprime le poids indiqué par l'user qui serait repris sinon, et alors affiché automatiquement dans l'ediText (associé à Aliment Open Food Facts)
                    */
                    ArrayList <Aliment> altsOff = DataHolder.getInstance().getAltsOff();
                    for(Aliment a : altsBasket){
                        String id=a.getId();
                        //==check si altsOff n'est pas nul, si l'user a déjà ajouté des alts de Open Food Facts (Off)
                        //==pour seulement ensuite (si != null), maj les alts de Off
                        if(altsOff!=null) {
                            for (Aliment aoff : altsOff) {
                                if (aoff.getId().equals(id)) {
                                    aoff.setInBasket(false);
                                    aoff.setColorBgd(Color.WHITE);
                                    aoff.setPoids("");
                                }
                            }
                        }
                    }
                    //maj du distributeur de données entre les 2 activites
                    DataHolder.getInstance().setAltsOff(altsOff);
                    //on supprime ensuite les aliments du basket (avec un array vide), et on met à jour la view qui supporte la liste des views des aliments du panier
                    altsBasket=new ArrayList<Aliment>();
                    adapter = new BasketAdapter(SecondActivity.this, altsBasket);
                    recyclerView.setAdapter(adapter);
                    //on efface les resultats qui seraient affichés, et donc plus à jour
                    resultats.setText("");
                    //maj aussi pour le DataHolder qui conserve les aliments du panier pour SecondActivity, et les recupere à l'ouverture avec getBasket()
                    DataHolder.getInstance().setBasket(altsBasket);
                }
                else{
                    Toast.makeText(SecondActivity.this,"Rien à supprimer",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*Gestion du clique sur le bouton 'plus' principal, bouton vert en haut de l'activite : pour ajouter un aliment dans le panier sans passer par la base de données de Open Food Facts
        - ouvre une fenêtre de dialogue qui permet à l'user d' :
            - indiquer le nom d'un nouvel aliment,
            - indiquer le % de glucides de cet aliment
        * */
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDialog.NewAlimentListener listener = new CustomDialog.NewAlimentListener() {
                    @Override
                    public void newAlimentEntered(String fullName, String pGlucides) {
                        /*
                        Consiste à ajouter un Aliment dans l'array d'Aliment altsBasket,
                        puis à mettre à jour l'affichage des items
                        */
                        Aliment newAlt=new Aliment();
                        newAlt.setName(fullName);
                        newAlt.setPglucides(pGlucides);
                        newAlt.setInBasket(true);
                        altsBasket.add(newAlt);
                        //on met à jour la liste des views des aliments du panier
                        adapter = new BasketAdapter(SecondActivity.this, altsBasket);
                        recyclerView.setAdapter(adapter);
                        //on efface les resultats qui seraient affichés, et donc plus à jour
                        resultats.setText("");
                        //maj aussi pour le DataHolder qui conserve les aliments pour SecondActivity, et les recupere à l'ouverture avec getBasket()
                        DataHolder.getInstance().setBasket(altsBasket);
                    }
                };
                final CustomDialog dialog = new CustomDialog(SecondActivity.this, listener);
                dialog.show();
            }
        });
        //=====================================================================================================
    }//FIN de OnCreate() ======================================================================================

} //FIN de l'activité =========================================================================================

