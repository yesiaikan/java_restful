package com.muli.utils.geo;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Subdivision;
import com.muli.utils.CommonUtils;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GEOIpLocation extends AbstractIpLocation {

    private static final Logger logger = LoggerFactory.getLogger(GEOIpLocation.class);

    private static DatabaseReader geoIPReader;

    @Inject
    private GEOIpLocation(@Named("cityGeoCenter") String cityGeoCenter,@Named("cityGeoMapper") String cityGeoMapper, @Named("provinceCapital") String provinceCapital, @Named("geoLite") String geoLite) {
        super(cityGeoCenter, provinceCapital,cityGeoMapper);
        loadDatabaseReader(geoLite);
    }

    private static void loadDatabaseReader(String geoLite) {
        try {
            File file = new File(CommonUtils.__CONF_DIR__, geoLite);
            InputStream is;
            if (!file.exists()) {
                URL url = ConfigurationUtils.locate(geoLite);
                logger.info("load geoLite2-city from:" + url);
                is = url.openStream();
            } else {
                logger.info("load geoLite2-city from:" + file.getAbsolutePath());
                is = new FileInputStream(file);
            }
            geoIPReader = new DatabaseReader.Builder(is).build();
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }
    }

    @Override
    public Map<String, String> getIpLocation(String ip) {
        Map<String, String> location = new HashMap<String, String>();
        try {
            CityResponse cityResponse = geoIPReader.city(InetAddress.getByName(ip));
            City city = cityResponse.getCity();
            Subdivision subdivision = cityResponse.getMostSpecificSubdivision();
            String provinceName = subdivision.getNames().get("zh-CN");
            String cityName = city.getNames().get("zh-CN");
            String key = null;
            if (null != provinceName) {
                if (provinceName.contains("北京")) {
                    key = "北京市";
                    provinceName = "北京市";
                    cityName = "北京市";
                } else if (provinceName.contains("天津")) {
                    key = "天津市";
                    provinceName = "天津市";
                    cityName = "天津市";
                } else if (provinceName.contains("上海")) {
                    key = "上海市";
                    provinceName = "上海市";
                    cityName = "上海市";
                } else if (provinceName.contains("重庆")) {
                    key = "重庆市";
                    provinceName = "重庆市";
                    cityName = "重庆市";
                } else if (provinceName.contains("香港")) {
                    key = "香港特别行政区";
                    provinceName = "香港特别行政区";
                    cityName = "香港特别行政区";
                } else if (provinceName.contains("澳门")) {
                    key = "澳门特别行政区";
                    provinceName = "澳门特别行政区";
                    cityName = "澳门特别行政区";
                } else if (provinceName.contains("台湾")) {
                    key = "台湾省";
                    provinceName = "台湾省";
                    cityName = "台湾省";
                } else if (provinceName.contains("新疆")) {
                    provinceName = "新疆维吾尔自治区";
                } else if (provinceName.contains("宁夏")) {
                    provinceName = "宁夏回族自治区";
                } else if (provinceName.contains("广西")) {
                    provinceName = "广西壮族自治区";
                } else if (provinceName.contains("西藏")) {
                    provinceName = "西藏自治区";
                } else if (provinceName.contains("内蒙古")) {
                    provinceName = "内蒙古自治区";
                }
            }

            if (!StringUtils.isEmpty(provinceName) && StringUtils.isEmpty(cityName)) {
                cityName = getProvinceCaptical(provinceName);
            }
            if (null != provinceName && null == cityName) {
                provinceName += "省";
                cityName = getProvinceCaptical(provinceName);
            }
            if (key == null) {
                key = provinceName + cityName;
            }
            int flag = 0;
            if (null == getCityGeoCenterLanLon(key)) {
                provinceName += "省";
                key = provinceName + cityName;
            } else {
                flag = 1;
            }
            if (flag == 0 && null == getCityGeoCenterLanLon(key)) {
                provinceName = provinceName.substring(0, provinceName.length() - 1);
                cityName += "市";
                key = provinceName + cityName;
            } else {
                flag = 1;
            }
            if (flag == 0 && null == getCityGeoCenterLanLon(key)) {
                provinceName += "省";
                key = provinceName + cityName;
            } else {
                flag = 1;
            }
            if (flag == 1 || null != getCityGeoCenterLanLon(key)) {
                location.put("province", provinceName);
                location.put("city", cityName);
            }
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }
        return location;
    }

}
