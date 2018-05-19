package cn.thoughtworks.school.programCenter.entities;

import javax.persistence.*;
import java.util.List;

@Table(name = "assignment")
@Entity
public class Assignment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long taskId;
  private String type;
  private String title;
  private String createTime;
  private Long creatorId;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "assignmentId")
  private List<AssignmentQuiz> selectedQuizzes;

  public List<AssignmentQuiz> getSelectedQuizzes() {
    return selectedQuizzes;
  }

  public void setSelectedQuizzes(List<AssignmentQuiz> selectedQuizzes) {
    this.selectedQuizzes = selectedQuizzes;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public Assignment() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}