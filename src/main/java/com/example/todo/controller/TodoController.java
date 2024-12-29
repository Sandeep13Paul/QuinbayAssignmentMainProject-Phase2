package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.service.TodoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoServiceImpl todoService;

    // Create or update a Todo task
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        if (todo.getText() == null || todo.getText().isBlank()) {
            return ResponseEntity.badRequest().body(null); // Return 400 if text is missing
        }
        if (todo.getDueDate() != null && todo.getDueDate().before(new Date())) {
            return ResponseEntity.badRequest().body(null); // Return 400 if dueDate is in the past
        }
        Todo createdTodo = todoService.createOrUpdateTodo(todo);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    // Fetch all Todos
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    // Fetch a Todo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable String id) {
        return todoService.getTodoById(id)
                .map(todo -> new ResponseEntity<>(todo, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Return 404 if not found
    }

    // Mark a Todo task as completed
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Todo> markCompleted(@PathVariable String id) {
        Todo updatedTodo = todoService.markCompleted(id);
        if (updatedTodo != null) {
            return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
    }

    // Delete a Todo task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
        if (todoService.getTodoById(id).isPresent()) {
            todoService.deleteTodo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 on successful deletion
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
    }
}
