package com.robosoft.admin.login.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chapter implements Comparable<Chapter>{
    private Integer chapterId;
    private Integer courseId;
    private Integer chapterNumber;
    private String chapterName;
    private String chapterDuration;
    private Boolean uploadStatus;

    public Chapter(String chapterDuration) {
        this.chapterDuration = chapterDuration;
    }


    @Override
    public int compareTo(Chapter o) {
        return this.getChapterId().compareTo(o.chapterId);
    }
}
