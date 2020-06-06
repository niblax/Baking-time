package android.example.bakingtime.viewmodel;

import android.app.Application;
import android.example.bakingtime.service.repository.RecipeRepository;
import android.example.bakingtime.service.models.Recipe;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    LiveData<List<Recipe>> mRecipeList;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeList = new RecipeRepository().getRecipeList();
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return mRecipeList;
    }

    public Recipe getRecipeByIndex(int index) {
        try {
            return mRecipeList.getValue().get(index);
        } catch (Exception e) {
            return null;
        }
    }
}
