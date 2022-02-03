package issonet.com.vote_enronlement_2019_new.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;



public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="votebd.db";

    Context context;

    public DatabaseHandler(Context context) {

        super(context, DATABASE_NAME, null, 1);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `etudiant` (\n" +
                "  `id` varchar(20) PRIMARY KEY,\n" +
                "  `nom` varchar(50) NOT NULL,\n" +
                "  `postnom` varchar(50) NOT NULL,\n" +
                "  `prenom` varchar(50) DEFAULT NULL,\n" +
                "  `faculte` varchar(20) DEFAULT NULL,\n" +
                "  `departement` varchar(20) DEFAULT NULL,\n" +
                "  `promotion` varchar(20) DEFAULT NULL,\n" +
                "  `etat` int(1) DEFAULT (0));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean insertInEtudiant(String id,String nom,String postnom,String prenom,
                                    String faculte,String departement,String promotion){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("id",id);
        contentValues.put("nom",nom);
        contentValues.put("postnom",postnom);
        contentValues.put("prenom",prenom);
        contentValues.put("faculte",faculte);
        contentValues.put("departement",departement);
        contentValues.put("promotion",promotion);

        try {

            long result = db.insert("etudiant", null, contentValues);
            if (result == -1) {
                db.close();
                return false;
            } else {
                db.close();
                return true;
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, "Vous etez deja enrollee,", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public Cursor getEutdiant(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM etudiant",null);
        return res;
    }

    public Cursor getEutdiantToUpload(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM etudiant WHERE etat = '0'",null);
        return res;
    }

    public Cursor getEutdiant(String valeur ){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM etudiant WHERE id ='"+valeur+"' OR faculte = '"+valeur+"' OR promotion = '"+valeur+"'",null);
        return res;
    }

    public Cursor getEutdiant(String faculte,String promotion ){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM etudiant WHERE faculte = '"+faculte+"' AND promotion = '"+promotion+"'",null);
        return res;
    }


    public boolean updateWheUploading(String id,String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("etat",state);
        db.update("etudiant",contentValues,"id = ?",new String[]{id});
        return true;
    }


    //suppression de toute la table
    public Integer VideLaTableEtudiant(){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete("etudiant",null,null);

    }

}
