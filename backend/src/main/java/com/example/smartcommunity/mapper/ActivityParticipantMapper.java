package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.ActivityParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivityParticipantMapper extends BaseMapper<ActivityParticipant> {

    @Select("SELECT * FROM activity_participant WHERE activity_id = #{activityId}")
    List<ActivityParticipant> findByActivityId(Long activityId);

    @Select("SELECT * FROM activity_participant WHERE user_id = #{userId}")
    List<ActivityParticipant> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM activity_participant WHERE activity_id = #{activityId} AND status = 'confirmed'")
    int countParticipants(Long activityId);

    @Select("SELECT * FROM activity_participant WHERE activity_id = #{activityId} AND user_id = #{userId}")
    ActivityParticipant findByActivityAndUser(Long activityId, Long userId);
}