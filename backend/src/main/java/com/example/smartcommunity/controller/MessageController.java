package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.Message;
import com.example.smartcommunity.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ApiResponse<Message> sendMessage(@RequestBody Map<String, Object> params) {
        Long senderId = Long.parseLong(params.get("senderId").toString());
        Long receiverId = Long.parseLong(params.get("receiverId").toString());
        String content = params.get("content").toString();
        String type = params.get("type") != null ? params.get("type").toString() : null;
        Message message = messageService.sendMessage(senderId, receiverId, content, type);
        return ApiResponse.success(message);
    }

    @GetMapping("/receiver/{receiverId}")
    public ApiResponse<List<Message>> getMessagesByReceiver(@PathVariable Long receiverId) {
        List<Message> messages = messageService.getMessagesByReceiver(receiverId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/unread/{receiverId}")
    public ApiResponse<List<Message>> getUnreadMessages(@PathVariable Long receiverId) {
        List<Message> messages = messageService.getUnreadMessages(receiverId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/unread/count/{receiverId}")
    public ApiResponse<Map<String, Integer>> countUnread(@PathVariable Long receiverId) {
        int count = messageService.countUnread(receiverId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return ApiResponse.success(result);
    }

    @PutMapping("/{messageId}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ApiResponse.success("已标记为已读");
    }

    @PutMapping("/receiver/{receiverId}/read")
    public ApiResponse<String> markAllAsRead(@PathVariable Long receiverId) {
        messageService.markAllAsRead(receiverId);
        return ApiResponse.success("所有消息已标记为已读");
    }

    @GetMapping("/conversation")
    public ApiResponse<List<Message>> getConversation(
            @RequestParam Long userId,
            @RequestParam Long otherId) {
        List<Message> messages = messageService.getConversation(userId, otherId);
        return ApiResponse.success(messages);
    }
    
    @DeleteMapping("/clear")
    public ApiResponse<String> clearMessages() {
        messageService.clearAll();
        return ApiResponse.success("消息表已清空");
    }
}