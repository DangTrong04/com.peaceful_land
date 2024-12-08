package com.example.peaceful_land.Entity;

import com.example.peaceful_land.DTO.PostApprovalResponse;
import com.example.peaceful_land.DTO.ResponsePost;
import com.example.peaceful_land.DTO.ResponseUserPostReqView;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "requests_post")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RequestPost extends BaseEntity {

    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private LocalDate expiration;

    @Column
    private Boolean approved;

    @Column(name = "deny_message")
    private String denyMessage;

    @Override
    public String toString() {
        return "RequestPost{" +
                "id=" + id +
                ", post_id=" + post.getId() +
                ", expiration=" + expiration +
                ", approved=" + approved +
                ", post_visibility=" + !post.getHide() +
                '}';
    }

    public PostApprovalResponse parsePostApprovalResponse(){
        return PostApprovalResponse.builder()
                .id(getId())
                .requestDate(getDateBegin())
                .expiryDate(expiration)
                .approved(approved)
                .denyMessage(denyMessage)
                .postId(post.getId())
                .postTitle(post.getTitle())
                .propertyAddress(post.getProperty().getLocationDetail() + ", " + post.getProperty().getLocation())
                .postVisibility(!this.post.getHide())
                .build();
    }

    public ResponseUserPostReqView parseResponseUserPostReqView() {
        return ResponseUserPostReqView.builder()
                .id(getId())
                .data(ResponsePost.fromPost(post))
                .expiration(expiration)
                .approved(approved)
                .denyMessage(denyMessage)
                .createdAt(getDateBegin())
                .build();
    }
}