package com.muli.utils.geo;

import java.util.Map;

public interface IpLocation {

    public Map<String, String> getIpLocation(String ip);

    public Map<String, Double> getCityGeoCenterLanLon(String key);

    public String getProvinceCaptical(String key);

    public Map<String, Object> formatLocation(String provinceName, String cityName);

}
