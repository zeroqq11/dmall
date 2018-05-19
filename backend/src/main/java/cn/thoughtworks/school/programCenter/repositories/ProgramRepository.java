package cn.thoughtworks.school.programCenter.repositories;

import cn.thoughtworks.school.programCenter.entities.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program,Long>{
    Program findProgramByCategoryAndId(String category,Long programId);
}
