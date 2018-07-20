package com.example.android.baking;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Recipe;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity implements ExoPlayer.EventListener{

    Recipe recipe;
    private RecyclerView stepsReciclerView;
    private LinearLayoutManager stepLayoutManager;
    private StepRecycleViewAdapter stepRecycleViewAdapter;
    private AppDatabase mDb;
    public static boolean isTablet = false;
    private TextView tabletDescription;
    private TextView tabletVideoNotAvailable;
    private SimpleExoPlayerView exoPlayerViewTablet;
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private String ingredientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        mDb = AppDatabase.getInstance(getApplicationContext());
        if (savedInstanceState == null) {
            Intent detailsIntent = getIntent();
            recipe = detailsIntent.getParcelableExtra("recipe");
        } else {
            recipe = savedInstanceState.getParcelable(RecipeJsonConstants.RECIPE);
        }

        if (findViewById(R.id.layout_recipe_tablet) != null) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isTablet = true;
            tabletDescription = findViewById(R.id.instructionTextTablet);
            tabletVideoNotAvailable = findViewById(R.id.noVideoTextViewTablet);
            exoPlayerViewTablet = findViewById(R.id.playerViewTablet);
        }

        Toolbar recipeToolbar = findViewById(R.id.recipe_toolbar);
        recipeToolbar.setTitle(recipe.getTitle());
        setSupportActionBar(recipeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CardView ingredientsCardView;
        ingredientsCardView = findViewById(R.id.ingredients_card);
        ingredientsCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentShowIngredients = new Intent(RecipeDetailsActivity.this, IngredientsActivity.class);
                intentShowIngredients.putExtra("recipe", recipe);
                startActivity(intentShowIngredients);
            }
        });

        stepsReciclerView = findViewById(R.id.steps_recycled_view);
        stepsReciclerView.setHasFixedSize(true);
        stepLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stepsReciclerView.setLayoutManager(stepLayoutManager);
        stepsReciclerView.setItemAnimator(new DefaultItemAnimator());

        new RequestSteps().execute(recipe.getId());

        stepRecycleViewAdapter = new StepRecycleViewAdapter(this);
        stepsReciclerView.setAdapter(stepRecycleViewAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RecipeJsonConstants.RECIPE, recipe);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_menu_favorite) {
            new RequestIngredients().execute(recipe.getId());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnStepClickedOnTablet(Step step) {
        releasePlayer();
        tabletDescription.setText(step.getDescription());
        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            initializeMediaSessionTablet();
            initializePlayerTablet(Uri.parse(step.getVideoURL()));
            exoPlayerViewTablet.setVisibility(View.VISIBLE);
            tabletVideoNotAvailable.setVisibility(View.GONE);
        } else if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()){
            initializeMediaSessionTablet();
            initializePlayerTablet(Uri.parse(step.getThumbnailURL()));
            exoPlayerViewTablet.setVisibility(View.INVISIBLE);
            tabletVideoNotAvailable.setVisibility(View.INVISIBLE);
        } else {
            exoPlayerViewTablet.setVisibility(View.INVISIBLE);
            tabletVideoNotAvailable.setVisibility(View.VISIBLE);
        }
    }

    public void initializeMediaSessionTablet() {

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

    public void initializePlayerTablet(Uri mediaUri) {
        if (exoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            exoPlayerViewTablet.setPlayer(exoPlayer);
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

    public class RequestSteps extends AsyncTask<Integer, Void, List<Step>> {

        @Override
        protected List<Step> doInBackground(Integer... params) {
            int recipeID = params[0];
            List<Step> stepsData;
            stepsData = mDb.stepsDao().retrieveSteps(recipeID);

            System.out.println("STEP DATA: "+ stepsData.size());
            return stepsData;
        }

        @Override
        protected void onPostExecute(List<Step> steps) {
            super.onPostExecute(steps);

            if (steps != null && steps.size() > 0) {
                stepRecycleViewAdapter.setStepsData(steps);
                stepsReciclerView.setAdapter(stepRecycleViewAdapter);
            } else {
                Toast.makeText(RecipeDetailsActivity.this, getString(R.string.queryFailed), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RequestIngredients extends AsyncTask<Integer, Void, List<Ingredient>> {

        @Override
        protected List<Ingredient> doInBackground(Integer... params) {
            int recipeID = params[0];
            List<Ingredient> ingredientData;
            ingredientData = mDb.ingredientsDao().retriveIngredients(recipeID);

            return ingredientData;
        }

        @Override
        protected void onPostExecute(List<Ingredient> ingredients) {
            super.onPostExecute(ingredients);

            if (ingredients != null && ingredients.size() > 0) {
                for (Ingredient ingredient : ingredients) {
                    ingredientsList = ingredientsList + ingredient.getName() + "\n";
                }
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(RecipeDetailsActivity.this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(RecipeDetailsActivity.this, BakingWidgetProvider.class));

                BakingWidgetProvider.updateTextWidgets(RecipeDetailsActivity.this, appWidgetManager,appWidgetIds, recipe.getTitle(), ingredientsList);

            } else {
                Toast.makeText(RecipeDetailsActivity.this, getString(R.string.queryFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
