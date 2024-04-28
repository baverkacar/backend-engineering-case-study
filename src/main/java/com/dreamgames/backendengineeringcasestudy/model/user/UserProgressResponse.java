package com.dreamgames.backendengineeringcasestudy.model.user;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserProgressResponse {
    private Long id;
    private Integer level;
    private Integer coins;
    private String country;
}
