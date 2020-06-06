package android.example.bakingtime.view.adapter;

import android.example.bakingtime.R;
import android.example.bakingtime.service.models.Recipe;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    List<Recipe> mRecipes;
    OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView recipeNameTv;

        RecipeViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            recipeNameTv = itemView.findViewById(R.id.recipe_name_tv);
            CardView cv  = itemView.findViewById(R.id.recipe_name_cv);
            cv.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_layout, parent, false);
        return new RecipeViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe currentRecipe = mRecipes.get(position);
        holder.recipeNameTv.setText(currentRecipe.getName());
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null) {
            return mRecipes.size();
        } else {
            return 0;
        }
    }

    public void setRecipeList(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }
}
