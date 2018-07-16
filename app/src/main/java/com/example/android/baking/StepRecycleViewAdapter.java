package com.example.android.baking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.baking.model.Step;

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
