package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.UserRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRelationMapper extends BaseMapper<UserRelation> {

    @Select("SELECT * FROM user_relation WHERE elder_id = #{elderId}")
    List<UserRelation> findByElderId(@Param("elderId") Long elderId);

    @Select("SELECT * FROM user_relation WHERE family_member_id = #{familyMemberId}")
    List<UserRelation> findByFamilyMemberId(@Param("familyMemberId") Long familyMemberId);

    @Select("SELECT * FROM user_relation WHERE elder_id = #{elderId} AND family_member_id = #{familyMemberId}")
    UserRelation findByElderAndFamily(@Param("elderId") Long elderId, @Param("familyMemberId") Long familyMemberId);

    @Select("SELECT family_member_id FROM user_relation WHERE elder_id = #{elderId}")
    List<Long> findFamilyMemberIdsByElderId(@Param("elderId") Long elderId);

    @Select("SELECT elder_id FROM user_relation WHERE family_member_id = #{familyMemberId}")
    List<Long> findElderIdsByFamilyMemberId(@Param("familyMemberId") Long familyMemberId);
}
