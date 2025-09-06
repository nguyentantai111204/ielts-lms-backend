package com.ntt.lms.pojo;

import lombok.Getter;
import lombok.Setter;

public enum StudentType {
    PRIMARY_STUDENT("Học sinh cấp 1"),
    SECONDARY_STUDENT("Học sinh cấp 2"),
    PARENT("Phụ huynh"),
    WORKING_PERSON("Người đi làm"),
    FRESHMAN("Sinh viên năm 1-2"),
    SENIOR_STUDENT("Sinh viên năm 3-4");

    private final String displayName;

    StudentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

