package com.montreal.wtm.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.montreal.wtm.R;
import com.montreal.wtm.model.Speaker;
import com.montreal.wtm.utils.Utils;

public class SpeakerActivity extends AppCompatActivity {

    private static String EXTRA_SPEAKER = "com.montreal.wtm.speaker";

    public static Intent newIntent(Context context, Speaker speaker) {
        Intent intent = new Intent(context, SpeakerActivity.class);
        intent.putExtra(EXTRA_SPEAKER, speaker);

        return intent;
    }

    private Speaker mSpeaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speaker_activity);

        Intent intent = getIntent();
        mSpeaker = intent.getExtras().getParcelable(EXTRA_SPEAKER);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mSpeaker.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton fab = findViewById(R.id.fab);

        //TODO if firebase with login with save
        //if (DataManager.Companion.getInstance().loveTalkContainSpeaker(mSpeakerKey)) {
        //    fab.setImageResource(R.drawable.ic_favorite_black_24px);
        //} else {
        fab.setImageResource(R.drawable.ic_favorite_white_24px);
        //}

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO SEND TO FIREBASE
                //if (DataManager.Companion.getInstance().loveTalkContainSpeaker(mSpeakerKey)) {
                //    DataManager.Companion.getInstance().removeLoveTalks(mSpeakerKey);
                //    fab.setImageResource(R.drawable.ic_favorite_white_24px);
                //    Snackbar.make(view, R.string.talk_removed, Snackbar.LENGTH_LONG)
                //            .setAction("Action", null).show();
                //} else {
                //    DataManager.Companion.getInstance().addLoveTalk(mSpeakerKey);
                //    fab.setImageResource(R.drawable.ic_favorite_black_24px);
                //    Snackbar.make(view, R.string.talk_added, Snackbar.LENGTH_LONG)
                //            .setAction("Action", null).show();
                //}
            }
        });

        findViewById(R.id.talkInformation).setVisibility(View.GONE);
        ImageView avatarImageView =  findViewById(R.id.avatarImageView);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getString(R.string.storage_url)).append(mSpeaker.getPhotoUrl());

        Utils.downloadImage(stringBuilder.toString(), avatarImageView);

        ((TextView) findViewById(R.id.titleTextView)).setText(
            mSpeaker.getTitle() != null ? Html.fromHtml(mSpeaker.getTitle()) : null);
        ((TextView) findViewById(R.id.descriptionTextView)).setText(
            mSpeaker.getBio() != null ? Html.fromHtml(mSpeaker.getBio()) : null);
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
}
