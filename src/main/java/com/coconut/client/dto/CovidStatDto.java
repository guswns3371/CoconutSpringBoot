package com.coconut.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CovidStatDto {

    private final String country; // 시도명
    private final String diffFromPrevDay; // 전일대비확진환자증감
    private final String total; // 확진환자수
    private final String death; // 사망자수
    private final String incidence; // 발병률
    private final String inspection; // 일일 검사환자 수

    @Builder
    public CovidStatDto(String country, String diffFromPrevDay, String total, String death, String incidence, String inspection) {
        this.country = country;
        this.diffFromPrevDay = diffFromPrevDay;
        this.total = total;
        this.death = death;
        this.incidence = incidence;
        this.inspection = inspection;
    }
}
