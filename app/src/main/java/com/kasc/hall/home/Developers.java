package com.kasc.hall.home;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kasc.hall.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Developers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        Toolbar toolbar = findViewById(R.id.appbarDEV);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ImageView gmail = findViewById(R.id.gmail);
        CircleImageView linkedin = findViewById(R.id.linkedin);
        CircleImageView github = findViewById(R.id.github);

        //  social media profile links
        String gmailLink = "mailto:supreethvennila@gmail.com";
        String linkedinLink = "https://www.linkedin.com/in/supreeth-ragavendra-s-203144244?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app";
        String githubLink = "https://github.com/SupreethRagavendra";

        // Set click listeners  the buttons
        gmail.setOnClickListener(view -> openLink(gmailLink));
        linkedin.setOnClickListener(view -> openLink(linkedinLink));
        github.setOnClickListener(view -> openLink(githubLink));
    }

    private void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
