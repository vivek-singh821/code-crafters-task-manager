
package com.example.taskmanager;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String priority;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser owner;

    public Task() {
    }

    public Task(String title, String description, String priority,
                AppUser owner) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.owner = owner;
        this.completed = false;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public AppUser getOwner() { return owner; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public void setOwner(AppUser owner) {
        this.owner = owner;
    }
}
