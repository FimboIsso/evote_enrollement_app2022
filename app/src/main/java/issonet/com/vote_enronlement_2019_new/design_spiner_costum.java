package issonet.com.vote_enronlement_2019_new;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class design_spiner_costum {

    //chargement de la qualite des produits dans le combo box
    String [] items;
    Spinner spinner;
    Context context;

    public design_spiner_costum( Context context,Spinner spinner,String [] items) {
        this.items = items;
        this.spinner = spinner;
        this.context = context;
    }

    public void remplir_spiner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, items);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.layout_spiner_adapter);
        dataAdapter.setNotifyOnChange(true);
        String produit, tempo;
        // looping through all rows and adding to list

        spinner.setAdapter(dataAdapter);
    }
}
