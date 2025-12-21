package org.LunaTelecom.dto.stats;

import lombok.Data;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

/**
 * DAO row mapping for active phone region aggregation.
 */
@Data
public class RegionActiveCountRow {
    @ColumnName("region_code")
    public String regionCode;

    @ColumnName("active_count")
    public Long activeCount;
}
