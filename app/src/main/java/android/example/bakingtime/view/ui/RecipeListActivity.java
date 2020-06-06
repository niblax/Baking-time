package android.example.bakingtime.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.bakingtime.R;
import android.example.bakingtime.service.models.Recipe;
import android.example.bakingtime.viewmodel.RecipeListViewModel;
import android.example.bakingtime.view.adapter.RecipeAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

public class RecipeListActivity extends AppCompatActivity
    implements RecipeAdapter.OnItemClickListener {
//todo ui testa med Espresso
    private static final String RV_STATE_KEY = "rv_state";

    RecyclerView mRecyclerView;
    Parcelable mSavedRecyclerLayoutState;
    RecipeAdapter mAdapter;
    RecipeListViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        if (savedInstanceState != null) {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(RV_STATE_KEY);
        }

        initializeRecyclerView();

        mViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);

        mViewModel.getRecipeList().observe(this, recipes -> {
            if (recipes != null) {
                mAdapter.setRecipeList(recipes);
            } else {
                Toast.makeText(this, R.string.network_error, 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializeRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new RecipeAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (getResources().getBoolean(R.bool.is_phone)) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        if (mSavedRecyclerLayoutState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        Recipe recipe = mViewModel.getRecipeByIndex(position);
        intent.putExtra(RecipeDetailActivity.RECIPE_EXTRA, recipe);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mSavedRecyclerLayoutState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RV_STATE_KEY, mSavedRecyclerLayoutState);
        super.onSaveInstanceState(outState);
    }
}
