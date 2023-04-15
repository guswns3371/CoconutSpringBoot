package com.coconut.chat.domain.entity;

import com.coconut.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class UserChatHistory {

    @Id
    @GeneratedValue
    @Column(name = "user_chat_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public void remove() {
        user.getUserChatHistoryList().remove(this);
        chatHistory.getUserChatHistoryList().remove(this);
    }

}


