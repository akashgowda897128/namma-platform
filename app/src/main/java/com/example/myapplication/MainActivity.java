package com.example.myapplication;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView currentStationSearch;
    private RecyclerView trainRecyclerView;
    private TrainAdapter trainAdapter;
    private final List<TrainModel> trainList = new ArrayList<>();
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentStationSearch = findViewById(R.id.currentStationSearch);
        trainRecyclerView = findViewById(R.id.trainRecyclerView);

        setupStationSearch();
        setupRecyclerView();
        setupTTS();
    }

    private void setupStationSearch() {
        List<String> stations = loadStationsFromJson();
        if (stations == null) stations = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stations);
        currentStationSearch.setAdapter(adapter);

        currentStationSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStation = (String) parent.getItemAtPosition(position);
            loadTrainsAtStation(selectedStation);
        });
    }

    private List<String> loadStationsFromJson() {
        String json = loadJSONFromAsset("stations.json");
        if (json == null) return null;
        Type type = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    private void setupRecyclerView() {
        trainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainAdapter = new TrainAdapter(trainList, this::speakTrainAnnouncement);
        trainRecyclerView.setAdapter(trainAdapter);
    }

    private void speakTrainAnnouncement(TrainModel train) {
        String originalName = train.getTrainName();
        String trainNumber = "";
        String trainNameOnly = originalName;

        // Extract 5-digit train number from "(12345)"
        if (originalName.contains("(") && originalName.contains(")")) {
            trainNumber = originalName.substring(originalName.indexOf("(") + 1, originalName.indexOf(")"));
            trainNameOnly = originalName.substring(0, originalName.indexOf("(")).trim();
        }

        StringBuilder kannadaDigits = new StringBuilder();
        for (char c : trainNumber.toCharArray()) {
            if (Character.isDigit(c)) {
                kannadaDigits.append(getKannadaDigitWord(c)).append(" ");
            }
        }

        String platformForSpeak = train.getPlatform().replace("PF", "Platform");
        
        // Structure: [Train Name] [Digit by Digit Number] ರೈಲು [Platform] ಗೆ [Time] ಗಂಟೆಗೆ ಬರಲಿದೆ.
        String announcement = trainNameOnly + " " + kannadaDigits.toString().trim() + 
                " ರೈಲು " + platformForSpeak + " ಗೆ " + 
                train.getArrivalTime() + " ಗಂಟೆಗೆ ಬರಲಿದೆ.";

        Log.d("TTS_ANNOUNCE", announcement);
        tts.speak(announcement, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private String getKannadaDigitWord(char digit) {
        switch (digit) {
            case '0': return "ಸೊನ್ನೆ";
            case '1': return "ಒಂದು";
            case '2': return "ಎರಡು";
            case '3': return "ಮೂರು";
            case '4': return "ನಾಲ್ಕು";
            case '5': return "ಐದು";
            case '6': return "ಆರು";
            case '7': return "ಏಳು";
            case '8': return "ಎಂಟು";
            case '9': return "ಒಂಬತ್ತು";
            default: return "";
        }
    }

    private void loadTrainsAtStation(String stationName) {
        // Extract Station Code (e.g., "SBC" from "KSR Bengaluru City (SBC)")
        String stationCode = "";
        if (stationName.contains("(") && stationName.contains(")")) {
            stationCode = stationName.substring(stationName.indexOf("(") + 1, stationName.indexOf(")"));
        }

        if (stationCode.isEmpty()) {
            loadFromLocalFallback(stationName);
            return;
        }

        // ACTUAL API CALL using your RapidAPI Key
        ApiService apiService = RetrofitClient.getApiService();
        String apiKey = "727fe39ff8msh89b3c2247e1235cp1ce625jsnc1317d049a94";
        String apiHost = "irctc-indian-railway-pnr-status.p.rapidapi.com";

        apiService.getTrains(apiKey, apiHost, stationCode)
                .enqueue(new retrofit2.Callback<List<TrainModel>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<TrainModel>> call, retrofit2.Response<List<TrainModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            trainList.clear();
                            trainList.addAll(response.body());
                            trainAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Live API Data Loaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("API_ERROR", "Response failed, code: " + response.code());
                            loadFromLocalFallback(stationName);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<TrainModel>> call, Throwable t) {
                        Log.e("API_FAILURE", t.getMessage());
                        loadFromLocalFallback(stationName);
                    }
                });
    }

    private void loadFromLocalFallback(String stationName) {
        String json = loadJSONFromAsset("trains.json");
        if (json == null) return;

        Type type = new TypeToken<List<TrainModel>>() {}.getType();
        List<TrainModel> allTrains = new Gson().fromJson(json, type);

        trainList.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        Calendar now = Calendar.getInstance();

        if (allTrains != null) {
            for (TrainModel train : allTrains) {
                // Check if selected station is source, destination, or a stop
                boolean matchesStation = train.getSource().equalsIgnoreCase(stationName) ||
                        train.getDestination().equalsIgnoreCase(stationName);

                if (!matchesStation && train.getStops() != null) {
                    for (String stop : train.getStops()) {
                        if (stop.equalsIgnoreCase(stationName)) {
                            matchesStation = true;
                            break;
                        }
                    }
                }

                if (matchesStation) {
                    trainList.add(train);
                    if (trainList.size() >= 3) break; // Focus on next 3 trains
                }
            }
        }

        if (trainList.isEmpty()) {
            Toast.makeText(this, "No upcoming trains / ಯಾವುದೇ ಮುಂಬರುವ ರೈಲುಗಳಿಲ್ಲ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Offline Data Loaded for " + stationName, Toast.LENGTH_SHORT).show();
        }
        trainAdapter.notifyDataSetChanged();
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();
            if (read > 0) {
                json = new String(buffer, StandardCharsets.UTF_8);
            } else {
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("kn", "IN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Kannada language not supported");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    private void speakAnnouncement() {
        if (trainList.isEmpty()) {
            tts.speak("ದಯವಿಟ್ಟು ಮೊದಲು ರೈಲು ನಿಲ್ದಾಣವನ್ನು ಆರಿಸಿ", TextToSpeech.QUEUE_FLUSH, null, null);
            return;
        }

        TrainModel firstTrain = trainList.get(0);
        String announcement = firstTrain.getTrainName() + " ರೈಲು " + firstTrain.getPlatform() + " ಗೆ " + 
                firstTrain.getArrivalTime() + " ಗಂಟೆಗೆ ಬರಲಿದೆ.";
        tts.speak(announcement, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
