package com.newland.tianyan.face.event.group;

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
public class GroupEvent {
    private Long appId;
    private String groupId;
}
