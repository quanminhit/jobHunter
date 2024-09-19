package vn.hoidanit.jobhunter.domain.Response.JobDTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;
import java.util.*;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private String createdBy;
    private Instant createdAt;
    private List<String> skills;

}
