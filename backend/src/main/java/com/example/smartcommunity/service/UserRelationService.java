package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.UserRelation;

import java.util.List;

public interface UserRelationService {
    
    UserRelation createRelation(Long elderId, Long familyMemberId, String relationType);
    
    void deleteRelation(Long id);
    
    void updateRelationStatus(Long id, Integer status);
    
    List<UserRelation> getRelationsByElder(Long elderId);
    
    List<UserRelation> getRelationsByFamilyMember(Long familyMemberId);
    
    UserRelation getRelation(Long elderId, Long familyMemberId);
    
    boolean existsRelation(Long elderId, Long familyMemberId);
    
    List<Long> getFamilyMemberIds(Long elderId);
    
    List<Long> getElderIds(Long familyMemberId);
    
    void cleanDuplicateRelations();
}