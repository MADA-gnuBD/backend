package com.MADA.mada_SeoulBike.ddarungi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "bike_inventory")
@Data
//대여소의 "시간별 변하는 정보" (잔여 자전거 수, 현황, 기록성 데이터)
public class BikeInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_id")
    private String stationId;

    @Column(name = "parking_bike_tot_cnt")
    private Integer parkingBikeTotCnt;

    private Integer shared;

    private Double latitude;
    private Double longitude;

    @Column(name = "station_name")
    private String stationName;

    private LocalDateTime timestamp;
}
