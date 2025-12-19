package org.LunaTelecom.dto.records;

import java.util.List;
import lombok.Data;

@Data
public class ListCallRecordResponse {
    private Long id;
    private String phoneNumber;
    private List<String> callPair;
    private int endType;
}
