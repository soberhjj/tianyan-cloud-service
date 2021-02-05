package com.newland.tianyan.face.event.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserEvent {
    private Long appId;
    private String groupId;
    private String userId;
}
