package com.calculglucidesinsulineappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.calculglucidesinsulineappandroid.commun.Aliment;
import com.calculglucidesinsulineappandroid.commun.DataHolder;
import com.calculglucidesinsulineappandroid.commun.MySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


//1ere activite où l'user recupere les données des aliments qu'il veut dans la base de données en ligne Open Food Facts
public class MainActivity extends AppCompatActivity {

    //Declaration de variables et de views :
    ArrayList <Aliment> altsOff; //liste d'aliments de Off (Open Food Facts) recuperes suite à la requête de l'user
    ArrayList <Aliment> altsBasket; //liste d'aliments que l'user a mis dans son panier.
    AlimentAdapter alimentList; //liste des views de chaque aliment de Off affichés suite à la requête de l'user
    ListView listView; //view qui supporte et gere AlimentAdapter, comme les cliques pour chaque view d'aliment de AlimentAdapter
    TextView labelM; //label qui affiche le nombre d'aliments que l'user a mis dans son panier.


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Construction de l'interface graphique de l'activite :
        * - mise en place des views
        * - affichage des donnees conservees des views quand retour depuis la seconde activite
        */
        labelM=(TextView)findViewById(R.id.labelmain);
        Button next=(Button)findViewById(R.id.next);
        EditText altUser=(EditText) findViewById(R.id.aliment);
        ImageButton rechercher=(ImageButton)findViewById(R.id.search);
        ImageButton cleaner=(ImageButton)findViewById(R.id.clean);
        listView=(ListView)findViewById(R.id.altlist);
        //==affichage dans l'editText de l'aliment recherché par l'user, quand retour depuis la seconde activité
        String altInEdit=DataHolder.getInstance().getAliment();
        altUser.setText(altInEdit);
        //==affichage dans le label du nombre d'aliments que l'user a mis dans son panier, quand retour depuis la seconde activité
        altsBasket=DataHolder.getInstance().getBasket();
        if(altsBasket==null || altsBasket.size()==0){
            altsBasket=new ArrayList<Aliment>();
            displayLabelMain(0);
        }
        if(altsBasket.size()>0){
            displayLabelMain(altsBasket.size());
        }
        //==instanciation de la liste des aliments de Open Food Facts
        altsOff=DataHolder.getInstance().getAltsOff();
        if(altsOff==null) {
            altsOff=new ArrayList<Aliment>();
        }
        //==instanciation de la liste des views des aliments de Open Food Facts
        alimentList = new AlimentAdapter(MainActivity.this, altsOff);
        //==association des views des aliments à la view qui les supporte et gere leurs cliques
        listView.setAdapter(alimentList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //maj (mise à jour) de la liste des aliments de OFF et du panier, quand clique sur la view d'un aliment
                //quand l'aliment est deja dans le panier
                if(altsOff.get(i).getInBasket()){
                    //maj de la liste altsOff
                    altsOff.get(i).setInBasket(false);
                    altsOff.get(i).setColorBgd(Color.WHITE);
                    altsOff.get(i).setPoids("");
                    //maj de la liste altsBasket
                    int index=-1;
                    for(Aliment a:altsBasket){
                        if(a.getId().equals(altsOff.get(i).getId())){
                            index=altsBasket.indexOf(a);
                        }
                    }
                    altsBasket.remove(index);
                }
                //quand l'aliment n'est pas dans le panier
                else{
                    //maj de la liste altsOff
                    altsOff.get(i).setInBasket(true);
                    altsOff.get(i).setColorBgd(Color.LTGRAY);
                    //maj de la liste altsBasket
                    altsBasket.add(altsOff.get(i));
                }
                //==maj du distributeur de données entre les 2 activites
                DataHolder.getInstance().setBasket(altsBasket);
                DataHolder.getInstance().setAltsOff(altsOff);
                //==maj de la liste des views aliment, et de la view qui supporte cette liste
                alimentList = new AlimentAdapter(MainActivity.this, altsOff);
                listView.setAdapter(alimentList);
                //==maj du label qui affiche le nombre d'aliments mis dans le panier
                displayLabelMain(altsBasket.size());
            }
        });

        /*Gestion du clique sur le bouton 'next', bouton bleu en haut de l'activite
        * - conservation des donnees de l'activite : quand retour depuis la seconde activite
        * - basculement vers la seconde activite
        */
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //conservation de l'aliment recherche par l'user :
                DataHolder.getInstance().setAliment(altUser.getText().toString());
                //basculement vers la seconde activite
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        //gestion du clique sur le bouton vert avec la loupe : rechercher l'aliment de l'user dans la base de données en ligne de Open Food Facts
        rechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String altRequest=altUser.getText().toString();
                if(altRequest.length()>0){
                    search(altRequest);
                }
                else{
                    Toast.makeText(MainActivity.this,"Renseignez un aliment",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //gestion du clique sur le bouton bleu avec le logo de recyclage, pour effacer les données
        cleaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vider editText
                altUser.setText("");
                //vider listview
                altsOff=new ArrayList<Aliment>();
                DataHolder.getInstance().setAltsOff(altsOff);
                alimentList = new AlimentAdapter(MainActivity.this, altsOff);
                listView.setAdapter(alimentList);
            }
        });
    }
    //=================================================================================================

    /* FONCTIONS APPELEES DANS L'ACTIVITE
    * - search() : lancer une requête dans la base de données de Open Food Facts, pour trouver une liste d'aliments correspondants à l'aliment recherché par l'user
    * - displayLabelMain() : maj de l'affichage du nombre d'aliments contenus dans le panier de l'user
    */

    public void search(String aliment){

        /* refs :
        cf https://developer.android.com/training/volley/requestqueue
        cf https://developer.android.com/training/volley/request
        String url="https://fr.openfoodfacts.org/categorie/pizzas.json";
        */
        altsOff=new ArrayList<Aliment>(); //liste des aliments trouvés sur Open Food Facts, et correspondants à l'aliment recherché par l'user
        String url="https://fr.openfoodfacts.org/categorie/"+aliment+".json"; //notre url pour la requête

        //construction de la requête JSON ==========================================================================
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray jsonArray = response.optJSONArray("products");

                        //si l'aliment existe dans la base de données de Open Food Facts, on recupere les donnees
                        if (jsonArray.length()>0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                Aliment altRequest=new Aliment();
                                //==
                                JSONObject jsonObjects = jsonArray.optJSONObject(i);
                                String imgurl="vide";
                                if(jsonObjects.optString("image_front_url").length()>0){
                                    imgurl = jsonObjects.optString("image_front_url");
                                }
                                String product_name_fr = jsonObjects.optString("product_name_fr");

                                if(product_name_fr.length()>0){
                                    //==
                                    Gson gson=new Gson();
                                    String nutriments = jsonObjects.optString("nutriments");
                                    HashMap<String, String> data = gson.fromJson(nutriments, HashMap.class);

                                    for (String elt : data.keySet()) {

                                        if(elt.contains("carbohydrates_100g")){
                                            //==
                                            altRequest.setName(product_name_fr);
                                            altRequest.setImage(imgurl);
                                            altRequest.setPglucides(String.valueOf(data.get(elt)));
                                            altRequest.setId(jsonObjects.optString("id"));
                                            //==pour check si aliment pas déjà dans Basket ==> check via id ==> si oui inBasket==true et colorBg==Color.LGRAY
                                            ArrayList <Aliment> altsBasketRef=DataHolder.getInstance().getBasket();
                                            if(altsBasketRef!=null){
                                                for(Aliment a : altsBasketRef){
                                                    if(a.getId().equals(altRequest.getId())){
                                                        altRequest.setInBasket(true);
                                                        altRequest.setColorBgd(Color.LTGRAY);
                                                    }
                                                }
                                            }
                                            //===================================================
                                        }
                                    }
                                    //===========================================================
                                }
                                /*on ajoute à notre liste des aliments trouvés sur Open Food Facts, et qu'on affiche, uniquement ceux qui ont un ID,
                                parce que l'id est une donnée dont nous nous servons juste au-dessus, pour distinguer les aliments recherchés par
                                l'user, et qui seraient déjà dans le panier (cas d'une recherche d'un aliment déjà recherché, et déjà mis dans le panier) de ceux qui ne le seraient pas
                                */
                                if(altRequest.getId().length()>0){
                                    altsOff.add(altRequest);
                                }
                            }
                        }
                        //si l'aliment n'existe pas dans la base de données de Open Food Facts
                        else{
                            Toast.makeText(MainActivity.this,"Aliment introuvable",Toast.LENGTH_SHORT).show();
                        }
                        //==maj du distributeur de données entre les 2 activites
                        DataHolder.getInstance().setAltsOff(altsOff);
                        //==maj de la liste des views aliment, et de la view qui supporte cette liste
                        alimentList = new AlimentAdapter(MainActivity.this, altsOff);
                        listView.setAdapter(alimentList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Pas de connexion internet",Toast.LENGTH_SHORT).show();

                    }
                });
        //envoie de la requête
        // Access the RequestQueue through your singleton class cf https://google.github.io/volley/simple.html
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    //=================================================================================

    public void displayLabelMain(int n){
        labelM.setText("Mes aliments : " + String.valueOf(n));
    }
    //==============================================================================================

} //FIN ============================================================================================

