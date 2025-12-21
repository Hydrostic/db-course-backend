package org.LunaTelecom.dao;

import org.LunaTelecom.model.Package;
import org.LunaTelecom.model.PackageToNumber;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;
import java.util.List;

public interface PackageDAO {
    @SqlQuery("SELECT COUNT(*) FROM packages WHERE deleted_at IS NULL")
    long count();

    @SqlQuery("SELECT * FROM packages WHERE id = :id AND deleted_at IS NULL")
    @RegisterBeanMapper(Package.class)
    Package findById(@Bind("id") long id);

    @SqlQuery("SELECT * FROM packages WHERE deleted_at IS NULL LIMIT :offset, :limit")
    @RegisterBeanMapper(Package.class)
    List<Package> list(@Bind("offset") long offset, @Bind("limit") int limit);

    @SqlUpdate("INSERT INTO packages (name, price, call_amount, data_amount, created_at, updated_at) VALUES (:name, :price, :callAmount, :dataAmount, NOW(), NOW())")
    @RegisterBeanMapper(Package.class)
    void insert(@BindBean Package pkg);

    @SqlUpdate("UPDATE packages SET name = :name, price = :price, call_amount = :callAmount, data_amount = :dataAmount, updated_at = NOW() WHERE id = :id AND deleted_at IS NULL")
    @RegisterBeanMapper(Package.class)
    boolean update(@BindBean Package pkg);
    @SqlUpdate("UPDATE packages SET deleted_at = NOW() WHERE id = :id AND deleted_at IS NULL")
    boolean delete(@Bind("id") long id);

    @SqlQuery("SELECT ptn.*, pkg.name AS package_name FROM package_to_number ptn LEFT JOIN packages pkg ON pkg.id = ptn.`package` WHERE ptn.phone_id = :phoneId")
    @RegisterBeanMapper(PackageToNumber.class)
    List<PackageToNumber> findPackagesByPhoneId(@Bind("phoneId") long phoneId);

    // Insert a package-to-number mapping
    @SqlUpdate("INSERT INTO package_to_number (phone_id, `package`, price, call_amount, data_amount, call_usage, data_usage, start_at, end_at) VALUES (:phoneId, :pkg, :price, :callAmount, :dataAmount, :callUsage, :dataUsage, :startAt, :endAt)")
    void insertPackageToNumber(@BindBean PackageToNumber packageToNumber);

    // Find package_to_number by id
    @SqlQuery("SELECT ptn.*, pkg.name AS package_name FROM package_to_number ptn LEFT JOIN packages pkg ON pkg.id = ptn.`package` WHERE ptn.id = :id LIMIT 1")
    @RegisterBeanMapper(PackageToNumber.class)
    PackageToNumber findPackageToNumberById(@Bind("id") long id);

    // Mark package_to_number as deleted
    @SqlUpdate("DELETE FROM package_to_number WHERE id = :id")
    boolean deletePackageToNumber(@Bind("id") long id);

    // Update end_at for a package_to_number entry
    @SqlUpdate("UPDATE package_to_number SET end_at = :endAt WHERE id = :id")
    boolean updatePackageToNumberEnd(@Bind("id") long id, @Bind("endAt") LocalDateTime endAt);

    /**
     * Return all currently active package-to-number mappings.
     * Active means: start_at <= now and (end_at is null OR end_at >= now).
     */
    @SqlQuery("SELECT * FROM package_to_number WHERE start_at <= :now AND (end_at IS NULL OR end_at >= :now)")
    @RegisterBeanMapper(PackageToNumber.class)
    List<PackageToNumber> listActiveMappings(@Bind("now") LocalDateTime now);

    @SqlUpdate("UPDATE package_to_number SET call_usage = 0, data_usage = 0")
    int resetAllUsage();

    @SqlUpdate("UPDATE package_to_number SET call_usage = 0, data_usage = 0 WHERE id = :id")
    int resetUsageById(@Bind("id") long id);

    @SqlQuery(
        "SELECT pkg.name AS package_name, COUNT(*) AS sales " +
        "FROM package_to_number ptn " +
        "JOIN packages pkg ON pkg.id = ptn.`package` " +
        "WHERE ptn.start_at >= :start AND ptn.start_at < :end " +
        "GROUP BY pkg.name"
    )
    @RegisterBeanMapper(org.LunaTelecom.dto.stats.PackageSalesRow.class)
    List<org.LunaTelecom.dto.stats.PackageSalesRow> countPackageSalesBetween(
            @Bind("start") LocalDateTime start,
            @Bind("end") LocalDateTime end);
}
