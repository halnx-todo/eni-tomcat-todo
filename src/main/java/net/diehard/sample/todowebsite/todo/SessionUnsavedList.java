package net.diehard.sample.todowebsite.todo;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Component
@SessionScope(proxyMode = TARGET_CLASS)
public class SessionUnsavedList extends ArrayList<TodoItem> implements Serializable {

}
