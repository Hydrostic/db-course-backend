package org.LunaTelecom.dto.stats;

import lombok.Data;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
@Data
public class PackageSalesRow {
    @ColumnName("package_name")
    public String packageName;

    @ColumnName("sales")
    public Long sales;
}

