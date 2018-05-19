package cn.thoughtworks.school.programCenter.entities;

import javax.persistence.*;

@Entity
@Table(name = "quizSuggestion")
public class SectionSuggestion {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column
  private Long sectionId;
  @Column
  private Long studentId;
  @Column
  private String content;
  @Column
  private String createTime;

  private Long assignmentId;

  private Long quizId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSectionId() {
    return sectionId;
  }

  public void setSectionId(Long sectionId) {
    this.sectionId = sectionId;
  }

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public Long getAssignmentId() {
    return assignmentId;
  }

  public void setAssignmentId(Long assignmentId) {
    this.assignmentId = assignmentId;
  }

  public Long getQuizId() {
    return quizId;
  }

  public void setQuizId(Long quizId) {
    this.quizId = quizId;
  }
}
