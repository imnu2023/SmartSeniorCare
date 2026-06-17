package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.RelationWithUserDTO;
import com.example.smartcommunity.entity.User;
import com.example.smartcommunity.entity.UserRelation;
import com.example.smartcommunity.mapper.UserRelationMapper;
import com.example.smartcommunity.service.UserRelationService;
import com.example.smartcommunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRelationServiceImpl implements UserRelationService {
    
    private final UserRelationMapper userRelationMapper;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public UserRelation createRelation(Long elderId, Long familyMemberId, String relationType) {
        UserRelation existing = userRelationMapper.findByElderAndFamily(elderId, familyMemberId);
        if (existing != null) {
            existing.setStatus(1);
            userRelationMapper.updateById(existing);
            return existing;
        }
        
        UserRelation relation = new UserRelation();
        relation.setElderId(elderId);
        relation.setFamilyMemberId(familyMemberId);
        relation.setRelationType(relationType);
        relation.setStatus(1);
        relation.setCreatedAt(LocalDateTime.now());
        userRelationMapper.insert(relation);
        return relation;
    }
    
    @Override
    public void deleteRelation(Long id) {
        userRelationMapper.deleteById(id);
    }
    
    @Override
    public void updateRelationStatus(Long id, Integer status) {
        UserRelation relation = userRelationMapper.selectById(id);
        if (relation != null) {
            relation.setStatus(status);
            userRelationMapper.updateById(relation);
        }
    }
    
    @Override
    public List<UserRelation> getRelationsByElder(Long elderId) {
        return userRelationMapper.findByElderId(elderId);
    }
    
    @Override
    public List<UserRelation> getRelationsByFamilyMember(Long familyMemberId) {
        return userRelationMapper.findByFamilyMemberId(familyMemberId);
    }
    
    @Override
    public UserRelation getRelation(Long elderId, Long familyMemberId) {
        return userRelationMapper.findByElderAndFamily(elderId, familyMemberId);
    }
    
    @Override
    public boolean existsRelation(Long elderId, Long familyMemberId) {
        UserRelation relation = userRelationMapper.findByElderAndFamily(elderId, familyMemberId);
        return relation != null && relation.getStatus() == 1;
    }
    
    @Override
    public List<Long> getFamilyMemberIds(Long elderId) {
        return userRelationMapper.findFamilyMemberIdsByElderId(elderId);
    }
    
    @Override
    public List<Long> getElderIds(Long familyMemberId) {
        return userRelationMapper.findElderIdsByFamilyMemberId(familyMemberId);
    }
    
    public List<RelationWithUserDTO> getRelationsWithUserByElder(Long elderId) {
        List<UserRelation> relations = userRelationMapper.findByElderId(elderId);
        List<RelationWithUserDTO> result = new ArrayList<>();
        
        for (UserRelation relation : relations) {
            RelationWithUserDTO dto = new RelationWithUserDTO();
            dto.setId(relation.getId());
            dto.setElderId(relation.getElderId());
            dto.setFamilyMemberId(relation.getFamilyMemberId());
            dto.setRelationType(relation.getRelationType());
            
            User elder = userService.getUserById(relation.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
                dto.setElderPhone(elder.getPhone());
            }
            
            User familyMember = userService.getUserById(relation.getFamilyMemberId());
            if (familyMember != null) {
                dto.setFamilyMemberName(familyMember.getName());
                dto.setFamilyMemberPhone(familyMember.getPhone());
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    public List<RelationWithUserDTO> getRelationsWithUserByFamilyMember(Long familyMemberId) {
        List<UserRelation> relations = userRelationMapper.findByFamilyMemberId(familyMemberId);
        List<RelationWithUserDTO> result = new ArrayList<>();
        
        for (UserRelation relation : relations) {
            RelationWithUserDTO dto = new RelationWithUserDTO();
            dto.setId(relation.getId());
            dto.setElderId(relation.getElderId());
            dto.setFamilyMemberId(relation.getFamilyMemberId());
            dto.setRelationType(relation.getRelationType());
            
            User elder = userService.getUserById(relation.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
                dto.setElderPhone(elder.getPhone());
            }
            
            User familyMember = userService.getUserById(relation.getFamilyMemberId());
            if (familyMember != null) {
                dto.setFamilyMemberName(familyMember.getName());
                dto.setFamilyMemberPhone(familyMember.getPhone());
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public void cleanDuplicateRelations() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("CREATE TEMPORARY TABLE temp_relations AS SELECT MIN(id) as id FROM user_relation GROUP BY elder_id, family_member_id");
        jdbcTemplate.execute("DELETE FROM user_relation WHERE id NOT IN (SELECT id FROM temp_relations)");
        jdbcTemplate.execute("DROP TEMPORARY TABLE IF EXISTS temp_relations");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
    }
}