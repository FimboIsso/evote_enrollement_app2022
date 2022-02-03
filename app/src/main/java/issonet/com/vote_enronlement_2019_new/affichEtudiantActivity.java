package issonet.com.vote_enronlement_2019_new;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import issonet.com.vote_enronlement_2019_new.SQLite.DatabaseHandler;
import issonet.com.vote_enronlement_2019_new.synchronisation.uplaodEtudiant;

public class affichEtudiantActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public  int[] to = new int[] {R.id.text_id, R.id.text_noms, R.id.text_faculte, R.id.text_promotion};
    public String[] columns = new String[] { "_id", "id", "noms","faculte","promotion"};
    public String[] from = new String[] {"id", "noms","faculte","promotion"};

    public SimpleCursorAdapter adapter ;
    public MatrixCursor matrixCursor;

    ListView lv;
    Spinner spn_promotion_filter,spn_faculte_filter;
    ArrayList<String> items;
    SharedPreferences shared;
    SharedPreferences.Editor editorr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affich_etudiant);

        lv =  (ListView)findViewById(R.id.lv_etudiant);
        spn_faculte_filter = (Spinner) findViewById(R.id.spn_facultee);
        spn_promotion_filter = (Spinner) findViewById(R.id.spn_promotion);
        //affichage de la liste des etudiant

        shared = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        editorr = shared.edit();

        new design_spiner_costum(this,spn_promotion_filter,
                getResources().getStringArray(R.array.spn_promotion_filter)).remplir_spiner();

        new design_spiner_costum(this,spn_faculte_filter,
                getResources().getStringArray(R.array.spn_faculte_filter)).remplir_spiner();

        setTitle("Students list");
        //ajout evenement sur le combo filter faculte

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spn_promotion_filter.setOnItemSelectedListener(this);

        registerForContextMenu(lv);

        //afficEtudiant(new DatabaseHandler(affichEtudiantActivity.this).getEutdiant());

        spn_faculte_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //quand on click sur falcuutee
                //spn_promotion_filter.setSelection(0);
                if(position == 0){
                    //on selection tous par facultes
                    //toutes les l1 par exemples
                    if(spn_promotion_filter.getSelectedItemPosition() == 0){
                        //tous les etudiants
                    }else{
                        //touts les etudiant d'une promotion
                        afficEtudiant(
                                new DatabaseHandler(affichEtudiantActivity.this)
                                        .getEutdiant(
                                                spn_promotion_filter.getSelectedItem().toString()
                                        )
                        );
                    }

                }else{
                    if(spn_promotion_filter.getSelectedItemPosition() == 0){
                        //toues les etudiant par facultes
                        afficEtudiant(
                                new DatabaseHandler(affichEtudiantActivity.this)
                                        .getEutdiant(
                                                spn_faculte_filter.getSelectedItem().toString()
                                        )
                        );

                    }else{
                        afficEtudiant(
                                new DatabaseHandler(affichEtudiantActivity.this)
                                        .getEutdiant(
                                                spn_faculte_filter.getSelectedItem().toString(),
                                                spn_promotion_filter.getSelectedItem().toString()
                                        )
                        );
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void afficEtudiant(Cursor res){
        matrixCursor= new MatrixCursor(columns);
        //startManagingCursor(matrixCursor);
        items = new ArrayList<String>();
        if(res.getCount() == 0){

            //affichage du message vide dans la liste view
            new empty_liste_view(affichEtudiantActivity.this,lv,
                    getResources().getStringArray(R.array.empty_lv)).display();

            //hlv.setAdapter(null);

        }else {
            StringBuffer buffer = new StringBuffer();
            int count = 0;
            while (res.moveToNext()) {

                items.add(res.getString(res.getColumnIndex("nom"))+"  "
                        +res.getString(res.getColumnIndex("postnom"))+"  "
                        +res.getString(res.getColumnIndex("prenom")));

                matrixCursor.addRow(new Object[]{count,
                        res.getString(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("nom")) + " "
                                + res.getString(res.getColumnIndex("postnom")) + " "
                                + res.getString(res.getColumnIndex("prenom")) + " ",
                        res.getString(res.getColumnIndex("faculte")),
                        res.getString(res.getColumnIndex("promotion"))
                });
                count++;
            }

            adapter = new SimpleCursorAdapter(this, R.layout.row_etudiant, matrixCursor, from, to
                    , 0);
            lv.setAdapter(adapter);

            // lv.setOnItemClickListener(new op
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //quand on clique sur la facult;

        if(position==0){
            if(spn_faculte_filter.getSelectedItemPosition() == 0) {
                //touts les etudiants
                afficEtudiant(
                        new DatabaseHandler(affichEtudiantActivity.this)
                                .getEutdiant()
                );
            }else{
                //touts les tudiants par promotion

                afficEtudiant(
                        new DatabaseHandler(affichEtudiantActivity.this)
                                .getEutdiant(
                                        spn_promotion_filter.getSelectedItem().toString()
                                )
                );

            }
        }else{

            if(spn_faculte_filter.getSelectedItemPosition() == 0){

                afficEtudiant(
                        new DatabaseHandler(affichEtudiantActivity.this)
                                .getEutdiant(
                                        spn_promotion_filter.getSelectedItem().toString()
                                )
                );
            }else {

                afficEtudiant(
                        new DatabaseHandler(affichEtudiantActivity.this)
                                .getEutdiant(
                                        spn_faculte_filter.getSelectedItem().toString(),
                                        spn_promotion_filter.getSelectedItem().toString()
                                )
                );
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_liste,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;


            case R.id.action_synchronisation:
                String adresseIP = shared.getString("adresseServeur","");
               // Toast.makeText(affichEtudiantActivity.this,adresseIP,Toast.LENGTH_LONG).show();
                new uplaodEtudiant(affichEtudiantActivity.this,"" +
                        "http://"+adresseIP+"/vote2019/uploadElecteur/upload.php").execute();
                break;

            case R.id.action_delete:
                    confirmDelete("Do you realy want to delete all data ?");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lv_etudiant) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(items.get(info.position));

            //MatrixCursor matrixCursor1 = (MatrixCursor)matrixCursor..getItemAtPosition(position);

            String[] menuItems = getResources().getStringArray(R.array.menu_contextuel);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }


    //confirmation suppression all
    public void confirmDelete(String message){

        final AlertDialog builder = new AlertDialog.Builder(affichEtudiantActivity.this).create();

        builder.setCancelable(false);
        //builder.setTitle(title);

        //initiaisation des compsant sur le view
        LayoutInflater inflater = LayoutInflater.from(affichEtudiantActivity.this);
        View view = inflater.inflate(R.layout.layout_dialogue_confirmation_sortie, null);
        builder.setView( view);

        Button btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        Button btn_confirmer = (Button) view.findViewById(R.id.btn_confirmer);

        TextView txt_message = (TextView)view.findViewById(R.id.txt_message);
        txt_message.setText(message);

        //en selectionnant sur le radio bouto

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        btn_confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatabaseHandler(affichEtudiantActivity.this).VideLaTableEtudiant();
                builder.dismiss();
                startActivity(new Intent(affichEtudiantActivity.this,affichEtudiantActivity.class));
                finish();
                Toast.makeText(
                        affichEtudiantActivity.this,
                        "All data have been deleted successfuly",Toast.LENGTH_LONG).show();

            }
        });
        builder.show();
    }
}
