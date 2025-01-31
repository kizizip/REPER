package com.d109.reper.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Animation {

    @Id @GeneratedValue
    private Long animationId;

    private String keyword;

    private String animationUrl;

    public Animation(String keyword, String animationUrl) {
        this.keyword = keyword;
        this.animationUrl = animationUrl;
    }

}
