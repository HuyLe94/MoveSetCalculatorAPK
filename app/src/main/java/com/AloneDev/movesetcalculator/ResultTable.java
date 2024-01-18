package com.AloneDev.movesetcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.HashMap;
import java.util.List;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class ResultTable extends AppCompatActivity {

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_table);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ImageButton goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the behavior when the "Go Back" button is clicked
                goBackToPage1();
            }
        });

        TableLayout tableLayout = findViewById(R.id.tableLayout2);

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
        textView.setSingleLine(true); // Allow single line
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE); // Enable marquee effect for scrolling
        textView.setHorizontallyScrolling(true); // Enable horizontal scrolling
        textView.setPadding(0,0,0,0);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        return textView;
    }
    private void goBackToPage1() {
        Intent intent = new Intent(ResultTable.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity to prevent stacking activities
    }
}







