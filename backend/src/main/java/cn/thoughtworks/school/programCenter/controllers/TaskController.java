package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.entities.Task;
import cn.thoughtworks.school.programCenter.entities.Topic;
import cn.thoughtworks.school.programCenter.exceptions.BusinessException;
import cn.thoughtworks.school.programCenter.repositories.ReviewQuizRepository;
import cn.thoughtworks.school.programCenter.repositories.TaskRepository;
import cn.thoughtworks.school.programCenter.repositories.TopicRepository;
//import cn.thoughtworks.school.programCenter.services.PaperCenterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api")
public class TaskController {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TaskRepository taskRepository;

    private ObjectMapper oMapper = new ObjectMapper();

    @RequestMapping(value = "/topics/{topicId}", method = RequestMethod.GET)
    public ResponseEntity getTasksByTopicId(@PathVariable Long topicId) {
        Topic topic = topicRepository.findOne(topicId);
        List<Task> tasks = taskRepository.findTaskByTopicIdOrderByOrderNumberAsc(topicId);
        HashMap<String, Object> currentTopicTasks = new HashMap();
        currentTopicTasks.put("topic", topic);
        currentTopicTasks.put("tasks", tasks);

        return new ResponseEntity<>(currentTopicTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public ResponseEntity addTask(@RequestBody Task task) {
        Task newTask = taskRepository.save(task);
        newTask.setOrderNumber(newTask.getId());
        task.setVisible(false);
        taskRepository.save(newTask);
        Map<String, String> body = new HashMap<>();
        body.put("uri", "/api/tasks/" + newTask.getId());
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.PUT)
    public ResponseEntity updateTask(@PathVariable Long taskId, @RequestBody Task task) throws Exception {
        Task oldTask = taskRepository.findOne(taskId);
        if (oldTask == null) {
            throw new BusinessException("该task不存在");
        }
        taskRepository.save(task);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.DELETE)
    public ResponseEntity addTask(@PathVariable Long taskId) throws Exception {
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            throw new BusinessException("task不存在");
        }
        taskRepository.delete(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/v2/tasks/{taskId}", method = RequestMethod.GET)
    public ResponseEntity getTask(@PathVariable Long taskId) throws BusinessException {
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            throw new BusinessException("task不存在");
        }
        Topic topic = topicRepository.findOne(task.getTopicId());
        Map map = oMapper.convertValue(task, Map.class);
        map.put("topic", topic);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/tasks")
    public ResponseEntity dragSort(@RequestBody List<Task> tasks) {
        for (int i = 0; i < tasks.size(); ++i) {
            tasks.get(i).setOrderNumber((long) i);
        }

        taskRepository.save(tasks);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/tasks/{id}/visibility")
    public ResponseEntity changeTaskVisible(@PathVariable Long id, @RequestBody Map data) throws BusinessException {
        Task task = taskRepository.findOne(id);
        if (task == null) {
            throw new BusinessException("task 不存在");
        }
        if ((Boolean) data.get("visibility")) {
            Topic topic = topicRepository.findOne(task.getTopicId());
            topic.setVisible(true);
            topicRepository.save(topic);
        }
        task.setVisible((Boolean) data.get("visibility"));
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/task/{id}/flip", method = RequestMethod.GET)
    public ResponseEntity getNextTask(@PathVariable Long id) throws BusinessException {
        Task task = taskRepository.findOne(id);
        List<Topic> topics = topicRepository.findByProgramIdAndVisibleOrderByOrderNumberAsc(task.getProgramId(), true);

        Long nextId = getNextTaskId(topics, task);
        Long previousId = getPreviousTaskId(topics, task);
        Map body = new HashMap();
        body.put("nextId", nextId);
        body.put("previousId", previousId);

        return new ResponseEntity(body, HttpStatus.OK);
    }

    private Long getNextTaskId(List<Topic> topics, Task task) {
        List<Task> nextTasks = taskRepository.findByProgramIdAndTopicIdAndVisibleIsTrueAndOrderNumberGreaterThanOrderByOrderNumberAsc(task.getProgramId(), task.getTopicId(), task.getOrderNumber());
        if (nextTasks.size() > 0) {
            return nextTasks.get(0).getId();
        }

        int index = -1;
        for (int i = 0; i < topics.size(); ++i) {
            if (task.getTopicId().equals(topics.get(i).getId())) {
                index = i;
            }
        }

        if (index < topics.size() - 1 && index > -1) {
            List<Task> tasks = taskRepository.findByProgramIdAndTopicIdAndVisibleIsTrueOrderByOrderNumberAsc(task.getProgramId(), topics.get(index + 1).getId());
            return tasks.size() > 0 ? tasks.get(0).getId() : -1;
        }

        return -1L;
    }

    private Long getPreviousTaskId(List<Topic> topics, Task task) {
        List<Task> previousTasks = taskRepository.findByProgramIdAndTopicIdAndOrderNumberLessThanOrderByOrderNumberDesc(task.getProgramId(), task.getTopicId(), task.getOrderNumber());
        if (previousTasks.size() > 0) {
            return previousTasks.get(0).getId();
        }

        int index = -1;
        for (int i = 0; i < topics.size(); ++i) {
            if (task.getTopicId().equals(topics.get(i).getId())) {
                index = i;
            }
        }

        if (index > 0) {
            List<Task> tasks = taskRepository.findByProgramIdAndTopicIdAndVisibleIsTrueOrderByOrderNumberAsc(task.getProgramId(), topics.get(index - 1).getId());
            return tasks.size() > 0 ? tasks.get(tasks.size() - 1).getId() : -1;
        }

        return -1L;
    }
}
