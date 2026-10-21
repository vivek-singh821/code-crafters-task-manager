
package com.example.taskmanager;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public HomeController(TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String showTasks(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long editId,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");

        List<Task> allTasks = taskRepository.findByOwnerId(userId);

        List<Task> filtered = allTasks.stream()
                .filter(t -> t.getTitle().toLowerCase()
                        .contains(search.toLowerCase()))
                .toList();

        model.addAttribute("tasks", filtered);
        model.addAttribute("search", search);
        model.addAttribute("total", allTasks.size());
        model.addAttribute("completed",
                allTasks.stream().filter(Task::isCompleted).count());
        model.addAttribute("userName", session.getAttribute("userName"));

        Task editTask = editId == null ? null :
                allTasks.stream()
                        .filter(t -> t.getId().equals(editId))
                        .findFirst().orElse(null);

        model.addAttribute("editTask", editTask);

        return "index";
    }

    @PostMapping("/tasks/add")
    public String addTask(
            @RequestParam String title,
            @RequestParam(defaultValue = "") String description,
            @RequestParam(defaultValue = "Medium") String priority,
            HttpSession session) {

        if (!title.isBlank()) {
            Long userId = (Long) session.getAttribute("userId");

            AppUser owner = userRepository.findById(userId)
                    .orElseThrow();

            taskRepository.save(new Task(
                    title.trim(), description.trim(), priority, owner));
        }

        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/edit")
    public String editTask(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(defaultValue = "") String description,
            @RequestParam(defaultValue = "Medium") String priority,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        taskRepository.findById(id)
                .filter(task -> task.getOwner().getId().equals(userId))
                .ifPresent(task -> {
                    if (!title.isBlank()) {
                        task.setTitle(title.trim());
                        task.setDescription(description.trim());
                        task.setPriority(priority);
                        taskRepository.save(task);
                    }
                });

        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/complete")
    public String completeTask(
            @PathVariable Long id, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        taskRepository.findById(id)
                .filter(task -> task.getOwner().getId().equals(userId))
                .ifPresent(task -> {
                    task.setCompleted(!task.isCompleted());
                    taskRepository.save(task);
                });

        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(
            @PathVariable Long id, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        taskRepository.findById(id)
                .filter(task -> task.getOwner().getId().equals(userId))
                .ifPresent(taskRepository::delete);

        return "redirect:/tasks";
    }
}
