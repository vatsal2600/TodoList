package com.example.todolist.Models;

public class Model {

    String task, description, id;

    public Model(){}

    public Model(String task, String description, String id) {
        this.task = task;
        this.description = description;
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
