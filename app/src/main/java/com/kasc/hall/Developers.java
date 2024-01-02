package com.kasc.hall;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Developers extends AppCompatActivity {

    private Uri uri;
    private ImageView gmail;
    private CircleImageView linkedin, github;
    private Intent intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);
        toolbar = findViewById(R.id.appbarDEV);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Developers");

        gmail = findViewById(R.id.gmail);
        linkedin = findViewById(R.id.linkedin);
        github = findViewById(R.id.github);

        // Replace the following URLs with your own social media profile links
        String gmailLink = "mailto:supreeth2@gmail.com";
        String linkedinLink = "https://www.linkedin.com/in/supreeth2322/";
        String githubLink = "https://github.com/supreeth2322";

        // Set click listeners for the buttons
        gmail.setOnClickListener(view -> openLink(gmailLink));
        linkedin.setOnClickListener(view -> openLink(linkedinLink));
        github.setOnClickListener(view -> openLink(githubLink));
    }

    private void openLink(String url) {
        uri = Uri.parse(url);
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
