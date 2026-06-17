package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT m.* FROM message m WHERE m.receiver_id = #{receiverId} ORDER BY m.created_at DESC")
    List<Message> findByReceiverId(Long receiverId);

    @Select("SELECT m.* FROM message m WHERE m.receiver_id = #{receiverId} AND m.is_read = false ORDER BY m.created_at DESC")
    List<Message> findUnreadByReceiverId(Long receiverId);

    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{receiverId} AND is_read = false")
    int countUnread(Long receiverId);

    @Select("SELECT m.* FROM message m WHERE (m.sender_id = #{userId} AND m.receiver_id = #{otherId}) OR (m.sender_id = #{otherId} AND m.receiver_id = #{userId}) ORDER BY m.created_at ASC")
    List<Message> findConversation(Long userId, Long otherId);
    
    @Select("<script>SELECT m.* FROM message m WHERE m.receiver_id = #{receiverId} AND m.sender_id IN <foreach collection='senderIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> ORDER BY m.created_at DESC</script>")
    List<Message> findMessagesFromMultipleSenders(Long receiverId, List<Long> senderIds);
}