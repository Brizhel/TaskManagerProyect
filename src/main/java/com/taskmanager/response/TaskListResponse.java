package com.taskmanager.response;

public class TaskListResponse {
    private String name;

    // Constructores, getters y setters

    // Constructor sin argumentos para frameworks como Jackson
    public TaskListResponse() {
    }

    // Constructor con argumentos para inicialización fácil
    public TaskListResponse(String name) {
        this.name = name;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}