package com.example.weather_report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id= item.getItemId();
        if (id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Ульяна одобрила твой выбор",
                    Toast.LENGTH_LONG);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }
}