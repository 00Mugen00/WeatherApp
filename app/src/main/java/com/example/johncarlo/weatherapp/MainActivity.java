package com.example.johncarlo.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.ProvinceName,R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Ejemplo: http://api.wunderground.com/api/6382c243f4bd1c6a/conditions/q/ES/Malaga.json
                String url = "http://api.wunderground.com/api/6382c243f4bd1c6a/conditions/q/ES/"+((String) parent.getItemAtPosition(position))+".json";
                //Obtener el tiempo usando una petición http
                Weather weather = new Weather();
                weather.execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private class Weather extends AsyncTask<String,Void,String> {

        @Override
        protected  void onPreExecute(){
            Context context = getApplicationContext();
            CharSequence text = "Loading Weather Data";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Get get = new Get();
                String jsonData = get.run(params[0]);
                return jsonData;
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Procesar el JSON
            TextView textView = (TextView) findViewById(R.id.textView2);
            try {
                JSONObject reader = new JSONObject(s);
                StringBuilder weather = new StringBuilder();
                weather.append("Province: ");
                weather.append(reader.getJSONObject("current_observation").getJSONObject("display_location").get("full"));
                weather.append("\nWeather: ");
                weather.append(reader.getJSONObject("current_observation").get("weather"));
                weather.append("\nTemperature: ");
                weather.append(reader.getJSONObject("current_observation").get("temp_c"));
                weather.append(" Cº");
                weather.append("\nWind: ");
                weather.append(reader.getJSONObject("current_observation").get("wind_kph"));
                weather.append(" kph");
                textView.setText(weather.toString());
            } catch (JSONException e) {
                textView.setText("Error: Unable to make request");
            }
        }
    }
}
