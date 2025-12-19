package org.LunaTelecom.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DataRecord {
    private Long id;
    private String phoneNumber;
    private LocalDate date;
    private Double usage;
}

/*
CREATE TABLE `data_usage_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT
  `phone_number` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL,
  `record_date` date NOT NULL,
  `usage` DECIMAL(10,2),
  PRIMARY KEY (`id`) USING BTREE  
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_uca1400_ai_ci ROW_FORMAT = Dynamic;
*/