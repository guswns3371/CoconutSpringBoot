package com.coconut.client.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EchoModel {

    private String echo;
    private String userIndex;

    @Builder
    public EchoModel(String echo, String userIndex) {
        this.echo = echo;
        this.userIndex = userIndex;
    }
}
