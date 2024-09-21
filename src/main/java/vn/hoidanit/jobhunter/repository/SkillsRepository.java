package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.*;
import vn.hoidanit.jobhunter.domain.Skill;

@Repository
public interface SkillsRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    List<Skill> findByIdIn(List<Long> idList);

    boolean existsByName(String name);
}
