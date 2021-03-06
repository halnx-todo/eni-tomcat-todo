/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.diehard.sample.todowebsite.todo;

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class TodoItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoItemRepository todolist;

    public TodoItemRepositoryTest() {
    }

    @Test
    public void testFindByName() {
        TodoItem item = new TodoItem("test", "test001");
        entityManager.persist(item);

        List<TodoItem> findByName = todolist.findByName("test001");

        assertThat(findByName).extracting(TodoItem::getName).containsOnly(item.getName());
    }

}
