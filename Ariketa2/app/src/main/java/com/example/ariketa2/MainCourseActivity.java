package com.example.ariketa2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_course);

        ListView lista = findViewById(R.id.mainCourseList);
        Plato[] platos = {
                new Plato("Melon and sesame salad","Melon and sesame salad",10),
                new Plato("Veal and potato stew","Veal and potato stew",9),
                new Plato("Spring onion and cheddar fritters","Spring onion and cheddar fritters",8),
                new Plato("Veal and peppercorn dumplings","Veal and peppercorn dumplings",11),
                new Plato("Potato and kumquat soup","Potato and kumquat soup",13)

        };

        ArrayAdapter<Plato> platosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,platos);
        lista.setAdapter(platosAdapter);

    }
}