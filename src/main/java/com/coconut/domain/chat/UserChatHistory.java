package com.coconut.domain.chat;

import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@IdClass(UserChatHistoryId.class)
public class UserChatHistory {

    @Id
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Id
    @Column(name = "chat_history_id", insertable = false, updatable = false)
    private Long chatHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_history_id")
    private ChatHistory chatHistory;

    @Builder
    public UserChatHistory(User user, ChatHistory chatHistory) {
        setUser(user);
        setChatHistory(chatHistory);
    }

    public void setUser(User user) {
        this.user = user;
        if (!user.getUserChatHistoryList().contains(this)) {
            user.getUserChatHistoryList().add(this);
        }
    }

    public void setChatHistory(ChatHistory chatHistory) {
        this.chatHistory = chatHistory;
        if (!chatHistory.getUserChatHistoryList().contains(this)) {
            chatHistory.getUserChatHistoryList().add(this);
        }
    }

}

class UserChatHistoryId implements Serializable {
    private Long userId;
    private Long chatHistoryId;
}
