package org.LunaTelecom.dto.stats;

public class HomeStatsResponse {
    public long todayNewUsers;
    public long todayNewPhones;
    /** total income today in cents */
    public long todayIncome;

    // New: active phone count (based on today's call count threshold)
    public long todayActivePhones;

    // New: active phone distribution by province name
    public java.util.Map<String, Long> activePhonesByProvince;

    // New: monthly package sales stats, key is package name, value is sales count.
    public java.util.Map<String, Long> monthlyPackageSales;

    public HomeStatsResponse(long todayNewUsers, long todayNewPhones, long todayIncome,
                             long todayActivePhones, java.util.Map<String, Long> activePhonesByProvince,
                             java.util.Map<String, Long> monthlyPackageSales) {
        this.todayNewUsers = todayNewUsers;
        this.todayNewPhones = todayNewPhones;
        this.todayIncome = todayIncome;
        this.todayActivePhones = todayActivePhones;
        this.activePhonesByProvince = activePhonesByProvince;
        this.monthlyPackageSales = monthlyPackageSales;
    }
}
