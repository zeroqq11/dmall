package cn.thoughtworks.school.programCenter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuizCenterService {

    @Value("${quizCenter}")
    private String quizCenter;
    private RestTemplate template = new RestTemplate();

    public Map getBasicQuizzes(Integer page, String searchType, String searchContent) {
        String getQuizzesUrl = quizCenter + "/api/v3/basicQuizzes?page=" + page + "&" + searchType + "=" + searchContent;
        ResponseEntity<Map> result = template.getForEntity(getQuizzesUrl, Map.class);
        return result.getBody();
    }
    public List getSelectingBasicQuizzes(String ids) {
        String getQuizzesUrl = quizCenter + "/api/v3/basicQuizzes/selecting/" + ids;
        ResponseEntity<List> result = template.getForEntity(getQuizzesUrl, List.class);
        return result.getBody();
    }

    public Map getHomeworkQuizzes(Integer page, String searchType, String searchContent) {
        String getQuizzesUrl = quizCenter + "/api/v3/homeworkQuizzes?page=" + page + "&" + searchType + "=" + searchContent + "&status=2";
        ResponseEntity<Map> result = template.getForEntity(getQuizzesUrl, Map.class);
        return result.getBody();
    }

    public List getSelectHomeworkQuizzes(String ids) {
        String getQuizzesUrl = quizCenter + "/api/v3/homeworkQuizzes/" + ids + "?status=many";
        ResponseEntity<List> result = template.getForEntity(getQuizzesUrl, List.class);
        return result.getBody();
    }

    public List getSelectHomeworkWithUserAnswer(String ids, Long assignmentId, Long studentId) {
        String getTagsUrl = quizCenter + "/api/v3/homeworkQuizzes/students/" + studentId + "/assignments/" + assignmentId + "/quizzes/" + ids;
        ResponseEntity<List> result = template.getForEntity(getTagsUrl, List.class);
        return result.getBody();
    }

    public Map getSubjectiveQuizzes(Integer page, String searchType, String searchContent) {
        String getQuizzesUrl = quizCenter + "/api/v3/subjectiveQuizzes?page=" + page + "&" + searchType + "=" + searchContent;
        ResponseEntity<Map> result = template.getForEntity(getQuizzesUrl, Map.class);
        return result.getBody();
    }

    public List getSelectSubjectiveQuizzes(String ids) {
        String getQuizzesUrl = quizCenter + "/api/v3/subjectiveQuizzes/selecting/" + ids;
        ResponseEntity<List> result = template.getForEntity(getQuizzesUrl, List.class);
        return result.getBody();
    }

    public Map getLogicQuizzes(Integer page, String searchType, String searchContent) {
        String getQuizzesUrl = quizCenter + "/api/v3/logicQuizzes?page=" + page + "&" + searchType + "=" + searchContent;
        ResponseEntity<Map> result = template.getForEntity(getQuizzesUrl, Map.class);
        return result.getBody();
    }

    public List getSelectLogicQuizzes(String ids) {
        String getQuizzesUrl = quizCenter + "/api/v3/logicQuizzes/selecting/" + ids;
        ResponseEntity<List> result = template.getForEntity(getQuizzesUrl, List.class);
        return result.getBody();
    }

    public ResponseEntity<List> getTags() {
        String getTagsUrl = quizCenter + "/api/v3/tags/searchTags";

        ResponseEntity<List> result = template.getForEntity(getTagsUrl, List.class);
        return result;
    }

    public List getSelectSubjectiveQuizzesWithUserAnswer(String ids, Long assignmentId, Long studentId) {
        String url = quizCenter + "/api/v3/subjectiveQuizzes/students/" + studentId + "/assignments/" + assignmentId + "/quizzes/" + ids;
        ResponseEntity<List> result = template.getForEntity(url, List.class);
        return result.getBody();
    }

    public ResponseEntity<Map> submitSubjectiveAnswer(Long assignmentId, Long quizId, Long studentId, Map data) {

        String submitAnswerUrl = quizCenter + "/api/v3/subjectiveQuizzes/students/" + studentId + "/assignments/" + assignmentId + "/quizzes/" + quizId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String, String>>(data, headers);
        ResponseEntity<Map> resp = template.exchange(submitAnswerUrl, HttpMethod.POST, requestEntity, Map.class);
        return resp;
    }

    public ResponseEntity<Map> submitHomeworkQuizAnswer(Map data) {

        String submitAnswerUrl = quizCenter + "/api/v3/homeworkSubmission";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String, String>>(data, headers);
        ResponseEntity<Map> resp = template.exchange(submitAnswerUrl, HttpMethod.POST, requestEntity, Map.class);
        return resp;
    }

    public Map getHomeworkQuizzesLogs(Long submissionId, Long assignmentId, Long quizId, Long userId) {
        String getLogsUrl = submissionId == 0 ?
                quizCenter + "/api/v3/homeworkSubmission/users/" + userId + "/assignments/" + assignmentId + "/quizzess/" + quizId + "/logs"
                :
                quizCenter + "/api/v3/homeworkSubmission/" + submissionId + "/logs";

        ResponseEntity<Map> resp = template.getForEntity(getLogsUrl, Map.class);
        return resp.getBody();
    }


    public List submitBasicQuizAnswer(Long assignmentId, List<Map> answer, Long studentId) {
        String url = this.quizCenter + "/api/v3/basicQuizzes/students/" + studentId + "/assignments/" + assignmentId + "/quizzes";
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<List<Map>> requestEntity = new HttpEntity<>(answer, headers);
        ResponseEntity<List> resp = template.exchange(url, HttpMethod.POST, requestEntity, List.class);
        return resp.getBody();
    }

    public List getSelectingBasicQuizzesWithUserAnswer(String ids, Long assignmentId, Long studentId) {
        String url = quizCenter + "/api/v3/basicQuizzes/students/" + studentId + "/assignments/" + assignmentId + "/quizzes/" + ids;
        ResponseEntity<List> result = template.getForEntity(url, List.class);
        return result.getBody();
    }

    public Map getHomeworkQuizById(Long quizId) {
        String url = quizCenter + "/api/v3/homeworkQuizzes/" + quizId;
        ResponseEntity<Map> result = template.getForEntity(url, Map.class);
        return result.getBody();
    }

    public ResponseEntity getHomeworkAnswerFile(Long quizId) {
        String url = quizCenter + "/api/v3/homeworkQuizzes/" + quizId+"/answerFile";
        ResponseEntity result = template.getForEntity(url, String.class);
        return result;
    }

    public List getQuizzesAndAnswerByQuizIds(String ids, Long assignmentId, Long studentId, String type) {
        List quizzes = new ArrayList();
        if (ids.equals("")) {
            return quizzes;
        }
        if (type.equals("BASIC_QUIZ")) {
            quizzes = this.getSelectingBasicQuizzesWithUserAnswer(ids, assignmentId, studentId);
        } else if ("HOMEWORK_QUIZ".equals(type)) {
            quizzes = this.getSelectHomeworkWithUserAnswer(ids, assignmentId, studentId);
        } else if ("SUBJECTIVE_QUIZ".equals(type)) {
            quizzes = this.getSelectSubjectiveQuizzesWithUserAnswer(ids, assignmentId, studentId);
        }
        return quizzes;
    }

}
