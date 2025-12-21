package org.LunaTelecom.model;

import lombok.Data;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.LocalDateTime;

@Data
public class PackageToNumber {
    private long id;
    private long phoneId;
    @ColumnName("package")
    private long pkg;
    private long price;
    private long callAmount;
    private long dataAmount;
    private long callUsage;
    private long dataUsage;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    // joined package name from packages table (optional)
    @ColumnName("package_name")
    private String packageName;
}
