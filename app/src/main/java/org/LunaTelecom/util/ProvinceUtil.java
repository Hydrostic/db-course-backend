package org.LunaTelecom.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class ProvinceUtil {
    public static enum RegionCode {

        BEIJING("11", "北京"),
        TIANJIN("12", "天津"),
        HEBEI("13", "河北"),
        SHANXI("14", "山西"),
        INNER_MONGOLIA("15", "内蒙古"),

        LIAONING("21", "辽宁"),
        JILIN("22", "吉林"),
        HEILONGJIANG("23", "黑龙江"),

        SHANGHAI("31", "上海"),
        JIANGSU("32", "江苏"),
        ZHEJIANG("33", "浙江"),
        ANHUI("34", "安徽"),
        FUJIAN("35", "福建"),
        JIANGXI("36", "江西"),
        SHANDONG("37", "山东"),

        HENAN("41", "河南"),
        HUBEI("42", "湖北"),
        HUNAN("43", "湖南"),
        GUANGDONG("44", "广东"),
        GUANGXI("45", "广西"),
        HAINAN("46", "海南"),

        CHONGQING("50", "重庆"),
        SICHUAN("51", "四川"),
        GUIZHOU("52", "贵州"),
        YUNNAN("53", "云南"),
        TIBET("54", "西藏"),

        SHAANXI("61", "陕西"),
        GANSU("62", "甘肃"),
        QINGHAI("63", "青海"),
        NINGXIA("64", "宁夏"),
        XINJIANG("65", "新疆"),

        TAIWAN("71", "台湾"),
        HONG_KONG("81", "香港"),
        MACAO("82", "澳门");

        private final String code;
        private final String name;

        RegionCode(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }


        private static final Map<String, RegionCode> CODE_MAP;

        static {
            Map<String, RegionCode> map = new HashMap<>();
            for (RegionCode region : values()) {
                map.put(region.code, region);
            }
            CODE_MAP = Collections.unmodifiableMap(map);
        }

        /**
         * 通过两位字符串查询地区名称
         *
         * @param code 两位字符串，如 "11"
         * @return 地区名称，若不存在返回 "未知"
         */
        public static String getNameByCode(String code) {
            RegionCode region = CODE_MAP.get(code);
            return region != null ? region.name : "未知";
        }

        /**
         * 更严格版本：不存在直接抛异常
         */
        public static String requireNameByCode(String code) {
            RegionCode region = CODE_MAP.get(code);
            if (region == null) {
                throw new IllegalArgumentException("Unknown region code: " + code);
            }
            return region.name;
        }
    }
}