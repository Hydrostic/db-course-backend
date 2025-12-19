package org.LunaTelecom.dto.pack;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PackagePublic {
    public Long id;
    public String name;
    public String price;
    public Long callAmount;
    public Long dataAmount;
    public String createdAt;
    public String updatedAt;
}
