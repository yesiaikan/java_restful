package com.muli.utils.geo;

import com.muli.utils.CommonUtils;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractIpLocation implements IpLocation {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIpLocation.class);

    private Map<String, Map<String, Double>> cityGeoCenterMap;

    private Map<String, String> provinceCapitalMap;

    private Map<String,String> cityGeoMapperMap;

    protected AbstractIpLocation(String cityGeoCenter, String provinceCapital,String cityGeoMapper) {
        loadCityGeoCenter(cityGeoCenter);
        loadProvinceCapital(provinceCapital);
        loadCityGeoMapper(cityGeoMapper);
    }

    public Map<String, Double> getCityGeoCenterLanLon(String key) {
        return cityGeoCenterMap.get(key);
    }

    public String getProvinceCaptical(String key) {
        return provinceCapitalMap.get(key);
    }

    private void loadProvinceCapital(String provinceCapital) {
        provinceCapitalMap = new HashMap<String, String>();
        try {
            File file = new File(CommonUtils.__CONF_DIR__, provinceCapital);
            InputStream is;
            if (!file.exists()) {
                URL url = ConfigurationUtils.locate(provinceCapital);
                logger.info("load provinceCapital from:" + url);
                is = url.openStream();
            } else {
                logger.info("load provinceCapital from:" + file.getAbsolutePath());
                is = new FileInputStream(file);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split("\\t");
                if (split.length != 2) {
                    continue;
                }
                provinceCapitalMap.put(split[0], split[1]);
            }
            reader.close();
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }
    }

    private void loadCityGeoCenter(String cityGeoCenter) {
        cityGeoCenterMap = new HashMap<String, Map<String, Double>>();
        String line=null;
        try {
            File file = new File(CommonUtils.__CONF_DIR__, cityGeoCenter);
            InputStream is;
            if (!file.exists()) {
                URL url = ConfigurationUtils.locate(cityGeoCenter);
                logger.info("load cityGeoCenter from:" + url);
                is = url.openStream();
            } else {
                logger.info("load cityGeoCenter from:" + file.getAbsolutePath());
                is = new FileInputStream(file);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split("\\t");
                if (split.length != 3) {
                    continue;
                }
                Map<String, Double> latLonMap = new HashMap<String, Double>();
                latLonMap.put("longitude", Double.valueOf(split[1]));
                latLonMap.put("latitude", Double.valueOf(split[2]));
                cityGeoCenterMap.put(split[0], latLonMap);
            }
            reader.close();
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }
    }

    private void loadCityGeoMapper(String cityGeoMapper) {
        cityGeoMapperMap = new HashMap<String, String>();
        String line=null;
        try {
            File file = new File(CommonUtils.__CONF_DIR__, cityGeoMapper);
            InputStream is;
            if (!file.exists()) {
                URL url = ConfigurationUtils.locate(cityGeoMapper);
                logger.info("load cityGeoMapper from:" + url);
                is = url.openStream();
            } else {
                logger.info("load cityGeoMapper from:" + file.getAbsolutePath());
                is = new FileInputStream(file);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split("\\t");
                if (split.length != 2) {
                    continue;
                }
                cityGeoMapperMap.put(split[0], split[1]);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(line);
            logger.warn(e.toString(), e);
        }
    }

    public Map<String, Object> formatLocation(String provinceName, String cityName) {
        Map<String, Object> location = new HashMap<String, Object>();
        if(StringUtils.isEmpty(provinceName)){
            return location;
        }
        try {
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
                }else if(!provinceName.endsWith("省")){
                    provinceName += "省";
                }
            }
            if (StringUtils.isEmpty(cityName)) {
                cityName = getProvinceCaptical(provinceName);
            }
            if (key == null) {
                key = provinceName + cityName;
            }
            Map<String,Double> lanlon = getCityGeoCenterLanLon(key);
            if(lanlon == null || lanlon.isEmpty()){
                if(cityGeoMapperMap.containsKey(cityName)){
                    cityName = cityGeoMapperMap.get(cityName);
                }else{
                    cityName = cityName+"市";
                }
                key = provinceName+cityName;
                lanlon = getCityGeoCenterLanLon(key);
            }
            if(lanlon == null || lanlon.isEmpty()){
                logger.info("latitude and longitude of {} not fount.",key);
                return location;
            }
            location.put("province", provinceName);
            location.put("city", cityName);
            location.putAll(lanlon);
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }
        return location;
    }
}
