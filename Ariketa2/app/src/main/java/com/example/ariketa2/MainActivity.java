package com.example.ariketa2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    CardView startersCard;
    CardView mainCard;

    CardView desertCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startersCard = findViewById(R.id.card_view_starters);
        mainCard = findViewById(R.id.card_view_mains);
        desertCard = findViewById(R.id.card_view_deserts);

        startersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startersActivityIntent = new Intent(MainActivity.this, StartersActivity.class);

                startActivity(startersActivityIntent);
            }
        });

        mainCard.setOnClickListener(v -> onClickMain());
        desertCard.setOnClickListener(v -> onClickDesert());





    }

    public void onClickMain() {
        Intent mainCourseActivityIntent = new Intent(MainActivity.this, MainCourseActivity.class);

        startActivity(mainCourseActivityIntent);

    }

    public void onClickDesert() {
        Intent desertActivityIntent = new Intent(MainActivity.this, DesertsActivity.class);

        startActivity(desertActivityIntent);

    }
}