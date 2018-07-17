package com.example.android.baking;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Step;
import com.example.android.baking.utils.RecipeJsonConstants;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class StepDetailActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    private int stepID;
    private int recipeID;
    private AppDatabase mDb;
    private TextView description;
    private TextView videoNotAvailable;
    private Button mButtonNext;
    private Button mButtonPrev;
    private List<Step> stepsLoaded;
    SimpleExoPlayer exoPlayer;
    SimpleExoPlayerView exoPlayerView;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail);
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState == null) {
            Intent detailsIntent = getIntent();
            recipeID = detailsIntent.getIntExtra(RecipeJsonConstants.RECIPE, 0);
            stepID = detailsIntent.getIntExtra("selectedStep", 0);
        } else {
            recipeID = savedInstanceState.getInt(RecipeJsonConstants.RECIPE);
            stepID = savedInstanceState.getInt("selectedStep");
        }
        videoNotAvailable = findViewById(R.id.noVideoTextView);
        description = findViewById(R.id.instructionText);
        exoPlayerView = findViewById(R.id.playerView);
        mButtonNext = findViewById(R.id.buttonNext);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stepID += 1;
                UpdateUI(stepsLoaded);
            }
        });
        mButtonPrev = findViewById(R.id.buttonPrevious);
        mButtonPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stepID -= 1;
                UpdateUI(stepsLoaded);
            }
        });
        new LoadStep().execute(recipeID);
    }

    private void UpdateUI(List<Step> steps) {
        mButtonPrev.setEnabled(true);
        mButtonNext.setEnabled(true);
        releasePlayer();
        for (Step step: steps) {
            if (step.getStepId().equals(String.valueOf(stepID))) {
                description.setText(step.getDescription());
                if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
                    initializeMediaSession();
                    initializePlayer(Uri.parse(step.getVideoURL()));
                    exoPlayerView.setVisibility(View.VISIBLE);
                    videoNotAvailable.setVisibility(View.GONE);
                } else {
                    exoPlayerView.setVisibility(View.INVISIBLE);
                    videoNotAvailable.setVisibility(View.VISIBLE);
                }
                if (Integer.parseInt(step.getStepId()) == 0)
                    mButtonPrev.setEnabled(false);
                if (Integer.parseInt(step.getStepId()) == (steps.size() - 1))
                    mButtonNext.setEnabled(false);
                UpdateUIOrientation();
                return;
            }
        }
        description.setText(String.valueOf(stepID) + " - " + getString(R.string.missedInstruction));
    }

    private void UpdateUIOrientation() {
        if ( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mButtonNext.setVisibility(View.GONE);
            mButtonPrev.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            videoNotAvailable.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            videoNotAvailable.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            exoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            exoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RecipeJsonConstants.RECIPE, recipeID);
        outState.putInt("selectedStep", stepID);
    }
    private void initializeMediaSession() {

        mediaSession = new MediaSessionCompat(this, "Steps");

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                exoPlayer.setPlayWhenReady(true);
            }

            @Override
            public void onPause() {
                exoPlayer.setPlayWhenReady(false);
            }

            @Override
            public void onSkipToPrevious() {
                exoPlayer.seekTo(0);
            }
        });
        mediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(this, "StepVideo");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }

        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    public class LoadStep extends AsyncTask<Integer, Void, List<Step>> {

        @Override
        protected List<Step> doInBackground(Integer... params) {
            int recipeID = params[0];
            List<Step> stepsData;
            stepsData = mDb.stepsDao().retrieveSteps(recipeID);

            return stepsData;
        }

        @Override
        protected void onPostExecute(List<Step> steps) {
            super.onPostExecute(steps);
            if (steps != null) {
                stepsLoaded = steps;
                UpdateUI(steps);
            } else {
                Toast.makeText(StepDetailActivity.this, getString(R.string.queryFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
