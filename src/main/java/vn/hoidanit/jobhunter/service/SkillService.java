package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skills;
import vn.hoidanit.jobhunter.domain.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillsRepository;
import java.util.*;

@Service
public class SkillService {
    private final SkillsRepository skillRepository;

    public SkillService(SkillsRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skills fetchSkillById(long id) {
        Optional<Skills> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent())
            return skillOptional.get();
        return null;
    }

    public Skills createSkill(Skills s) {
        return this.skillRepository.save(s);
    }

    public Skills updateSkills(Skills s) {
        return this.skillRepository.save(s);
    }

    public void deleteSkills(long id) {
        // delete job (inside job_Skills table)
        Optional<Skills> skillOptional = this.skillRepository.findById(id);
        Skills currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.delete(currentSkill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skills> spec, Pageable pageable) {
        Page<Skills> pageUser = this.skillRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageUser.getContent());

        return rs;
    }
}
