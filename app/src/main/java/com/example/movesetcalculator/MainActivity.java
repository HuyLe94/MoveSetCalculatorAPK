package com.example.movesetcalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private JSONArray jsonArray;
    private ArrayAdapter<String> moveAdapter;
    private AutoCompleteTextView ability;
    private AutoCompleteTextView move1;
    private AutoCompleteTextView move2;
    private AutoCompleteTextView move3;
    private AutoCompleteTextView move4;


    List<HashMap<String, Object>> matchingPokemonList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View rootView = findViewById(android.R.id.content);
        InputStream inputStream = getResources().openRawResource(R.raw.alldata);
        // Replace "your_json_file" with the actual name of your JSON file without the file extension

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();

            // Parse JSON string to extract unique "moves" values
            jsonArray = new JSONArray(jsonString);
            Set<String> uniqueMoveSuggestions = new HashSet<>(); // Use Set to store unique values
            Set<String> uniqueAbilities = new HashSet<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray movesArray = jsonObject.getJSONArray("moves");

                for (int j = 0; j < movesArray.length(); j++) {
                    uniqueMoveSuggestions.add(movesArray.getString(j)); // Add to set to maintain uniqueness
                }
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray movesArray = jsonObject.getJSONArray("abilities");

                for (int j = 0; j < movesArray.length(); j++) {
                    uniqueAbilities.add(movesArray.getString(j)); // Add to set to maintain uniqueness
                }
            }

            // Convert the Set back to a List for ArrayAdapter
            List<String> moveSuggestions = new ArrayList<>(uniqueMoveSuggestions);
            List<String> abilitySuggestions = new ArrayList<>(uniqueAbilities);

            // Sort the list alphabetically
            Collections.sort(moveSuggestions);

            // Set suggestions for AutoCompleteTextView
            move1 = findViewById(R.id.FillMove1);
            ArrayAdapter<String> move1list = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, moveSuggestions);
            move1.setAdapter(move1list);
            move1.setThreshold(1);

            move2 = findViewById(R.id.FillMove2);
            ArrayAdapter<String> move2list = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, moveSuggestions);
            move2.setAdapter(move2list);
            move2.setThreshold(1);

            move3 = findViewById(R.id.FillMove3);
            ArrayAdapter<String> move3list = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, moveSuggestions);
            move3.setAdapter(move3list);
            move3.setThreshold(1);

            move4 = findViewById(R.id.FillMove4);
            ArrayAdapter<String> move4list = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, moveSuggestions);
            move4.setAdapter(move4list);
            move4.setThreshold(1);

            // Set suggestions for AutoCompleteTextView for abilities
            ability = findViewById(R.id.FillAbility);
            ArrayAdapter<String> abilitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, abilitySuggestions);
            ability.setAdapter(abilitiesAdapter);
            ability.setThreshold(1);

            Button startSearchButton = findViewById(R.id.startSearch);
            startSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateList();

                    // Create an Intent to start Page2Activity
                    Intent intent = new Intent(MainActivity.this, ResultTable.class);
                    intent.putExtra("matchingPokemonList", (Serializable) matchingPokemonList);

                    // Start Page2Activity
                    startActivity(intent);
                    }
            });

            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.FillAbility); // Replace 'yourAutoCompleteTextView1' with the ID of the first AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView);

            AutoCompleteTextView autoCompleteTextView1 = findViewById(R.id.FillMove1); // Replace 'yourAutoCompleteTextView2' with the ID of the second AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView1);

            AutoCompleteTextView autoCompleteTextView2 = findViewById(R.id.FillMove2); // Replace 'yourAutoCompleteTextView2' with the ID of the second AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView2);

            AutoCompleteTextView autoCompleteTextView3 = findViewById(R.id.FillMove3); // Replace 'yourAutoCompleteTextView2' with the ID of the second AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView3);

            AutoCompleteTextView autoCompleteTextView4 = findViewById(R.id.FillMove4); // Replace 'yourAutoCompleteTextView2' with the ID of the second AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView4);


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAutoCompleteClickListener(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteTextView.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
            }
        });
    }
    private List<HashMap<String, Object>> updateList() {
        String enteredMove1 = move1.getText().toString().trim();
        String enteredMove2 = move2.getText().toString().trim();
        String enteredMove3 = move3.getText().toString().trim();
        String enteredMove4 = move4.getText().toString().trim();
        String enteredAbility = ability.getText().toString().trim();

        EditText hpNumberEditText = findViewById(R.id.hpNumber);
        String hpConditionStr = hpNumberEditText.getText().toString().trim();

        EditText atkNumberEditText = findViewById(R.id.atkNumber);
        String atkConditionStr = atkNumberEditText.getText().toString().trim();

        EditText defNumberEditText = findViewById(R.id.defNumber);
        String defConditionStr = defNumberEditText.getText().toString().trim();

        EditText satkNumberEditText = findViewById(R.id.satkNumber);
        String satkConditionStr = satkNumberEditText.getText().toString().trim();

        EditText sdefNumberEditText = findViewById(R.id.sdefNumber);
        String sdefConditionStr = sdefNumberEditText.getText().toString().trim();

        EditText speedNumberEditText = findViewById(R.id.speedNumber);
        String speedConditionStr = speedNumberEditText.getText().toString().trim();

        boolean meetAllConditions = true;






        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pokemon = jsonArray.getJSONObject(i);
                JSONArray moves = pokemon.getJSONArray("moves");
                JSONArray abilities = pokemon.getJSONArray("abilities");

                boolean move1Found = false;
                boolean move2Found = false;
                boolean move3Found = false;
                boolean move4Found = false;
                boolean abilityFound = false;


                for (int j = 0; j < moves.length(); j++) {
                    String move = moves.getString(j);

                    if (move.equalsIgnoreCase(enteredMove1)) {
                        move1Found = true;
                    }
                    if (move.equalsIgnoreCase(enteredMove2)) {
                        move2Found = true;
                    }
                    if (move.equalsIgnoreCase(enteredMove3)) {
                        move3Found = true;
                    }
                    if (move.equalsIgnoreCase(enteredMove4)) {
                        move4Found = true;
                    }
                }
                for (int j = 0; j < abilities.length(); j++) {
                    String ability = abilities.getString(j);

                    if (ability.equalsIgnoreCase(enteredAbility)) {
                        abilityFound = true;
                        break; // No need to continue checking if the ability is found
                    }
                }
                // Check if all entered moves and the ability are found
                if ((enteredMove1.isEmpty() || move1Found) &&
                        (enteredMove2.isEmpty() || move2Found) &&
                        (enteredMove3.isEmpty() || move3Found) &&
                        (enteredMove4.isEmpty() || move4Found) &&
                        (enteredAbility.isEmpty() || abilityFound)) {
                    //String formattedEntry = "Pokedex : " + pokemon.getString("id") + " - " + pokemon.getString("name");
                    //matchingPokemonList.add(formattedEntry);
                    //HashMap<String, Object> pokemonDetails = new HashMap<>();
                    //pokemonDetails.put("id", pokemon.getInt("id"));
                    String name = pokemon.getString("name");


                    int hp = pokemon.getJSONObject("stats").getInt("HP");
                    if(getStatCondition(hpConditionStr)!=-1 && getStatCondition(hpConditionStr)>hp){
                        continue;
                    }

                    int attack = pokemon.getJSONObject("stats").getInt("Attack");
                    if(getStatCondition(atkConditionStr)!=-1 && getStatCondition(atkConditionStr)>attack){
                        continue;
                    }

                    int defense = pokemon.getJSONObject("stats").getInt("Defense");
                    if(getStatCondition(defConditionStr)!=-1 && getStatCondition(defConditionStr)>defense){
                        continue;
                    }

                    int satk = pokemon.getJSONObject("stats").getInt("Sp. Atk");
                    if(getStatCondition(satkConditionStr)!=-1 && getStatCondition(satkConditionStr)>satk){
                        continue;
                    }

                    int sdef = pokemon.getJSONObject("stats").getInt("Sp. Def");
                    if(getStatCondition(sdefConditionStr)!=-1 && getStatCondition(sdefConditionStr)>sdef){
                        continue;
                    }

                    int speed = pokemon.getJSONObject("stats").getInt("Speed");
                    if(getStatCondition(speedConditionStr)!=-1 && getStatCondition(speedConditionStr)>speed){
                        continue;
                    }




                    HashMap<String, Object> pokemonDetails = new HashMap<>();
                    pokemonDetails.put("name", name);
                    pokemonDetails.put("hp", hp);
                    pokemonDetails.put("attack", attack);
                    pokemonDetails.put("defense", defense);
                    pokemonDetails.put("satk", satk);
                    pokemonDetails.put("sdef", sdef);
                    pokemonDetails.put("speed", speed);

                    // Add the Pokemon details HashMap to the list
                    matchingPokemonList.add(pokemonDetails);
                }
            }


            /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, matchingPokemonList);
            ListView resultListView = findViewById(R.id.resultList);
            resultListView.setAdapter(adapter);*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return matchingPokemonList;
    }

    private int getStatCondition(String conditionStr) {
        if (!conditionStr.isEmpty()) {
            return Integer.parseInt(conditionStr);
        }
        return -1; // Return a default value (0) if the condition string is empty
    }

}










