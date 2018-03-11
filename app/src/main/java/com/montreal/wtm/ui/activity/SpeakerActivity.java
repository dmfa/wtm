package com.montreal.wtm.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import com.google.firebase.auth.FirebaseAuth;
import kotlin.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseWrapper;
import com.montreal.wtm.R;
import com.montreal.wtm.api.FirebaseData;
import com.montreal.wtm.model.Session;
import com.montreal.wtm.model.Speaker;
import com.montreal.wtm.utils.Utils;
import com.montreal.wtm.utils.ui.activity.BaseActivity;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

public class SpeakerActivity extends BaseActivity implements View.OnClickListener {

    private static final String EXTRA_SPEAKER = "com.montreal.wtm.speaker";
    protected FloatingActionButton fab;

    public static Intent newIntent(Context context, Speaker speaker) {
        Intent intent = new Intent(context, SpeakerActivity.class);
        intent.putExtra(EXTRA_SPEAKER, speaker);

        return intent;
    }

    private Speaker speaker;
    private boolean sessionSaved;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speaker_activity);

        Intent intent = getIntent();
        speaker = intent.getExtras().getParcelable(EXTRA_SPEAKER);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(speaker.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_favorite_white_24px);

        fab.setOnClickListener(this);

        ImageView avatarImageView = findViewById(R.id.avatarImageView);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getString(R.string.storage_url)).append(speaker.getPhotoUrl());

        Utils.downloadImage(stringBuilder.toString(), avatarImageView);

        ((TextView) findViewById(R.id.speaker_position)).setText(
            speaker.getTitle() != null ? Html.fromHtml(speaker.getTitle()) : null);
        ((TextView) findViewById(R.id.speaker_bio)).setText(
            speaker.getBio() != null ? Html.fromHtml(speaker.getBio()) : null);

        FirebaseData.INSTANCE.getMySessionState(this, speakerListener, speaker.getSessionId());

        getLoginChanged().subscribe(this::onLoginChanged);
    }

    private void onLoginChanged(Boolean loggedIn) {
        this.loggedIn = loggedIn;
        if(loggedIn) {
            FirebaseData.INSTANCE.getMySessionState(this, speakerListener, speaker.getSessionId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (FirebaseWrapper.Companion.isLogged()) {
            sessionSaved = !sessionSaved;
            FirebaseData.INSTANCE.saveSession(speaker.getSessionId(), sessionSaved);
            int drawableId, messageId;

            if (sessionSaved) {
                drawableId = R.drawable.ic_favorite_black_24px;
                messageId = R.string.speaker_added;
            } else {
                drawableId = R.drawable.ic_favorite_white_24px;
                messageId = R.string.speaker_removed;
            }
            fab.setImageResource(drawableId);
            Snackbar.make(view, messageId, Snackbar.LENGTH_LONG).show();
        } else {
            promptLogin();
        }
    }


    private FirebaseData.RequestListener<Pair<String, Boolean>> speakerListener =
        new FirebaseData.RequestListener<Pair<String, Boolean>>() {
            @Override
            public void onDataChange(Pair<String, Boolean> sessionState) {
                sessionSaved = sessionState.getSecond();
                int resource = R.drawable.ic_favorite_white_24px;
                if (FirebaseWrapper.Companion.isLogged() && sessionSaved) {
                    resource = R.drawable.ic_favorite_black_24px;
                }
                fab.setImageResource(resource);
            }

            @Override
            public void onCancelled(@NotNull FirebaseData.ErrorFirebase errorType) {

            }
        };
}
