package ar.com.stokle.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Mariano_Stokle on 13/07/2016.
 * Popular Movies project for Udacity Project 1 - Android Developer Nanodegree
 */

public class PopMoviesActivity extends AppCompatActivity {

    final static int SORT_BY_REQUEST_CODE = 1;
    final static int SORT_BY_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pop_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)  {
            case R.id.action_settings:
            startActivity(new Intent(this,PopMoviesSettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
