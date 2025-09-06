package com.ntt.lms.dto;

import com.ntt.lms.pojo.Grading;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradingDto {
    private int quiz_id;
    private List<String> answers ;
    private int student_id;
    private int grades;
    private List<Grading> allGrades;
}
