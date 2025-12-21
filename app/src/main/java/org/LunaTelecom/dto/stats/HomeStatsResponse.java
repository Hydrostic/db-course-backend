package org.LunaTelecom.dto.stats;

public class HomeStatsResponse {
    public long todayNewUsers;
    public long todayNewPhones;
    /** total income today in cents */
    public long todayIncome;

    public HomeStatsResponse(long todayNewUsers, long todayNewPhones, long todayIncome) {
        this.todayNewUsers = todayNewUsers;
        this.todayNewPhones = todayNewPhones;
        this.todayIncome = todayIncome;
    }
}

