package android.example.bakingtime.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.example.bakingtime.R;
import android.example.bakingtime.service.models.Recipe;
import android.example.bakingtime.service.models.Step;
import android.example.bakingtime.view.widget.IngredientWidget;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import static android.os.Build.VERSION.SDK_INT;

public class RecipeDetailActivity extends AppCompatActivity implements
        InstructionListFragment.OnItemClickListener {

    private static final String TAG = "RecipeDetailActivity";

    public static final String RECIPE_EXTRA = "recipe";

    Recipe mRecipe;
    boolean mTwoPane;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private long playbackPosition = 0;
    private int windowIndex = 0;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getIntent().hasExtra(RECIPE_EXTRA)) {
            mRecipe = getIntent().getParcelableExtra(RECIPE_EXTRA);
        } else {
            Toast.makeText(this, R.string.recipe_error,
                    Toast.LENGTH_LONG).show();
            finish();
        }

        if (!getResources().getBoolean(R.bool.is_phone)) {
            mTwoPane = true;
        }

        getSupportActionBar().setTitle(mRecipe.getName());

        displayInitialContent();

        playerView = findViewById(R.id.video_view);

        if (!mTwoPane) {
            setNavClickListener(findViewById(R.id.instructions_ingredients_nav_view));
        }

        //updateWidget(getFormattedIngredients(mRecipe.getIngredients()));
    }

    private void displayInitialContent() {
        displayStepContent(0);

        displayInstructions();

        if (mTwoPane) {
            displayIngredients();
        }
    }

    private void setNavClickListener(BottomNavigationView view) {
        BottomNavigationView.OnNavigationItemSelectedListener listener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_instructions:
                                replaceFragment(InstructionListFragment.newInstance(mRecipe.getSteps()),
                                        R.id.fragment_container);
                                return true;
                            case R.id.nav_ingredients:
                                replaceFragment(IngredientListFragment.newInstance(mRecipe.getIngredients()),
                                        R.id.fragment_container);
                                return true;
                            default:
                                return false;
                        }
                    }
                };

        view.setOnNavigationItemSelectedListener(listener);
    }

    private void displayInstructions() {
        InstructionListFragment fragment = InstructionListFragment.newInstance(mRecipe.getSteps());

        if (mTwoPane) {
            replaceFragment(fragment, R.id.instructions_container);
        } else {
            replaceFragment(fragment, R.id.fragment_container);
        }
    }

    private void displayIngredients() {
        IngredientListFragment fragment = IngredientListFragment.newInstance(mRecipe.getIngredients());

        if (mTwoPane) {
            replaceFragment(fragment, R.id.ingredients_container);
        } else {
            replaceFragment(fragment, R.id.fragment_container);
        }
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    @Override
    public void onItemClick(int position) {
        String url = mRecipe.getSteps().get(position).getVideoURL();
        if (missing(url)) {
            Toast.makeText(this, R.string.video_url_missing,
                    Toast.LENGTH_LONG).show();
        }
        player.prepare(buildMediaSource(Uri.parse(url)),
                true, true);

        displayStepContent(position);
    }

    private void displayStepContent(int id) {
        Step step = mRecipe.getSteps().get(id);

        ((TextView) findViewById(R.id.desc_tv))
                .setText(step.getDescription());

        ((TextView) findViewById(R.id.short_desc_tv))
                .setText(step.getShortDescription());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SDK_INT >= 24) {
            initPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SDK_INT < 24 || player == null) {
            initPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void initPlayer() {
        String url = mRecipe.getSteps().get(0).getVideoURL(); //first step video
        if (missing(url)) {
            Toast.makeText(this, R.string.video_url_missing, Toast.LENGTH_LONG).show();
            return;
        }
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(windowIndex, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private boolean missing(String url) {
        return url.equals("");
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            windowIndex = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory).
                createMediaSource(uri);
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        outState.putBoolean();
//        private boolean playWhenReady = true;
//        private long playbackPosition = 0;
//        private int windowIndex = 0;
//        private String mUrl;
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (mTwoPane) {
                return;
            }

            if(getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            findViewById(R.id.short_desc_tv).setVisibility(View.GONE);
            findViewById(R.id.desc_tv).setVisibility(View.GONE);
            findViewById(R.id.instructions_ingredients_nav_view).setVisibility(View.GONE);
            findViewById(R.id.fragment_container).setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();

            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            playerView.setLayoutParams(params);

            hideSystemUi();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (mTwoPane) {
                return;
            }

            showSystemUi();

            if(getSupportActionBar() != null) {
                getSupportActionBar().show();
            }

            findViewById(R.id.short_desc_tv).setVisibility(View.VISIBLE);
            findViewById(R.id.desc_tv).setVisibility(View.VISIBLE);
            findViewById(R.id.instructions_ingredients_nav_view).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int) (250 * getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
        }
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void showSystemUi () {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    private void updateWidget(List<String> ingredientTexts) {

        // wrap intent inside pending intent
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.RECIPE_EXTRA, mRecipe);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // get the widget views
        RemoteViews views = new RemoteViews(this.getPackageName(),
                R.layout.ingredients_widget);

        // set pending intent to root view
        views.setOnClickPendingIntent(R.id.widget_root_view, pendingIntent);

        // set ingredients to text view
        String ingredientsText = getIngredientsText(ingredientTexts);
        views.setTextViewText(R.id.widget_ingredients_tv, ingredientsText);

        // update widget
        AppWidgetManager.getInstance(this).updateAppWidget(
                new ComponentName(this, IngredientWidget.class),
                views);
    }

    private String getIngredientsText(List<String> ingredientTexts) {
        StringBuilder sb = new StringBuilder();
        for (String text : ingredientTexts) {
            sb.append(text).append("\n");
        }
        return sb.toString();
    }
}
