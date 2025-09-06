

package com.ntt.lms.pojo;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "question_type")
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private int typeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_name", nullable = false, length = 255)
    private QuestionType.QuestionTypeEnum typeName;

    @Override
    public String toString() {
        return "QuestionType{" +
                "typeId=" + typeId +
                ", typeName=" + typeName +
                '}';
    }

    public enum QuestionTypeEnum {
        MULTIPLE_CHOICE_SINGLE,
        MULTIPLE_CHOICE_MULTIPLE,
        FILL_IN_THE_BLANK,
        TRUE_FALSE,
        ESSAY,
        OPEN_ENDED;
    }


}
