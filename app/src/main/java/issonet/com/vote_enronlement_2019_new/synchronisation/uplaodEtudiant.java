package issonet.com.vote_enronlement_2019_new.synchronisation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import issonet.com.vote_enronlement_2019_new.SQLite.DatabaseHandler;

public class uplaodEtudiant  extends AsyncTask<Void, Void, Void> {

    Activity context;
    String urll;
    ProgressDialog progressDialog;


    public uplaodEtudiant(Activity context, String urll) {
        this.context = context;
        this.urll = urll;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Chargement ...","Veuillez patientez s.v.p !",false,false);
    }

    @Override
    protected Void doInBackground(Void... voids) {
//
//        Cursor res = new DatabaseHandler(context).getEutdiantToUpload();
//            int count = 0;
//            while (res.moveToNext()) {
//
//            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
//            dataToSend.add(new BasicNameValuePair("action","uploadEtudiant"));
//            dataToSend.add(new BasicNameValuePair("id", res.getString(res.getColumnIndex("id"))));
//            dataToSend.add(new BasicNameValuePair("nom", res.getString(res.getColumnIndex("nom"))));
//            dataToSend.add(new BasicNameValuePair("postnom", res.getString(res.getColumnIndex("postnom"))));
//            dataToSend.add(new BasicNameValuePair("prenom", res.getString(res.getColumnIndex("prenom"))));
//            dataToSend.add(new BasicNameValuePair("faculte", res.getString(res.getColumnIndex("faculte"))));
//            dataToSend.add(new BasicNameValuePair("departement", res.getString(res.getColumnIndex("departement"))));
//            dataToSend.add(new BasicNameValuePair("promotion", res.getString(res.getColumnIndex("promotion"))));
//
//            HttpParams httpParams = getHttpParams();
//
//            HttpClient httpClient = new DefaultHttpClient(httpParams);
//            HttpPost httpPost = new HttpPost(urll);
//
//            try {
//                httpPost.setEntity(new UrlEncodedFormEntity(dataToSend));
//                httpClient.execute(httpPost);
//                //cochage de donnees deja synchroniser
//                //signalisation que le fichier est deja uploaded
//                new DatabaseHandler(context).updateWheUploading(res.getString(res.getColumnIndex("id")), "1");
//                count++;
//            } catch (Exception e) {
//                e.printStackTrace();
//                progressDialog.cancel();
//            }

       // }

        upload(urll);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        progressDialog.cancel();
        super.onPostExecute(aVoid);
    }

    private HttpParams getHttpParams() {

        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;

    }

    private void upload(String url) {

        final Cursor res = new DatabaseHandler(context).getEutdiantToUpload();
        int count = res.getCount();
        Log.e("jsjs",count+"");
        while (res.moveToNext()) {

            final HashMap<String, String> MyData = new HashMap<String, String>();
            MyData.put("Content-Type", "application/x-www-form-urlencoded");
            MyData.put("action", "uploadEtudiant");
            MyData.put("id", res.getString(res.getColumnIndex("id")));
            MyData.put("nom", res.getString(res.getColumnIndex("nom")));
            MyData.put("postnom", res.getString(res.getColumnIndex("postnom")));
            MyData.put("prenom", res.getString(res.getColumnIndex("prenom")));
            MyData.put("faculte", res.getString(res.getColumnIndex("faculte")));
            MyData.put("departement", res.getString(res.getColumnIndex("departement")));
            MyData.put("promotion", res.getString(res.getColumnIndex("promotion")));


            RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, urll, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   // Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                    if (response.contains("succès")) {
                        Toast.makeText(context, "Synchronisation réussie avec succès", Toast.LENGTH_LONG).show();
                        new DatabaseHandler(context).updateWheUploading(res.getString(res.getColumnIndex("id")), "1");

                    } else {
                        Toast.makeText(context, "Echec d'enregistrement : error = " + response, Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //This code is executed if there is an error.
                    error.printStackTrace();
                }
            }) {
                protected Map<String, String> getParams() {

                    return MyData;
                }
            };

            MyStringRequest.setRetryPolicy(mRetryPolicy);
            MyRequestQueue.add(MyStringRequest);

        }
        res.close();
    }
}
