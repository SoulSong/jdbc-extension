package com.shf.jdbc.extension.mybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 19:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String name;
}