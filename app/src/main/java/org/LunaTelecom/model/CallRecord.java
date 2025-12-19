package org.LunaTelecom.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CallRecord {
    private Long id;
    private String phoneNumber;
    private String calledNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration;
    private int endType;
}

/*
CREATE TABLE `call_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone_number` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL,
  `called_number` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `duration` int(10) DEFAULT 0, (以秒计)
  `end_type` int(10) DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_uca1400_ai_ci ROW_FORMAT = Dynamic;
*/