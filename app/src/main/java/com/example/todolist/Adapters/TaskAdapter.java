package com.example.todolist.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Models.Model;
import com.example.todolist.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TaskAdapter extends FirebaseRecyclerAdapter<Model,TaskAdapter.MyViewHolder> {

    public TaskAdapter(
            @NonNull FirebaseRecyclerOptions<Model> options)
    {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model)
    {
        holder.setTask(model.getTask());
        holder.setDescription(model.getDescription());


    }

    @Override
    public MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_layout, parent, false);
        return new TaskAdapter.MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = mView.findViewById(R.id.taskCard);
            taskTextView.setText(task);
        }

        public void setDescription(String description) {
            TextView descTextView = mView.findViewById(R.id.descriptionCard);
            descTextView.setText(description);
        }
    }

}
