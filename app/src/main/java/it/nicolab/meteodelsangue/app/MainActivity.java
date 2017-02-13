package it.nicolab.meteodelsangue.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MainActivity extends ActionBarActivity  {

    ImageView imgAP, imgAM, imgBP, imgBM, img0P, img0M, imgABP, imgABM;
    TextView txtAggiornamento;


    private String Aggironamento;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgAP = (ImageView) findViewById(R.id.imageViewAP);
        imgAM = (ImageView) findViewById(R.id.imageViewAM);
        imgBP = (ImageView) findViewById(R.id.imageViewBP);
        imgBM = (ImageView) findViewById(R.id.imageViewBM);
        img0P = (ImageView) findViewById(R.id.imageView0P);
        img0M = (ImageView) findViewById(R.id.imageView0M);
        imgABP = (ImageView) findViewById(R.id.imageViewABP);
        imgABM = (ImageView) findViewById(R.id.imageViewABM);

        txtAggiornamento = (TextView) findViewById(R.id.textViewAggiornamento);

        handleSSLHandshake();
        new LongOperation().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {
            Intent i = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            return true;
        }

        else if(id == R.id.action_legenda) {
            Intent i = new Intent(MainActivity.this, LegendaActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            return true;
        }

        else if(id == R.id.action_reload) {

            new LongOperation().execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class LongOperation extends AsyncTask<Void, Void, List<Notifica>> {

        @Override
        protected List<Notifica> doInBackground(Void... params) {

            List<Notifica> lista = new ArrayList<Notifica>();
            Document doc = null;

            try {
                doc = Jsoup.parse(new URL("https://web2.e.toscana.it/crs/meteo/"), 2000);   //http://www.bits4beats.it/

                int idx = 1;
                Elements resultImg = doc.select("a img");
                for (Element link : resultImg) {
                    Notifica n = new Notifica();
                    n.set_Link(downloadBitmap("https://web2.e.toscana.it/crs/meteo/" + link.attr("src")));
                    lista.add(n);
                    n = null;
                }

                Elements resultImput = doc.select("input");
                for (Element link : resultImput) {

                    Notifica n = new Notifica();
                    n.set_Stato(link.attr("value"));
                    lista.add(n);
                    n = null;

                }

                Elements resultDiv = doc.select("div");
                for (Element link : resultDiv) {
                    if(link.attr("id").toString().equalsIgnoreCase("aggiornamento")) {
                        Aggironamento = link.text().toString();
                        Log.d("ID", link.attr("id").toString());

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return lista;
        }

        protected void onPostExecute(List<Notifica> lista) {

            if((lista != null) && (lista.size() > 0)) {
                for (int idx = 0; idx <= lista.size(); idx++) {

                    if (idx == 0) {
                        imgAP.setImageBitmap(lista.get(idx).get_Link());
                    }
                    else if (idx == 2) {
                        imgAM.setImageBitmap(lista.get(idx).get_Link());
                    }
                    if (idx == 4) {
                        imgBP.setImageBitmap(lista.get(idx).get_Link());
                    }
                    else if (idx == 6) {
                        imgBM.setImageBitmap(lista.get(idx).get_Link());
                    }

                    if (idx == 8) {
                        img0P.setImageBitmap(lista.get(idx).get_Link());
                    }
                    else if (idx == 10) {
                        img0M.setImageBitmap(lista.get(idx).get_Link());
                    }
                    if (idx == 12) {
                        imgABP.setImageBitmap(lista.get(idx).get_Link());
                    }
                    else if (idx == 14) {
                        imgABM.setImageBitmap(lista.get(idx).get_Link());
                    }
                }

                if(Aggironamento.equalsIgnoreCase("")) {

                }
                else
                {
                    txtAggiornamento.setText(Aggironamento);
                }
            }
            else {
                txtAggiornamento.setText("Data aggiornamento non disponibile");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                // set title
                alertDialogBuilder.setTitle("Avviso");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Si è verificato un problema controlla che la connessione dati sia attiva.")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                //MainActivity.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }


            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attendere ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }

    /**
     *
     * @param Messaggio
     */
    private void dialogMessage(String Messaggio) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Avviso");

        // set dialog message
        alertDialogBuilder
                .setMessage(Messaggio)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        MainActivity.this.finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    /**
     *
     * @param fileUrl
     * @return
     */
    private Bitmap downloadBitmap(String fileUrl)
    {
        InputStream is = null;
        try
        {
            HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.connect();
            is = conn.getInputStream();

            return BitmapFactory.decodeStream(is);
        }
        catch (MalformedURLException e)
        {
            is = null;
        }
        catch (IOException e)
        {

        }
        finally
        {
            if (is != null)
                try
                {
                    is.close();
                }
                catch (IOException e)
                {

                }
        }

        return null;
    }

    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    /** * Enables https connections */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override public void checkClientTrusted ( X509Certificate[] certs, String authType ) { }

                        @Override public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        }
        catch (Exception ignored) { } }

}
