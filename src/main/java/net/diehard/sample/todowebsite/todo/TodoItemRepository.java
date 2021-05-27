package net.diehard.sample.todowebsite.todo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.List;
public interface TodoItemRepository extends CrudRepository<TodoItem, Long> , Serializable {

    List<TodoItem> findByName(String name);

    List<TodoItem> findAll();

}
