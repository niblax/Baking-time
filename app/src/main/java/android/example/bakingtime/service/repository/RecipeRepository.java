package android.example.bakingtime.service.repository;

import android.example.bakingtime.service.RecipeListApi;
import android.example.bakingtime.service.models.Recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeRepository {

    private MutableLiveData<List<Recipe>> data = new MutableLiveData<>();

    public LiveData<List<Recipe>> getRecipeList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RecipeListApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeListApi api = retrofit.create(RecipeListApi.class);

        api.getRecipeList().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}