package issonet.com.vote_enronlement_2019_new;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class empty_liste_view {

    //chargement de la qualite des produits dans le combo box
    String [] items;
    ListView lv;
    Context context;

    public empty_liste_view(Context context, ListView lv, String [] items) {
        this.items = items;
        this.lv = lv;
        this.context = context;
    }

    public void display() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.layout_empty_liste_view,R.id.txt_empty_listView, items);

        lv.setAdapter(dataAdapter);
    }
}
