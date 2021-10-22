package com.coconut.utils.file;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PathNameBuilder {
    private String userIndex;
    private String fileOriginalName;
    private String chatRoomIndex;

    @Builder
    public PathNameBuilder(String userIndex, String fileOriginalName, String chatRoomIndex) {
        this.userIndex = userIndex;
        this.fileOriginalName = fileOriginalName;
        this.chatRoomIndex = chatRoomIndex;
    }

    public String getProfileImagePath() {
        return "u" + userIndex + "_profileImage_" + fileOriginalName;
    }

    public String getBackgroundImagePath() {
        return "u" + userIndex + "_backgroundImage_" + fileOriginalName;
    }

    public String getChatImagePath() {
        return "u" + userIndex + "_c" + chatRoomIndex + "_chatImage_" + fileOriginalName;
    }

}
