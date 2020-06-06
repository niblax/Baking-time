package android.example.bakingtime.view.ui;

import android.example.bakingtime.R;
import android.example.bakingtime.service.models.Ingredient;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class IngredientListFragment extends ListFragment {

    public static final String INGREDIENTS_ARG = "ingredients";

    private List<Ingredient> mIngredients;

    public static IngredientListFragment newInstance(List<Ingredient> ingredients) {
        IngredientListFragment fragment = new IngredientListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(INGREDIENTS_ARG, (ArrayList) ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(INGREDIENTS_ARG)) {
            mIngredients = getArguments().getParcelableArrayList(INGREDIENTS_ARG);
        } else {
            Toast.makeText(getContext(), R.string.no_ingredients,
                    Toast.LENGTH_SHORT).show();
            // to prevent null exception
            mIngredients = new ArrayList<>();
        }

        setListAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getFormattedIngredients()));
    }

    private List<String> getFormattedIngredients() {
        List<String> allTexts = new ArrayList<>();

        StringBuilder text = new StringBuilder();
        for (Ingredient ingredient : mIngredients) {
            text.append(ingredient.getQuantity()).append(" ");
            text.append(ingredient.getMeasure()).append(" ");
            text.append(ingredient.getIngredient());
            allTexts.add(text.toString());
            // reset builder
            text.setLength(0);
        }

        return allTexts;
    }
}
