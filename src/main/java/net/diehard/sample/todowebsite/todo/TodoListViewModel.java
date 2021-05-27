package net.diehard.sample.todowebsite.todo;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TodoListViewModel {

    @Valid
    private List<TodoItem> todoList = new ArrayList<>();

    public TodoListViewModel(List<TodoItem> todoList) {
        this.todoList = todoList;
    }

    public List<TodoItem> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<TodoItem> todoList) {
        this.todoList = todoList;
    }

}
