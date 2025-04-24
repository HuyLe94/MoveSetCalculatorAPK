package com.AloneDev.movesetcalculator;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private JSONObject fullData;
    private JSONArray abilitiesData;
    private JSONArray movesData;
    private JSONArray typesData;
    private ArrayAdapter<String> moveAdapter;
    private AutoCompleteTextView ability;
    private AutoCompleteTextView move1;
    private AutoCompleteTextView move2;
    private AutoCompleteTextView move3;
    private AutoCompleteTextView move4;
    private AutoCompleteTextView type1;
    private AutoCompleteTextView type2;
    private CheckedTextView megaCheck ;
    private CheckedTextView alolanCheck;
    private CheckedTextView hisuianCheck;
    private CheckedTextView paldeanCheck;
    private CheckedTextView galarianCheck;
    private CheckedTextView gmaxCheck;
    private AdView mAdView;
    private AdView mAdView2;
    private ProgressBar downloadProgressBar;

    private ArrayList<CheckedTextView>nameFilter = new ArrayList<>();
    List<HashMap<String, Object>> matchingPokemonList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        downloadProgressBar = findViewById(R.id.downloadProgressBar);
        downloadProgressBar.setVisibility(View.GONE);

        Button updateButton = findViewById(R.id.updateData);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadJsonFiles();
            }
        });

        Button deleteButton = findViewById(R.id.deleteData);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFiles();
            }
        });

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


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest);

        matchingPokemonList = new ArrayList<>();
        megaCheck =findViewById(R.id.megaBox);
        alolanCheck = findViewById(R.id.alolanBox);
        galarianCheck = findViewById(R.id.galarianBox);
        hisuianCheck = findViewById(R.id.hisuianBox);
        paldeanCheck = findViewById(R.id.paldeanBox);
        gmaxCheck = findViewById(R.id.gmaxBox);

        nameFilter.add(megaCheck);
        nameFilter.add(alolanCheck);
        nameFilter.add(galarianCheck);
        nameFilter.add(hisuianCheck);
        nameFilter.add(paldeanCheck);
        nameFilter.add(gmaxCheck);

        //checkAndLoadData();

        String fullPokemonDetails="";
        String uniqueMovesList="";
        String uniqueTypesList="";
        String uniqueAbilitiesList="";

        //Parse JSON string to extract unique "moves" values
        if(!areFilesAvailable()) {
            Toast.makeText(this, "Press Update to get the latest data", Toast.LENGTH_LONG).show();
            fullPokemonDetails = readJsonFileFromRaw("complete_data");
            uniqueMovesList = readJsonFileFromRaw("unique_moves");
            uniqueTypesList = readJsonFileFromRaw("unique_types");
            uniqueAbilitiesList = readJsonFileFromRaw("unique_abilities");
        }
        else {
            fullPokemonDetails = readJsonFile("complete_data.json");
            uniqueMovesList = readJsonFile("unique_moves.json");
            uniqueTypesList = readJsonFile("unique_types.json");
            uniqueAbilitiesList = readJsonFile("unique_abilities.json");
        }

        try {

            fullData = new JSONObject(fullPokemonDetails);
            typesData = new JSONArray(uniqueTypesList);
            movesData = new JSONArray(uniqueMovesList);
            abilitiesData = new JSONArray(uniqueAbilitiesList);

            Set<String> uniqueMoveSuggestions = new HashSet<>(); // Use Set to store unique values
            Set<String> uniqueAbilities = new HashSet<>();
            Set<String> uniqueTypes = new HashSet<>();


            for (int j = 0; j < movesData.length(); j++) {
                uniqueMoveSuggestions.add(movesData.getString(j)); // Add to set to maintain uniqueness
            }
            for (int j = 0; j < typesData.length(); j++) {
                uniqueTypes.add(typesData.getString(j)); // Add to set to maintain uniqueness
            }
            for (int j = 0; j < abilitiesData.length(); j++) {
                uniqueAbilities.add(abilitiesData.getString(j)); // Add to set to maintain uniqueness
            }

            // Convert the Set back to a List for ArrayAdapter
            List<String> moveSuggestions = new ArrayList<>(uniqueMoveSuggestions);
            List<String> abilitySuggestions = new ArrayList<>(uniqueAbilities);
            List<String> typesSuggestions = new ArrayList<>(uniqueTypes);

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

            type1 = findViewById(R.id.type1);
            ArrayAdapter<String> type1List = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typesSuggestions);
            type1.setAdapter(type1List);
            type1.setThreshold(1);

            type2 = findViewById(R.id.type2);
            ArrayAdapter<String> type2List = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typesSuggestions);
            type2.setAdapter(type2List);
            type2.setThreshold(1);


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

            AutoCompleteTextView autoCompleteTextView5 = findViewById(R.id.type1); // Replace 'yourAutoCompleteTextView2' with the ID of the second AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView5);

            AutoCompleteTextView autoCompleteTextView6 = findViewById(R.id.type2); // Replace 'yourAutoCompleteTextView2' with the ID of the second AutoCompleteTextView
            setAutoCompleteClickListener(autoCompleteTextView6);

            for (final CheckedTextView checkedTextView : nameFilter) {
                // Use the CheckedTextView's ID as the key
                final String key = "checkedTextView_" + checkedTextView.getId();

                // Retrieve the current state from SharedPreferences
                final boolean savedState = sharedPreferences.getBoolean(key, false);

                // Set the initial state of CheckedTextView
                checkedTextView.setChecked(savedState);

                // Add an OnClickListener to each CheckedTextView
                checkedTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toggle the state of the CheckedTextView
                        boolean newState = !checkedTextView.isChecked();
                        checkedTextView.setChecked(newState);

                        // Update the state in SharedPreferences with the new state
                        editor.putBoolean(key, newState);
                        editor.apply();
                    }
                });
            }
        } catch (JSONException e) {
            Log.d("Run Error", "1");
            throw new RuntimeException(e);
        }
        }
    @Override
    protected void onResume() {
        super.onResume();
        // Your code to perform actions when the activity is resumed or displayed
        matchingPokemonList = new ArrayList<>();
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
        String enteredType1 = type1.getText().toString().trim();
        String enteredType2 = type2.getText().toString().trim();

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

        try {
            for (Iterator<String> iterator = fullData.keys(); iterator.hasNext(); ) {
                String pokemonName = iterator.next();
                JSONObject pokemon = fullData.getJSONObject(pokemonName);
                JSONArray moves = pokemon.getJSONArray("learnset");
                JSONArray abilities = pokemon.getJSONArray("abilities");
                JSONArray types = pokemon.getJSONArray("types");

                //JSONArray moves = pokemon.getJSONArray("learnset");
                //JSONArray abilities = pokemon.getJSONArray("abilities");
                //JSONArray types = pokemon.getJSONArray("types");
                String pokename = pokemon.getString("name");

                boolean move1Found = false;
                boolean move2Found = false;
                boolean move3Found = false;
                boolean move4Found = false;
                boolean abilityFound = false;
                boolean type1Found = false;
                boolean type2Found = false;


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

                for (int j = 0; j < types.length(); j++) {
                    String type = types.getString(j);

                    if (type.equalsIgnoreCase(enteredType1)) {
                        type1Found = true;
                    }
                    if (type.equalsIgnoreCase(enteredType2)) {
                        type2Found = true;
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
                        (enteredType1.isEmpty() || type1Found) &&
                        (enteredType2.isEmpty() || type2Found) &&
                        (enteredAbility.isEmpty() || abilityFound)) {
                    //String formattedEntry = "Pokedex : " + pokemon.getString("id") + " - " + pokemon.getString("name");
                    //matchingPokemonList.add(formattedEntry);
                    //HashMap<String, Object> pokemonDetails = new HashMap<>();
                    //pokemonDetails.put("id", pokemon.getInt("id"));
                    //String name = pokemon.getString("Name");
                    String name = pokename;

                    boolean namecheck= true;
                    for (final CheckedTextView checkedTextView : nameFilter) {
                        //Log.d("casesearch", "case1");
                        // Check the initial state of the CheckedTextView
                        boolean isChecked = checkedTextView.isChecked();
                        String text = checkedTextView.getText().toString();
                        //Log.d("casesearch", "current form check = "+text);

                        // Perform actions based on the initial state and text
                        if (isChecked && name.contains(text)) {
                            //Log.d("casesearch", "current form check does contain this = "+text);
                            // Perform actions when initially checked and the text matches "YourString"
                            namecheck=false;
                        } else {
                            // Perform other actions if needed
                        }
                    }
                    if (namecheck==false){
                        continue;
                    }


                    int hp = pokemon.getJSONObject("baseStats").getInt("hp");
                    if(getStatCondition(hpConditionStr)!=-1 && getStatCondition(hpConditionStr)>hp){
                        continue;
                    }

                    int attack = pokemon.getJSONObject("baseStats").getInt("atk");
                    if(getStatCondition(atkConditionStr)!=-1 && getStatCondition(atkConditionStr)>attack){
                        continue;
                    }

                    int defense = pokemon.getJSONObject("baseStats").getInt("def");
                    if(getStatCondition(defConditionStr)!=-1 && getStatCondition(defConditionStr)>defense){
                        continue;
                    }

                    int satk = pokemon.getJSONObject("baseStats").getInt("spa");
                    if(getStatCondition(satkConditionStr)!=-1 && getStatCondition(satkConditionStr)>satk){
                        continue;
                    }

                    int sdef = pokemon.getJSONObject("baseStats").getInt("spd");
                    if(getStatCondition(sdefConditionStr)!=-1 && getStatCondition(sdefConditionStr)>sdef){
                        continue;
                    }

                    int speed = pokemon.getJSONObject("baseStats").getInt("spe");
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
            Log.d("Run Error", "2");
            e.printStackTrace();
        }
        return matchingPokemonList;
    }
    private int getStatCondition(String conditionStr) {
        int condition = -1; // Default value if the condition string is empty or not a valid number

        if (!conditionStr.isEmpty()) {
            try {
                condition = Integer.parseInt(conditionStr);
                if (condition < 0) {
                    condition = -1;
                }
            } catch (NumberFormatException e) {
                Log.d("Run Error", "3");
                condition = -1;
            }
        }
        return condition;
    }

    private boolean areFilesAvailable() {

        String[] fileNames = {"complete_data.json", "unique_moves.json", "unique_types.json", "unique_abilities.json"};
        for (String fileName : fileNames) {
            File file = new File(getFilesDir(), fileName);
            if (!file.exists()) {
                return false; // At least one file does not exist
            }
        }
        return true; // All files exist
    }

    private String readJsonFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file = new File(getFilesDir(), fileName);
            if (!file.exists()) {
                Log.e("FileCheck", "File not found when reading: " + file.getAbsolutePath());
                //return null; // Handle accordingly
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d("Run Error", "4");
            Log.e("FileCheck", "File not found: " + fileName, e);
        } catch (IOException e) {
            Log.d("Run Error", "5");
            Log.e("FileCheck", "Error reading file: " + fileName, e);
        }

        return stringBuilder.toString();
    }
    private String readJsonFileFromRaw(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(fileName, "raw", getPackageName()));

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                // Handle file not found or any other issue
            }
        } catch (IOException e) {
            Log.d("Run Error", "6");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // downloadJsonFiles: Downloads all required JSON files in a background thread.
    // Shows a horizontal ProgressBar at the top of the screen indicating per-file progress.
    // Notifies the user with a Toast only after all downloads are finished (success or failure).
    // The ProgressBar is hidden when not downloading.
    private void downloadJsonFiles() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fileNames = {"complete_data", "unique_moves", "unique_types", "unique_abilities"};
        String baseUrl = "https://raw.githubusercontent.com/HuyLe94/Pokemon_Data_Files/main/";

        // Show and reset the ProgressBar
        runOnUiThread(() -> {
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadProgressBar.setMax(fileNames.length);
            downloadProgressBar.setProgress(0);
        });

        Thread thread = new Thread(() -> {
            AtomicBoolean allFilesDownloaded = new AtomicBoolean(true);
            int[] progress = {0};

            for (String fileName : fileNames) {
                String urlString = baseUrl + fileName + ".json";
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }

                        reader.close();
                        inputStream.close();

                        String jsonData = stringBuilder.toString();
                        saveJsonToFile(jsonData, fileName + ".json");

                    } else {
                        allFilesDownloaded.set(false);
                        runOnUiThread(() ->
                                Toast.makeText(this, "Failed to download " + fileName + ".json", Toast.LENGTH_SHORT).show()
                        );
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    allFilesDownloaded.set(false);
                    Log.d("Run Error", "Error downloading " + fileName + ".json", e);
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error downloading " + fileName + ".json", Toast.LENGTH_SHORT).show()
                    );
                }
                // Update progress after each file
                runOnUiThread(() -> {
                    progress[0]++;
                    downloadProgressBar.setProgress(progress[0]);
                });
            }

            // Hide ProgressBar and notify when done
            runOnUiThread(() -> {
                downloadProgressBar.setVisibility(View.GONE);
                if (allFilesDownloaded.get()) {
                    Toast.makeText(this, "All downloads completed. Please restart the app to apply the new data.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Some files failed to download. Please check your connection and try again.", Toast.LENGTH_LONG).show();
                }
            });
        });

        thread.start();
    }
    private void downloadJsonFiles2() {
        Log.d("Download", "Attempting to download: ");
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fileNames = {"complete_data", "unique_moves", "unique_types", "unique_abilities"};
        String baseUrl = "https://raw.githubusercontent.com/HuyLe94/Pokemon_Data_Files/main/";

        Toast.makeText(this, "Starting download...", Toast.LENGTH_SHORT).show();

        for (String fileName : fileNames) {
            String urlString = baseUrl + fileName + ".json";
            Log.d("Download", "Attempting to download: " + urlString);
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d("Download", "Response code for " + fileName + ": " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    reader.close();
                    inputStream.close();

                    String jsonData = stringBuilder.toString();
                    saveJsonToFile(jsonData, fileName + ".json");

                    runOnUiThread(() -> {
                        Toast.makeText(this, fileName + ".json downloaded successfully.", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to download " + fileName + ".json", Toast.LENGTH_SHORT).show();
                    });
                }
                connection.disconnect();
            } catch (IOException e) {
                Log.e("Download", "Error downloading " + fileName + ": " + e.getMessage());
            }
        }

    }



    private void checkAndLoadData() {
        if (!areFilesAvailable()) {
            Log.d("FileCheck", "not exist");
            downloadJsonFiles();
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void saveJsonToFile(String jsonData, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonData.getBytes());

        } catch (IOException e) {
            Log.d("Run Error", "9");
            Log.e("FileCheck", "Error saving file: " + fileName, e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.d("Run Error", "10");
                    Log.e("FileCheck", "Error closing file output stream.", e);
                }
            }
        }
    }

    private void deleteFiles() {

        Log.d("FileDelete", " deleting");
        String[] fileNames = {"complete_data.json", "unique_moves.json", "unique_types.json", "unique_abilities.json"};

        for (String fileName : fileNames) {
            File file = new File(getFilesDir(), fileName);
            if (file.exists()) {
                if (file.delete()) {
                    Log.d("FileDelete", fileName + " deleted successfully.");
                } else {
                    Log.e("FileDelete", "Failed to delete " + fileName);
                }
            } else {
                Log.d("FileDelete", fileName + " does not exist.");
            }
        }

        // Notify user that files have been deleted
        Toast.makeText(this, "Downloaded files deleted.", Toast.LENGTH_SHORT).show();
    }

}
