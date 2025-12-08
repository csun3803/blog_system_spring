package com.example.blog_system_spring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blogId")
    private Integer blogId;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "postTime")
    private LocalDateTime postTime;

    // 构造函数
    public Blog() {
        this.postTime = LocalDateTime.now();
    }

    // Getter和Setter（保持原项目字段名）
    public Integer getBlogId() {
        return blogId;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    // 兼容原项目的格式化时间方法
    public String getFormattedPostTime() {
        if (postTime == null) {
            return "";
        }
        // 格式化成原项目一样的格式：yyyy-MM-dd HH:mm:ss
        return postTime.toString().replace("T", " ");
    }

    // 可选：添加 toString() 方法便于调试
    @Override
    public String toString() {
        return "Blog{" +
                "blogId=" + blogId +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", postTime=" + postTime +
                '}';
    }
}