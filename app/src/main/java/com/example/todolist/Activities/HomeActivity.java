package com.example.todolist.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Models.Model;
import com.example.todolist.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    LinearLayoutManager linearLayoutManager;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userId;
    private String name="";

    private String key="";
    private String task, description;

    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.homeToolbar);
        recyclerView=findViewById(R.id.recyclerView);
        floatingActionButton=findViewById(R.id.fab);

        GoogleSignInAccount user= GoogleSignIn.getLastSignedInAccount(this);
        if(user!=null){
            name=user.getGivenName()+"'s ";
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name+"Todo List");

        loader=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        userId=mAuth.getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("tasks").child(userId);

        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });


    }

    private void addTask() {

        AlertDialog.Builder myDialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);

        View myView=inflater.inflate(R.layout.input_task,null);
        myDialog.setView(myView);

        AlertDialog dialog=myDialog.create();
        dialog.setCancelable(false);

        final EditText task=myView.findViewById(R.id.task);
        final EditText description=myView.findViewById(R.id.description);
        Button save=myView.findViewById(R.id.saveBtn);
        Button cancel=myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTask=task.getText().toString().trim();
                String mDescription=description.getText().toString().trim();
                String id=reference.push().getKey();

                if(TextUtils.isEmpty(mTask)){
                    task.setError("Task Required");
                    return;
                }
                if(TextUtils.isEmpty(mDescription)){
                    description.setError("Description Required");
                    return;
                }
                else{
                    loader.setMessage("Adding your task");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model=new Model(mTask,mDescription,id);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "Task has been added successfully.", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                            else{
                                Toast.makeText(HomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        loader.setMessage("Loading tasks..");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        FirebaseRecyclerOptions<Model> options=new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());
                loader.dismiss();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key=getRef(holder.getAdapterPosition()).getKey();
                        task= model.getTask();
                        description= model.getDescription();

                        updateTask();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_layout,parent,false);
                loader.dismiss();
                return new MyViewHolder(view);

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateTask(){

        AlertDialog.Builder myDialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);

        View myView=inflater.inflate(R.layout.update_list,null);
        myDialog.setView(myView);

        AlertDialog dialog=myDialog.create();

        EditText mTask= myView.findViewById(R.id.editTask);
        EditText mDescription= myView.findViewById(R.id.editDescription);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button deleteBtn=myView.findViewById(R.id.deleteBtn);
        Button updateBtn=myView.findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task=mTask.getText().toString().trim();
                description=mDescription.getText().toString().trim();

                Model model=new Model(task,description,key);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Task updated successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(HomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Task deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(HomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setTask(String task){
            TextView taskTextView=mView.findViewById(R.id.taskCard);
            taskTextView.setText(task);
        }

        public void setDescription(String description){
            TextView descTextView=mView.findViewById(R.id.descriptionCard);
            descTextView.setText(description);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                //signOut();
                mAuth.signOut();
                Toast.makeText(HomeActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(HomeActivity.this,SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

}