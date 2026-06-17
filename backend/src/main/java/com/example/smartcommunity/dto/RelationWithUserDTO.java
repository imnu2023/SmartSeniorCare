package com.example.smartcommunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationWithUserDTO {
    
    private Long id;
    
    private Long elderId;
    
    private String elderName;
    
    private String elderPhone;
    
    private Long familyMemberId;
    
    private String familyMemberName;
    
    private String familyMemberPhone;
    
    private String relationType;
}
