package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.Message;
import com.example.smartcommunity.mapper.MessageMapper;
import com.example.smartcommunity.service.MessageService;
import com.example.smartcommunity.service.UserRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserRelationService userRelationService;

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content, String type) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type != null ? type : "text");
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Message> getMessagesByReceiver(Long receiverId) {
        return messageMapper.findByReceiverId(receiverId);
    }

    @Override
    public List<Message> getUnreadMessages(Long receiverId) {
        return messageMapper.findUnreadByReceiverId(receiverId);
    }

    @Override
    public int countUnread(Long receiverId) {
        return messageMapper.countUnread(receiverId);
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message != null) {
            message.setIsRead(true);
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long receiverId) {
        List<Message> unreadMessages = messageMapper.findUnreadByReceiverId(receiverId);
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            messageMapper.updateById(message);
        }
    }

    @Override
    public List<Message> getConversation(Long userId, Long otherId) {
        return messageMapper.findConversation(userId, otherId);
    }
    
    @Override
    public List<Message> getMessagesForElder(Long elderId) {
        List<Long> familyIds = userRelationService.getFamilyMemberIds(elderId);
        if (familyIds.isEmpty()) {
            return new ArrayList<>();
        }
        return messageMapper.findMessagesFromMultipleSenders(elderId, familyIds);
    }
    
    @Override
    public List<Message> getMessagesForFamily(Long familyMemberId) {
        List<Long> elderIds = userRelationService.getElderIds(familyMemberId);
        if (elderIds.isEmpty()) {
            return new ArrayList<>();
        }
        return messageMapper.findMessagesFromMultipleSenders(familyMemberId, elderIds);
    }
    
    @Override
    @Transactional
    public void clearAll() {
        messageMapper.delete(null);
    }
}