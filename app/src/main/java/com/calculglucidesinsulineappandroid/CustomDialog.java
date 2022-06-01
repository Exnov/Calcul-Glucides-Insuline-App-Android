package com.calculglucidesinsulineappandroid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

//pour creer une fenêtre de dialogue dans la seconde activite qui permet à l'user d'ajouter un aliment sans passer par la base de données Open Food Facts
public class CustomDialog extends Dialog {

    interface NewAlimentListener {
        public void newAlimentEntered(String fullName, String pGlucides);
    }

    public Context context;
    private EditText editNewAltName;
    private EditText editNewPgluAlt;
    private ImageButton buttonOK;
    private ImageButton buttonCancel;
    private CustomDialog.NewAlimentListener listener;

    public CustomDialog(Context context, CustomDialog.NewAlimentListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        this.editNewAltName = (EditText) findViewById(R.id.name_new_alt);
        this.editNewPgluAlt = (EditText) findViewById(R.id.pGlucides_new_alt);
        this.buttonOK = (ImageButton) findViewById(R.id.button_ok);
        this.buttonCancel  = (ImageButton) findViewById(R.id.button_cancel);

        this.buttonOK .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOKClick();
            }
        });
        this.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCancelClick();
            }
        });
    }

    //L'user clique sur le bouton vert de validation (signe de validation)
    private void buttonOKClick()  {
        String fullName = this.editNewAltName.getText().toString();
        String pGlucides=this.editNewPgluAlt.getText().toString();

        if(fullName.isEmpty() || pGlucides.isEmpty())  {
            Toast.makeText(this.context, "Entrez un nom d'aliment, et un % de glucides", Toast.LENGTH_LONG).show();
            return;
        }
        this.dismiss(); //ferme la fenêtre de dialogue

        if(this.listener!= null)  {
            this.listener.newAlimentEntered(fullName,pGlucides);
        }
    }

    //L'user clique sur le bouton rouge d'annulation (signe "x")
    private void buttonCancelClick()  {
        this.dismiss();
    }
}
