package com.taskmanager.response;

public class TaskListResponse {
    private Long id;
    private String name;

    // Constructores, getters y setters

    // Constructor sin argumentos para frameworks como Jackson
    public TaskListResponse() {
    }

    // Constructor con argumentos para inicialización fácil
    public TaskListResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}