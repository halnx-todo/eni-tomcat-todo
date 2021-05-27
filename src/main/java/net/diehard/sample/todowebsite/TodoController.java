package net.diehard.sample.todowebsite;

import net.diehard.sample.todowebsite.todo.SessionUnsavedList;
import net.diehard.sample.todowebsite.todo.TodoItem;
import net.diehard.sample.todowebsite.todo.TodoItemRepository;
import net.diehard.sample.todowebsite.todo.TodoListViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class TodoController {


    private static final Logger LOG = Logger.getLogger(TodoController.class.getCanonicalName());
    private static final long serialVersionUID = 8570655773280783303L;


    private final TodoItemRepository repository;

    private final SessionUnsavedList todoUnsavedList;
    private final FileService fileService;
    private long sessionIndex = 0;

    public TodoController(TodoItemRepository repository, SessionUnsavedList todoUnsavedList, FileService fileService) {
        this.repository = repository;
        this.todoUnsavedList = todoUnsavedList;
        this.fileService = fileService;
    }

    @RequestMapping("/")
    public String index(HttpSession session, Model model) {
        LOG.info("/ called ");
        List<TodoItem> todoList = repository.findAll();
        LOG.info("requestItems Persisted : " + todoList);
        LOG.info("requestItems Memory : " + todoUnsavedList);
        todoList.addAll(todoUnsavedList);
        model.addAttribute("newitem", new TodoItem());
        model.addAttribute("items", new TodoListViewModel(todoList));
        model.addAttribute("myHostName", getHostname());
        return "index";
    }

    @RequestMapping("/add")
    public String addTodo(HttpSession session, @RequestParam("file") MultipartFile file, @ModelAttribute TodoItem requestItem) {
        LOG.info("/add called ");
        TodoItem item = new TodoItem(requestItem.getCategory(), requestItem.getName());
        item.setOnlyinsession(requestItem.isOnlyinsession());

        LOG.finer("requestItem : " + requestItem);
        //Check that the file is uploaded
        if (!file.isEmpty()) {
            String tmpFileName = fileService.storeFile(file);
            item.setFilename(tmpFileName);
        }
        if (requestItem.isOnlyinsession()) {
            item.setId(sessionIndex--);
            LOG.finer("requestItem : " + item);
            todoUnsavedList.add(item);
        } else {
            LOG.finer("requestItem : " + item);
            repository.save(item);
        }
        return "redirect:/";
    }

    @RequestMapping("/update")
    public String updateTodo(HttpSession session, @ModelAttribute TodoListViewModel requestItems) {
        LOG.info("/update called ");
        for (TodoItem requestItem : requestItems.getTodoList()) {
            LOG.finer("update requestItem : " + requestItem);
            Long index = requestItem.getId();
            // index < 1 means todoitems in sessions (not in database)
            if (index < 1) {
                // todo is draft
                if (requestItem.isDelete()) {
                    todoUnsavedList.remove(requestItem);
                    session.setAttribute("SessionUnsavedList",
                            todoUnsavedList);
                }else if (requestItem.isComplete()) {
                    todoUnsavedList.remove(requestItem);
                    session.setAttribute("SessionUnsavedList",
                            todoUnsavedList);
                    saveTodo(requestItem);
                }
            }else if (requestItem.isDelete()) {
                TodoItem item = new TodoItem(requestItem.getCategory(), requestItem.getName());
                item.setId(requestItem.getId());
                repository.delete(item);
            } else {
                saveTodo(requestItem);
            }
        }
        return "redirect:/";
    }

    private void saveTodo(TodoItem requestItem) {
        TodoItem item = new TodoItem(requestItem.getCategory(), requestItem.getName());
        item.setComplete(requestItem.isComplete());
        item.setFilename(requestItem.getFilename());
        item.setOnlyinsession(false);
        item.setId(requestItem.getId());
        repository.save(item);
    }

    //this is a very dirty trick
    //It is used into index.html in order to display hostname (aka name of the pod)
    private String getHostname() {
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            return hostName;
        } catch (UnknownHostException e) {
            LOG.warning("UnknownHostException : " + e.getLocalizedMessage());
            return "no-hostname";
        }

    }

    @RequestMapping(value = "/files/{filename}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(
            @PathVariable("filename") String fileName,
            HttpServletResponse response) throws IOException {

            Resource resource = fileService.loadFileAsResource(fileName);
            if (resource != null) {
                FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
                response.flushBuffer();
            }

    }

}
