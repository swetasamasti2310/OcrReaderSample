package com.example.ocrreadersample;

import java.util.List;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.natural_language_understanding.v1.model.Features;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String watsonApiKey = "Enter Your Watson Api Key";
    private static final String watsonUrl = "Enter Your Watson Url";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button readText = findViewById(R.id.read_txt_btn);

        readText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                startActivity(intent);
            }
        });

        final EditText dataToBeParsed = findViewById(R.id.enter_data);

        Button parseText = findViewById(R.id.parse_txt_btn);

        parseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        parseDataUsingWatson(String.valueOf(dataToBeParsed.getText()));
                    }
                }).start();
            }
        });

    }

    private void parseDataUsingWatson(String data)
    {
        IamOptions options = new IamOptions.Builder().apiKey(watsonApiKey).build();

        NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding("2019-07-12",
                options);
        naturalLanguageUnderstanding.setEndPoint(watsonUrl);

        EntitiesOptions entities = new EntitiesOptions.Builder().build();

        Features features = new Features.Builder()
            .entities(entities)
            .build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
            .text(data)
            .features(features)
            .build();

        AnalysisResults response = naturalLanguageUnderstanding
            .analyze(parameters)
            .execute()
            .getResult();

        Log.i(LOG_TAG, "Response: " + response.getLanguage());

        List<EntitiesResult> entitiesResultList = response.getEntities();
        Log.i(LOG_TAG, "Entity result size: " + entitiesResultList.size());
        for (EntitiesResult entitiesResult : entitiesResultList) {
            Log.i(LOG_TAG, "Data: " + entitiesResult.getText());
            Log.i(LOG_TAG, "Type: " + entitiesResult.getType());
        }
    }
}
