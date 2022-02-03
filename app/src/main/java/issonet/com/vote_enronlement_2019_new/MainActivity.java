package issonet.com.vote_enronlement_2019_new;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.Settings;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import java.util.List;

import issonet.com.vote_enronlement_2019_new.NFC.NdefMessageParser;
import issonet.com.vote_enronlement_2019_new.NFC.ParsedNdefRecord;
import issonet.com.vote_enronlement_2019_new.SQLite.DatabaseHandler;

public class MainActivity extends AppCompatActivity {
    Spinner spn_promotion, spn_faculte,spn_departement;
    EditText ed_nom,ed_postnom,ed_prenom;
    Button btn_submit;
    TextView text   ; NfcAdapter nfcAdapter; PendingIntent pendingIntent;

    SharedPreferences shared;
    SharedPreferences.Editor editorr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shared = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        editorr = shared.edit();

        spn_promotion = (Spinner) findViewById(R.id.spn_promotion);
        spn_faculte = (Spinner)findViewById(R.id.spn_facultee);
        spn_departement = (Spinner)findViewById(R.id.spn_departement);

        ed_nom = (EditText)findViewById(R.id.ed_nom);
        ed_postnom = (EditText)findViewById(R.id.ed_postnom);
        ed_prenom = (EditText)findViewById(R.id.ed_prenom);

        btn_submit = (Button)findViewById(R.id.btn_submit);

        new design_spiner_costum(this,spn_promotion,
                getResources().getStringArray(R.array.spn_promotion)).remplir_spiner();

        new design_spiner_costum(this,spn_faculte,
                getResources().getStringArray(R.array.spn_faculte)).remplir_spiner();

        setTitle("Registration Vote 2019");

        text = (TextView) findViewById(R.id.txt_id);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //verification si le champs sont vide

                if(isEditTextEmpty(ed_nom) || isEditTextEmpty(ed_postnom) || isEditTextEmpty(ed_prenom)
                        || isEditTextEmpty(text)){

                }else {
                    //demande confirmation et insertion enrollement
                    confirmerEnrollement();

                }
            }
        });


        //remplir departement en fonction de la faculte
        spn_faculte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: // pour le departement de FASA
                        new design_spiner_costum(MainActivity.this,spn_departement,
                                getResources().getStringArray(R.array.spn_departement_fasa)).remplir_spiner();
                        break;

                    case 1://pour FASIC
                        new design_spiner_costum(MainActivity.this,spn_departement,
                                getResources().getStringArray(R.array.spn_departement_communication)).remplir_spiner();
                        break;
                    case 2:// pour economie
                        new design_spiner_costum(MainActivity.this,spn_departement,
                                getResources().getStringArray(R.array.spn_departement_economie)).remplir_spiner();
                        break;

                    case 3://pour la theologie
                        new design_spiner_costum(MainActivity.this,spn_departement,
                                getResources().getStringArray(R.array.spn_departement_theologie)).remplir_spiner();
                        break;

                    case 4://pour la fac de droit
                        new design_spiner_costum(MainActivity.this,spn_departement,
                                getResources().getStringArray(R.array.spn_departement_droit)).remplir_spiner();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_liste_etudiant:
                startActivity(new Intent(MainActivity.this,affichEtudiantActivity.class));
                break;

            case R.id.action_serveur:
                configurationsServeur();
                break;

            case R.id.action_about_us:
                startActivity(new Intent(MainActivity.this,aboutUsActivity.class));
                break;

            case R.id.action_exit:
                confirmeExit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            displayMsgs(msgs);
        }
    }

    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str);
        }

        text.setText(builder.toString());
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(500);

    }


    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();

        sb.append(toDec(id));

        return sb.toString();
    }


    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length-1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }



    //verification si le champs sont vide

    public boolean isEditTextEmpty(EditText editText) {
        if(editText.getText().toString().matches(" ") ||
                editText.getText().toString().length() == 0 ||
                editText.getText().toString().trim().length() == 0
                ){
            editText.setError("Please complete this field");
            return true;
        }else{
            return false;
        }
    }

    public boolean isEditTextEmpty(TextView editText) {
        if(editText.getText().toString().matches(" ") ||
                editText.getText().toString().length() == 0 ||
                editText.getText().toString().trim().length() == 0
                ){
            editText.setError("Please complete this field");
            return true;
        }else{
            return false;
        }
    }

    //methode dialogue de confirmation

    public void confirmerEnrollement(){

        final AlertDialog builder = new AlertDialog.Builder(this).create();

        builder.setCancelable(false);
        //builder.setTitle(title);

        //initiaisation des compsant sur le view
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialogue_confirmation, null);
        builder.setView( view);

        Button btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        Button btn_confirmer = (Button) view.findViewById(R.id.btn_confirmer);

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

                //insertion dans la base de donnees
                boolean result = false;

                result = new DatabaseHandler(MainActivity.this)
                        .insertInEtudiant(text.getText().toString(),
                                ed_nom.getText().toString(), ed_postnom.getText().toString(),
                                ed_prenom.getText().toString(), spn_faculte.getSelectedItem().toString(),
                                spn_departement.getSelectedItem().toString(),
                                spn_promotion.getSelectedItem().toString());

                if (result) {
                    Toast.makeText(MainActivity.this, "Operation executed successfully", Toast.LENGTH_LONG).show();
                    text.setText("");
                    ed_nom.setText("");
                    ed_postnom.setText("");
                    ed_prenom.setText("");

                    spn_faculte.setSelection(0);
                    spn_promotion.setSelection(0);
                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();

                }

                builder.dismiss();

            }
        });
        builder.show();
    }


    //methode pour la configuration du serveur
    public void configurationsServeur(){

        final AlertDialog builder = new AlertDialog.Builder(this).create();

        builder.setCancelable(false);
        //builder.setTitle(title);

        //initiaisation des compsant sur le view
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_configuration_serveur, null);
        builder.setView( view);

        Button btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        Button btn_confirmer = (Button) view.findViewById(R.id.btn_confirmer);

        final EditText ed_adresse_serveur = (EditText) view.findViewById(R.id.ed_adresse_serveur);

        //en selectionnant sur le radio bouto
        String adresseIP = shared.getString("adresseServeur","");
        ed_adresse_serveur.setText(adresseIP);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        btn_confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //insertion dans la base de donnees
                editorr.putString("adresseServeur", ed_adresse_serveur.getText().toString());
                editorr.apply();
                editorr.commit();

                builder.dismiss();

            }
        });
        builder.show();
    }


    //methode dialogue confirmation sortie
    public void confirmeExit(){

        final AlertDialog builder = new AlertDialog.Builder(this).create();

        builder.setCancelable(false);
        //builder.setTitle(title);

        //initiaisation des compsant sur le view
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialogue_confirmation_sortie, null);
        builder.setView( view);

        Button btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        Button btn_confirmer = (Button) view.findViewById(R.id.btn_confirmer);

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
                Toast.makeText(MainActivity.this,"Zoonic vous dit merci",Toast.LENGTH_SHORT).show();
                finish();
                builder.dismiss();

            }
        });
        builder.show();
    }


    boolean doublePress =  false;
    @Override
    public void onBackPressed() {
        if(doublePress) {
            super.onBackPressed();
            Toast.makeText(this,"Zoonic vous dit merci",Toast.LENGTH_SHORT).show();
            return;
        }
        this.doublePress = true;
        Toast.makeText(this,"Please click BACK again to exit",Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePress = false;
            }
        },3000);
    }



}
