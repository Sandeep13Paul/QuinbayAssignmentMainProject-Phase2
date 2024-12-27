package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl {

    @Autowired
    private TodoRepository todoRepository;

    // Create or update a Todo task
    public Todo createOrUpdateTodo(Todo todo) {
        if (todo.getDueDate() != null) {
            // Set expiredAt to match dueDate
            todo.setExpiredAt(todo.getDueDate());
        }
        return todoRepository.save(todo);
    }

    // Delete a Todo task
    public void deleteTodo(String id) {
        todoRepository.deleteById(id);
    }

    // Find a Todo by ID
    public Optional<Todo> getTodoById(String id) {
        return todoRepository.findById(id);
    }

    // Mark a Todo task as completed
    public Todo markCompleted(String id) {
        return todoRepository.findById(id).map(todo -> {
            todo.setCompleted(true);
            return todoRepository.save(todo);
        }).orElse(null);
    }

    // Mark a Todo task as expired
    public void markExpired(String id) {
        todoRepository.findById(id).map(todo -> {
            todo.setExpiredAt(new Date());  // Set the current time as expiredAt
            todo.setCompleted(false);
            return todoRepository.save(todo);
        });
    }

    // Fetch all Todos and mark expired ones
    public List<Todo> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        for (Todo todo : todos) {
            if (todo.getDueDate() != null && todo.getDueDate().before(new Date()) && !todo.isCompleted()) {
                markExpired(todo.getId());
            }
        }
        return todos;
    }

    // Check for expired Todos every minute
    @Scheduled(fixedRate = 60000)
    public void checkForExpiredTodos() {
        List<Todo> todos = todoRepository.findAll();
        for (Todo todo : todos) {
            if (todo.getDueDate() != null && todo.getDueDate().before(new Date()) && !todo.isCompleted()) {
                markExpired(todo.getId());
            }
        }
    }
}
