package org.LunaTelecom.dto.records;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class AddCallRecordRequest {
    @NotEmpty
    public List<String> callPair;
    @NotNull
    public String startTime;
    @NotNull
    public String endTime;
    @PositiveOrZero
    public int endType;
}
