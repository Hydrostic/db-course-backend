package org.LunaTelecom.dto.pack;

import org.LunaTelecom.model.PackageToNumber;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatterBuilder;

public class PackageToNumberPublic {
    public Long id;
    public Long packageId;
    public Long phoneId;
    public String price;
    public Long callAmount;
    public Long dataAmount;
    public Long callUsage;
    public Long dataUsage;
    public String startAt;
    public String endAt;
    public String packageName;
    public PackageToNumberPublic(PackageToNumber packageToNumber) {
        var df = new DecimalFormat("#.##");
        var datef = new DateTimeFormatterBuilder().appendPattern("yyyy/MM/dd HH:mm:ss").toFormatter();
        this.id = packageToNumber.getId();
        this.packageId = packageToNumber.getPkg();
        this.phoneId = packageToNumber.getPhoneId();
        this.price = df.format((double)packageToNumber.getPrice() / 100);
        this.callAmount = packageToNumber.getCallAmount();
        this.dataAmount = packageToNumber.getDataAmount();
        this.callUsage = packageToNumber.getCallUsage();
        this.dataUsage = packageToNumber.getDataUsage();
        this.startAt = datef.format(packageToNumber.getStartAt());
        this.endAt = datef.format(packageToNumber.getEndAt());
        this.packageName = packageToNumber.getPackageName();

    }
}
