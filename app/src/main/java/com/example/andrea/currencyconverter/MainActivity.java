package com.example.andrea.currencyconverter;

import android.app.PendingIntent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
   public static ArrayList<CharSequence> countryCodes = new ArrayList<>(170);
   public static Spinner spinner;
   public static ArrayAdapter<CharSequence> adapter;
   public static String code = "USD";
   public JSONObject rates;
   public static HashMap<String, String> codeToCountry = new HashMap<>();
   private  TextView longName;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      spinner = (Spinner) findViewById(R.id.spinner);
      adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, countryCodes);
      spinner.setAdapter(adapter);
      DownloadTask downloadTask = new DownloadTask();
      downloadTask.execute("http://apilayer.net/api/live?access_key=0167f2a30958c345d3e713c9ea44abe1&format=1", "quotes");
      DownloadTask downloadTask1 = new DownloadTask();
      downloadTask1.execute("http://apilayer.net/api/list?access_key=0167f2a30958c345d3e713c9ea44abe1&prettyprint=1", "currencies");
      spinner.setOnItemSelectedListener(this);
      longName = (TextView) findViewById(R.id.long_name);

   }

   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      code = parent.getItemAtPosition(pos).toString();
      spinner.setSelection(pos);
      TextView txtVw  =  (TextView) parent.getChildAt(0);
      if (txtVw != null)
      {
         txtVw.setTextColor(Color.BLACK);
      }
      longName.setText(codeToCountry.get(code));

   }


   @Override
   public void onNothingSelected(AdapterView<?> parent)
   {
      spinner.setSelection(0);
      code = parent.getItemAtPosition(0).toString();
      longName.setText(codeToCountry.get(code));
   }


   public void viewConvertCurrency(View view)
   {
      try
      {
         EditText dollarAmount = (EditText) findViewById(R.id.dollarAmount);
         Double dollars = Double.parseDouble(dollarAmount.getText().toString());
         String toFrom = getString(R.string.usd) + code;
         Double multiplier = rates.getDouble(toFrom);
         Double amount = Math.round(dollars * multiplier * 100) / 100.0;
         TextView toCurrency = (TextView) findViewById(R.id.toCurrency);
         toCurrency.setText(Double.toString(amount));
      }
      catch (JSONException e)
      {
         Log.e("Multiplier", "no double found");
      }

   }


   public class DownloadTask extends AsyncTask<String, Void, String>
   {
      private URL url;
      private HttpURLConnection urlConnection;
      private boolean try1;

      @Override
      protected String doInBackground(String... urls)
      {
         String result = "";
         try
         {
            url = new URL(urls[0]);
            try1 = urls[1].equals("quotes");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while (data != -1)
            {
               char current = (char) data;
               result += current;
               data = reader.read();
            }
            return result;
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         return null;
      }

      @Override
      protected void onPostExecute(String result)
      {
         super.onPostExecute(result);
         try
         {
            JSONObject jsonObject = new JSONObject(result);
            if (try1)
            {
               String quotes = jsonObject.getString("quotes");
               rates = new JSONObject(quotes);
               Iterator<String> stringIterator = rates.keys();
               while (stringIterator.hasNext())
               {
                  String toFrom = stringIterator.next();
                  String code = toFrom.substring(3);
                  countryCodes.add(code);
               }
               adapter.notifyDataSetChanged();
            }
            else
            {
               String currencies = jsonObject.getString("currencies");
               JSONObject currenciesJSON = new JSONObject(currencies);
               Iterator<String> fullNames = currenciesJSON.keys();
               while (fullNames.hasNext())
               {
                  String key = fullNames.next();
                  String value = currenciesJSON.getString(key);
                  codeToCountry.put(key, value);
               }

            }

         }
         catch (JSONException e)
         {
            e.printStackTrace();
         }


      }
   }
}

