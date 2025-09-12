package com.example.myapplication2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText ageEditText, feetEditText, inchesEditText, weightEditText;
    TextView resultTextView;
    RadioButton radioMale, radioFemale;
    Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazar vistas con sus IDs
        ageEditText = findViewById(R.id.age);
        feetEditText = findViewById(R.id.feet);
        inchesEditText = findViewById(R.id.inches);
        weightEditText = findViewById(R.id.Wheight);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        calculateButton = findViewById(R.id.buttonCalculate);
        resultTextView = findViewById(R.id.resultTextView);


        // Lógica del botón
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        try {
            // Leer datos
            int age = Integer.parseInt(ageEditText.getText().toString());
            int feet = Integer.parseInt(feetEditText.getText().toString());
            int inches = Integer.parseInt(inchesEditText.getText().toString());
            float weightKg = Float.parseFloat(weightEditText.getText().toString());

            // Convertir altura a metros
            int totalInches = (feet * 12) + inches;
            float heightMeters = (float) (totalInches * 0.0254);

            // Calcular BMI
            float bmi = weightKg / (heightMeters * heightMeters);

            // Determinar género
            String gender = radioMale.isChecked() ? "Masculino" : radioFemale.isChecked() ? "Femenino" : "No especificado";

            // Clasificación del BMI (opcional)
            String bmiCategory;
            if (bmi < 18.5) {
                bmiCategory = "Bajo peso";
            } else if (bmi < 24.9) {
                bmiCategory = "Peso normal";
            } else if (bmi < 29.9) {
                bmiCategory = "Sobrepeso";
            } else {
                bmiCategory = "Obesidad";
            }

            // Mostrar resultado en pantalla
            String message = "Género: " + gender +
                    "\nEdad: " + age +
                    "\nBMI: " + String.format("%.2f", bmi) +
                    "\nCategoría: " + bmiCategory;

            resultTextView.setText(message);

        } catch (NumberFormatException e) {
            resultTextView.setText("Por favor, completa todos los campos correctamente.");
        }
    }
}
