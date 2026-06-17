package com.example.smartcommunity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_relation", autoResultMap = true)
public class UserRelation {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long elderId;
    
    private Long familyMemberId;
    
    private String relationType;
    
    private Integer status;
    
    private LocalDateTime createdAt;
}