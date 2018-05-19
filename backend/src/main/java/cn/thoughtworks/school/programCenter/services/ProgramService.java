package cn.thoughtworks.school.programCenter.services;

import cn.thoughtworks.school.programCenter.entities.Program;
import cn.thoughtworks.school.programCenter.entities.Task;
import cn.thoughtworks.school.programCenter.entities.UserProgram;
import cn.thoughtworks.school.programCenter.repositories.ProgramRepository;
import cn.thoughtworks.school.programCenter.repositories.ReviewQuizRepository;
import cn.thoughtworks.school.programCenter.repositories.TaskRepository;
import cn.thoughtworks.school.programCenter.repositories.UserProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProgramService {
    @Autowired
    private ReviewQuizRepository reviewQuizRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserProgramRepository userProgramRepository;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private ProgramRepository programRepository;

    @Cacheable(value = "LEADER_BOARD_CACHE")
    public List getStudentLeaderBoard() {
        return cacheLeaderBoard();
    }

    public List cacheLeaderBoard() {
        List result = new ArrayList();
        List<Program> programs = programRepository.findAll();
        programs.forEach(program -> {
            Map temp = new HashMap();
            List leaderBoards = new ArrayList();
            List<Task> tasks = taskRepository.findByProgramId(program.getId());
            List<Long> ids = getTaskIds(tasks);
            List<UserProgram> userPrograms = userProgramRepository.findByProgramId(program.getId());

            userPrograms.forEach(item -> {
                ResponseEntity userInfoResponse = userCenterService.getUserInfo(item.getUserId());
                if (Objects.nonNull(userInfoResponse)) {
                    Map userInfo = (Map) userInfoResponse.getBody();
                    String name = getStudentName(userInfo);
                    Long studentId = getStudentId(userInfo);
                    leaderBoards.add(getUserBoard(ids, studentId, name));
                }
            });
            leaderBoards.sort((Comparator<Map>) (o1, o2) ->
                    (int) (Double.valueOf(o2.get("finishHomeworkPoint").toString()) - Double.valueOf(o1.get("finishHomeworkPoint").toString())));
            temp.put("currentProgramLeaderBoard", leaderBoards);
            temp.put("programId", program.getId());
            result.add(temp);
        });
        return result;
    }


    private Long getStudentId(Map userInfo) {
        if (Objects.isNull(userInfo.get("id"))) {
            return Long.valueOf(userInfo.get("userId").toString());
        } else {
            return Long.valueOf(userInfo.get("id").toString());
        }
    }

    private String getStudentName(Map userInfo) {
        if (Objects.isNull(userInfo.get("name"))) {
            return userInfo.get("userName").toString();
        } else {
            return userInfo.get("name").toString();
        }
    }

    public Map getUserBoard(List<Long> taskIds, Long studentId, String name) {
        Map result = new HashMap();

        result.put("name", name);
        result.put("studentId", studentId);
        Long finishSectionCount = reviewQuizRepository.getFinishSectionCount(studentId, taskIds);
        result.put("finishHomeworkCount", Objects.isNull(finishSectionCount) ? 0 : finishSectionCount);
        Long scores = reviewQuizRepository.getScores(studentId, taskIds);
        result.put("finishHomeworkPoint", Objects.isNull(scores) ? 0 : scores);

        return result;
    }

    private List<Long> getTaskIds(List<Task> tasks) {
        List<Long> result = new ArrayList<>();
        for (Task task : tasks) {
            result.add(task.getId());
        }

        return result;
    }

}
