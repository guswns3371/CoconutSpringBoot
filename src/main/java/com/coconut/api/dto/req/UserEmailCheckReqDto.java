package com.coconut.api.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// @AllArgsConstructor 하면 요청이 받아들여지지 않음.. 왜??
@NoArgsConstructor
public class UserEmailCheckReqDto {
    private String email;
}
