package com.MADA.mada_SeoulBike.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
//대여소의 "정적인 정보" (위치, 이름, 전체 거치대 수 등, 변하지 않음)
@Entity
@Table(name = "bike_station")
@Data
public class BikeStation {
    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(name = "station_name")
    private String stationName;

    private Double latitude;
    private Double longitude;

    @Column(name = "rack_tot_cnt")
    private Integer rackTotCnt;
}
