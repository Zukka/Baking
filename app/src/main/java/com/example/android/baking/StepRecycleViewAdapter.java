package com.example.android.baking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.baking.model.Recipe;
import com.example.android.baking.model.Step;
import com.example.android.baking.utils.RecipeJsonConstants;

import java.util.List;

public class StepRecycleViewAdapter extends RecyclerView.Adapter<StepRecycleViewAdapter.StepsViewHolder>{

    private List<Step> mStepData;
    private Context mContext;

    public StepRecycleViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder {
        TextView stepDescription;
        StepsViewHolder(View itemView) {
            super(itemView);
            stepDescription = itemView.findViewById(R.id.step_name);
        }
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.steps_list_card, parent, false);

       StepRecycleViewAdapter.StepsViewHolder vh = new StepRecycleViewAdapter.StepsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StepRecycleViewAdapter.StepsViewHolder holder, int position) {
        final Step step = mStepData.get(position);
        holder.stepDescription.setText(step.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShowStepDetails = new Intent(mContext, StepDetailActivity.class);
                intentShowStepDetails.putExtra(RecipeJsonConstants.RECIPE, step.getRecipeId());
                intentShowStepDetails.putExtra("selectedStep", Integer.parseInt(step.getStepId()));
                mContext.startActivity(intentShowStepDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
       if (null == mStepData) return 0;
       return mStepData.size();
    }

    public void setStepsData(List<Step> stepData) {
        mStepData = stepData;
        notifyDataSetChanged();
    }
}
