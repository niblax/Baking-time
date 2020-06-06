package android.example.bakingtime.view.ui;

import android.content.Context;
import android.example.bakingtime.R;
import android.example.bakingtime.service.models.Step;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InstructionListFragment extends ListFragment {

    public static final String STEPS_ARG = "steps";

    private List<Step> mSteps;
    private OnItemClickListener mCallback;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static InstructionListFragment newInstance(List<Step> steps) {
        InstructionListFragment fragment = new InstructionListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS_ARG, (ArrayList) steps);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(STEPS_ARG)) {
            mSteps = getArguments().getParcelableArrayList(STEPS_ARG);
        } else {
            Toast.makeText(getContext(), R.string.no_instructions,
                    Toast.LENGTH_SHORT).show();
            // prevent null exception
            mSteps = new ArrayList<>();
        }

        setListAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                getStepDescriptions()));
    }

    private List<String> getStepDescriptions() {
        List<String> stepDescriptions = new ArrayList<>();
        for (Step step : mSteps) {
            stepDescriptions.add(step.getShortDescription());
        }
        return stepDescriptions;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        if (mCallback != null) {
            mCallback.onItemClick(position);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallback = (OnItemClickListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
