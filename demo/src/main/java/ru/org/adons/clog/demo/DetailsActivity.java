package ru.org.adons.clog.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView tv = (TextView) findViewById(android.R.id.text1);
        tv.setText(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));
    }
}
