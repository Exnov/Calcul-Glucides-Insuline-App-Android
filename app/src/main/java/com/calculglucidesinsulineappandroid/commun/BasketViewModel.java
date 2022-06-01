package com.calculglucidesinsulineappandroid.commun;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/*
==> La classe BasketViewModel nous permet de mettre à jour la liste de views des aliments du panier dans la seconde activité , quand l'user supprime un aliment de cette liste.
==> Classe appelée dans SecondActivity et BasketAdapter.
*/

public class BasketViewModel extends ViewModel {

    MutableLiveData<ArrayList<Aliment>> altsBasket = new MutableLiveData<>();

    public LiveData<ArrayList <Aliment>> getAltsBasket() {
        return altsBasket;
    }
    public void setAltsBasket(ArrayList <Aliment> alts) {
        altsBasket.setValue(alts);
    }

} //FIN =====================================================================

