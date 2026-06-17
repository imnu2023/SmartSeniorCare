package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Message;

import java.util.List;

public interface MessageService {

    Message sendMessage(Long senderId, Long receiverId, String content, String type);

    List<Message> getMessagesByReceiver(Long receiverId);

    List<Message> getUnreadMessages(Long receiverId);

    int countUnread(Long receiverId);

    void markAsRead(Long messageId);

    void markAllAsRead(Long receiverId);

    List<Message> getConversation(Long userId, Long otherId);
    
    List<Message> getMessagesForElder(Long elderId);
    
    List<Message> getMessagesForFamily(Long familyMemberId);
    
    void clearAll();
}