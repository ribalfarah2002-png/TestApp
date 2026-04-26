package com.example.testapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private EditText etIpAddress;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        tvDisplay = findViewById(R.id.tv_display);
        etIpAddress = findViewById(R.id.et_ip);
        Button btnEnergy = findViewById(R.id.btn_energy);
        Button btnPrice = findViewById(R.id.btn_price);

        // Set button click listeners
        btnEnergy.setOnClickListener(v -> fetchData("energy", " kWh"));
        btnPrice.setOnClickListener(v -> fetchData("cost", " SYP"));
    }

    private void fetchData(String endpoint, String unit) {
        String ip = etIpAddress.getText().toString().trim();
        if (ip.isEmpty()) {
            Toast.makeText(this, "Please enter ESP8266 IP Address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the URL (e.g., http://192.168.1.50/energy)
        String url = "http://" + ip + "/" + endpoint;
        Request request = new Request.Builder().url(url).build();

        tvDisplay.setText("Fetching data...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvDisplay.setText("Error: Could not connect to ESP8266"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    runOnUiThread(() -> tvDisplay.setText(result + unit));
                } else {
                    runOnUiThread(() -> tvDisplay.setText("Error: Server responded with " + response.code()));
                }
            }
        });
    }
}
