package com.example.movesetcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_table);

        TableLayout tableLayout = findViewById(R.id.tableLayout);


        List<HashMap<String, Object>> pokemonDetailsList =
                (List<HashMap<String, Object>>) getIntent().getSerializableExtra("matchingPokemonList");

        // Populate your table dynamically with the matching Pokémon data
        if (pokemonDetailsList != null && !pokemonDetailsList.isEmpty()) {


            for (HashMap<String, Object> pokemonDetails : pokemonDetailsList) {
                addTableRow(tableLayout, pokemonDetails);
            }
        }
    }

    private void addTableRow(TableLayout tableLayout, HashMap<String, Object> pokemonDetailsList) {
        TableRow tableRow = new TableRow(this);

        TextView nameHeader = findViewById(R.id.nameCol); // Replace with your actual header IDs
        TextView hpHeader = findViewById(R.id.hpCol);
        TextView attackHeader = findViewById(R.id.atkCol);
        TextView defenseHeader = findViewById(R.id.defCol); // Replace with your actual header IDs
        TextView satkHeader = findViewById(R.id.satkCol);
        TextView sdefHeader = findViewById(R.id.sdefCol);
        TextView speedHeader = findViewById(R.id.speedCol);

        // Example: Add TextViews to display Pokemon details in each row
        TextView nameTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("name")), nameHeader);
        tableRow.addView(nameTextView);

        TextView hpTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("hp")), hpHeader);
        tableRow.addView(hpTextView);

        TextView attackTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("attack")), attackHeader);
        tableRow.addView(attackTextView);

        TextView defenseTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("defense")), defenseHeader);
        tableRow.addView(defenseTextView);

        TextView satkTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("satk")), satkHeader);
        tableRow.addView(satkTextView);

        TextView sdefTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("sdef")), sdefHeader);
        tableRow.addView(sdefTextView);

        TextView speedTextView = createNewTextView(String.valueOf(pokemonDetailsList.get("speed")), speedHeader);
        tableRow.addView(speedTextView);


        // Add the TableRow to the TableLayout
        tableLayout.addView(tableRow);
    }

    private TextView createNewTextView(String text, TextView headerView) {
        TextView textView = new TextView(this);
        textView.setText(text);

        TableRow.LayoutParams params = (TableRow.LayoutParams) headerView.getLayoutParams();
        textView.setLayoutParams(new TableRow.LayoutParams(params.width, params.height, params.weight));
        textView.setPadding(headerView.getPaddingLeft(), headerView.getPaddingTop(), headerView.getPaddingRight(), headerView.getPaddingBottom());
        textView.setTextColor(headerView.getCurrentTextColor());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerView.getTextSize());
        // Apply other styling or properties as needed

        return textView;
    }
}







