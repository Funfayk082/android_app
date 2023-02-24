package com.example.weather_report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

//import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.common.InitializationListener;

import org.json.JSONException;
import org.json.JSONObject;

//import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetData.AsyncResponse, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private static final String YANDEX_MOBILE_ADS_TAG = "YandexMobileAds";

    private Button searchButton;
    private EditText searchField;
    private TextView city;

    protected static boolean showWind = true;
    protected static boolean showTemp = true;
    protected static String color = "red";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = findViewById(R.id.searchField);
        city = findViewById(R.id.city);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        setupSharedPreferences();

        MobileAds.initialize(this, new InitializationListener() {
            @Override
            public void onInitializationCompleted() {
                Log.d(YANDEX_MOBILE_ADS_TAG, "SDK initialized");
            }
        });

        BannerAdView mbBannerAdView = (BannerAdView) findViewById(R.id.banner_ad_view);
        String AdUnitId = "demo-banner-yandex";
        mbBannerAdView.setAdUnitId(AdUnitId);
        mbBannerAdView.setAdSize(AdSize.stickySize(300));

        final AdRequest adRequest = new AdRequest.Builder().build();
        mbBannerAdView.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                
            }

            @Override
            public void onAdFailedToLoad(AdRequestError adRequestError) {
                
            }

            @Override
            public void onAdClicked() {
                
            }

            @Override
            public void onLeftApplication() {
                
            }

            @Override
            public void onReturnedToApplication() {
                
            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {

            }
        });
        mbBannerAdView.loadAd(adRequest);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.show_wind_settings_key))){
            showWind=sharedPreferences.getBoolean(getString(R.string.show_wind_settings_key), true);

        } else if (key.equals(getString(R.string.show_temp_settings_key))){
            showTemp=sharedPreferences.getBoolean(getString(R.string.show_temp_settings_key), true);

        } else if (key.equals(getString(R.string.pref_color_label_key))){
            color=sharedPreferences.getString(getString(R.string.pref_color_label_key), getString(R.string.pref_color_label_red_value));

        }

    }

    private void setupSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showWind = sharedPreferences.getBoolean(getString(R.string.show_wind_settings_key), true);
        showTemp = sharedPreferences.getBoolean(getString(R.string.show_temp_settings_key), true);
        color = sharedPreferences.getString(getString(R.string.pref_color_label_key), getString(R.string.pref_color_label_red_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private URL buildURL(String city){
        String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
        String PARAMETER = "q";
        String PARAM_APPID = "appid";
        String appid_value = "f11f5e2e462b25dc717cc70e1cd3d3b3";

        Uri buildUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(PARAMETER, city).appendQueryParameter(PARAM_APPID, appid_value).build();
        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.d(TAG, "buildURL: "+url);
        return url;
    }

    @Override
    public void onClick(View view) {
//            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=57.7679158&lon=40.9269141&appid=f11f5e2e462b25dc717cc70e1cd3d3b3");
        URL url = buildURL(searchField.getText().toString().trim());
        city.setText(searchField.getText().toString());
        new GetData(this).execute(url);
        if (city.getText().toString().equals("Кострома")){
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "О, Ульяна туточки!",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void proccessFinish(String output) {
        Log.d(TAG, "proccessFinish: "+output);
        try {

            JSONObject resultJSON = new JSONObject(output);
            JSONObject weather = resultJSON.getJSONObject("main");
            JSONObject sys = resultJSON.getJSONObject("sys");

            TextView temp = findViewById(R.id.tempValue);
            String temp_K = weather.getString("temp");
            float temp_C = Float.parseFloat(temp_K);
            temp_C -= 273.15;
            String temp_C_string = Float.toString(temp_C);

            if (this.showWind) {
                temp.setText(temp_C_string);
            } else temp.setText("");

            TextView pressure = findViewById(R.id.presValue);
            String pres_b = weather.getString("pressure");
            float pres_m = Float.parseFloat(pres_b);
            pres_m *= 0.750064;

            if (this.showTemp) {
                pressure.setText(Float.toString(pres_m));
            } else pressure.setText("");

            TextView sunrise = findViewById(R.id.sunrise);
            String timeSunrise = sys.getString("sunrise");
            Locale myLocale = new Locale("ru", "RU");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);

            String dateString = formatter.format(new Date(Long.parseLong(timeSunrise)*1000+(60*60*1000)*3));
            sunrise.setTextColor(Color.parseColor(color));
            sunrise.setText(dateString);

            TextView sunset = findViewById(R.id.sunset);
            String timeSunset = sys.getString("sunset");

            String dateString1 = formatter.format(new Date(Long.parseLong(timeSunset)*1000+(60*60*1000)*3));
            sunset.setTextColor(Color.parseColor(color));
            sunset.setText(dateString1);



            String showText;
            Context context = getApplicationContext();
            if (temp_C<0){
                showText = "Ульяне холодно!";
            } else {
                showText = "Ульяне тепло!";
            }
            Toast toast = Toast.makeText(context, showText,
                    Toast.LENGTH_LONG);
            toast.show();

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}