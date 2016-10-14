package com.muli.utils.geo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.muli.utils.CommonUtils;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * <pre>
 * 用来读取QQwry.dat文件，以根据ip获得好友位置，QQwry.dat的格式是
 * 一. 文件头，共8字节
 *    1. 第一个起始IP的绝对偏移， 4字节
 *     2. 最后一个起始IP的绝对偏移， 4字节
 * 二. &quot;结束地址/国家/区域&quot;记录区
 *     四字节ip地址后跟的每一条记录分成两个部分
 *     1. 国家记录
 *     2. 地区记录
 *     但是地区记录是不一定有的。而且国家记录和地区记录都有两种形式
 *     1. 以0结束的字符串
 *     2. 4个字节，一个字节可能为0x1或0x2
 *   a. 为0x1时，表示在绝对偏移后还跟着一个区域的记录，注意是绝对偏移之后，而不是这四个字节之后
 *        b. 为0x2时，表示在绝对偏移后没有区域记录
 *        不管为0x1还是0x2，后三个字节都是实际国家名的文件内绝对偏移
 *   如果是地区记录，0x1和0x2的含义不明，但是如果出现这两个字节，也肯定是跟着3个字节偏移，如果不是
 *        则为0结尾字符串
 * 三. &quot;起始地址/结束地址偏移&quot;记录区
 *     1. 每条记录7字节，按照起始地址从小到大排列
 *        a. 起始IP地址，4字节
 *        b. 结束ip地址的绝对偏移，3字节
 * 
 * 注意，这个文件里的ip地址和所有的偏移量均采用little-endian格式，而java是采用
 * big-endian格式的，要注意转换
 * </pre>
 * 
 * 
 * Created by l430 on 2014/7/4.
 */
@Singleton
public class QQIpLocation extends AbstractIpLocation {

    private static final Logger logger = LoggerFactory.getLogger(QQIpLocation.class);

    // 一些固定常量，比如记录长度等等
    private static final int IP_RECORD_LENGTH = 7;
    private static final byte AREA_FOLLOWED = 0x01;
    private static final byte NO_AREA = 0x02;

    // 用来做为cache，查询一个ip时首先查看cache，以减少不必要的重复查找
    private Map<String, String> ipCache;
    // 随机文件访问类
    private RandomAccessFile ipFile;

    // 起始地区的开始和结束的绝对偏移
    private long ipBegin, ipEnd;
    // 为提高效率而采用的变量
    private byte[] buf;
    private byte[] b4;
    private byte[] b3;

    private Map<String, String> cityMapQQToCellMap;

    /**
     * 私有构造函数
     */
    @Inject
    private QQIpLocation(@Named("cityGeoCenter") String cityGeoCenter,@Named("cityGeoMapper") String cityGeoMapper, @Named("provinceCapital") String provinceCapital, @Named("qqwry") String qqwry, @Named("cityMapQQToCell") String cityMapQQToCell) {
        super(cityGeoCenter, provinceCapital,cityGeoMapper);
        loadQQWry(qqwry);
        loadCityMapQQToCell(cityMapQQToCell);
    }

    private void loadQQWry(String qqwry) {
        ipCache = new Hashtable<String, String>();
        buf = new byte[100];
        b4 = new byte[4];
        b3 = new byte[3];
        try {
            File file = new File(CommonUtils.__CONF_DIR__, qqwry);
            if (!file.exists()) {
                URL url = ConfigurationUtils.locate(qqwry);
                logger.info("load qqwry from:" + url);
                file = new File(url.getFile());
            } else {
                logger.info("load qqwry from:" + file.getAbsolutePath());
            }
            ipFile = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }

        // 如果打开文件成功，读取文件头信息
        if (ipFile != null) {
            try {
                ipBegin = readLong4(0);
                ipEnd = readLong4(4);
                if (ipBegin == -1 || ipEnd == -1) {
                    ipFile.close();
                    ipFile = null;
                }
            } catch (IOException e) {
                logger.warn("IP地址信息文件格式有错误");
                ipFile = null;
            }
        }
    }

    private void loadCityMapQQToCell(String cityMapQQToCell) {
        cityMapQQToCellMap = new HashMap<String, String>();
        try {
            File file = new File(CommonUtils.__CONF_DIR__, cityMapQQToCell);
            InputStream is;
            if (!file.exists()) {
                URL url = ConfigurationUtils.locate(cityMapQQToCell);
                logger.info("load cityMapQQToCell from:" + url);
                is = url.openStream();
            } else {
                logger.info("load cityMapQQToCell from:" + file.getAbsolutePath());
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
                cityMapQQToCellMap.put(split[0], split[1]);
            }
            reader.close();
        } catch (Exception e) {
            logger.warn(e.toString(), e);
        }
    }

    /**
     * 根据IP得到地址
     * 
     * @param ip
     *            ip的字节数组形式
     * @return 地址名字符串
     */
    private String getAddress(byte[] ip) {
        String address;
        // 检查ip地址文件是否正常
        if (ipFile == null)
            return null;
        // 保存ip，转换ip字节数组为字符串形式
        String ipStr = getIpStringFromBytes(ip);
        // 先检查cache中是否已经包含有这个ip的结果，没有再搜索文件
        if (ipCache.containsKey(ipStr)) {
            address = (String) ipCache.get(ipStr);
        } else {
            address = getLocation(ip);
            ipCache.put(ipStr, address);
        }
        address = address.equals(" CZ88.NET") ? "" : address;
        return address.trim();
    }

    /**
     * 根据IP得到地址
     * 
     * @param ip
     *            IP的字符串形式
     * @return 地址名字符串
     */
    private String getAddress(String ip) {
        return getAddress(getIpByteArrayFromString(ip));
    }

    /**
     * 根据ip搜索ip信息文件,返回地址
     * 
     * @param ip
     *            要查询的IP
     * @return 地址
     */
    private String getLocation(byte[] ip) {
        String result = null;
        long offset = locateIP(ip);
        if (offset != -1) {
            result = getLocation(offset);
        }
        return result;
    }

    /**
     * 从offset位置读取4个字节为一个long，因为java为big-endian格式，所以没办法 用了这么一个函数来做转换
     * 
     * @param offset
     * @return 读取的long值，返回-1表示读取文件失败
     */
    private long readLong4(long offset) {
        long ret = 0;
        try {
            ipFile.seek(offset);
            ret |= (ipFile.readByte() & 0xFF);
            ret |= ((ipFile.readByte() << 8) & 0xFF00);
            ret |= ((ipFile.readByte() << 16) & 0xFF0000);
            ret |= ((ipFile.readByte() << 24) & 0xFF000000);
            return ret;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 从offset位置读取3个字节为一个long，因为java为big-endian格式，所以没办法 用了这么一个函数来做转换
     * 
     * @param offset
     * @return 读取的long值，返回-1表示读取文件失败
     */
    private long readLong3(long offset) {
        long ret = 0;
        try {
            ipFile.seek(offset);
            ipFile.readFully(b3);
            ret |= (b3[0] & 0xFF);
            ret |= ((b3[1] << 8) & 0xFF00);
            ret |= ((b3[2] << 16) & 0xFF0000);
            return ret;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 从当前位置读取3个字节转换成long
     * 
     * @return
     */
    private long readLong3() {
        long ret = 0;
        try {
            ipFile.readFully(b3);
            ret |= (b3[0] & 0xFF);
            ret |= ((b3[1] << 8) & 0xFF00);
            ret |= ((b3[2] << 16) & 0xFF0000);
            return ret;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 从offset位置读取四个字节的ip地址放入ip数组中，读取后的ip为big-endian格式，但是 文件中是little-endian形式，将会进行转换
     * 
     * @param offset
     * @param ip
     */
    private void readIP(long offset, byte[] ip) {
        try {
            ipFile.seek(offset);
            ipFile.readFully(ip);
            byte temp = ip[0];
            ip[0] = ip[3];
            ip[3] = temp;
            temp = ip[1];
            ip[1] = ip[2];
            ip[2] = temp;
        } catch (IOException e) {
            logger.debug(e.toString(), e);
        }
    }

    /**
     * 把类成员ip和beginIp比较，注意这个beginIp是big-endian的
     * 
     * @param ip
     *            要查询的IP
     * @param beginIp
     *            和被查询IP相比较的IP
     * @return 相等返回0，ip大于beginIp则返回1，小于返回-1。
     */
    private int compareIP(byte[] ip, byte[] beginIp) {
        for (int i = 0; i < 4; i++) {
            int r = compareByte(ip[i], beginIp[i]);
            if (r != 0)
                return r;
        }
        return 0;
    }

    /**
     * 把两个byte当作无符号数进行比较
     * 
     * @param b1
     * @param b2
     * @return 若b1大于b2则返回1，相等返回0，小于返回-1
     */
    private int compareByte(byte b1, byte b2) {
        if ((b1 & 0xFF) > (b2 & 0xFF)) // 比较是否大于
            return 1;
        else if ((b1 ^ b2) == 0)// 判断是否相等
            return 0;
        else
            return -1;
    }

    /**
     * 这个方法将根据ip的内容，定位到包含这个ip国家地区的记录处，返回一个绝对偏移 方法使用二分法查找。
     * 
     * @param ip
     *            要查询的IP
     * @return 如果找到了，返回结束IP的偏移，如果没有找到，返回-1
     */
    private long locateIP(byte[] ip) {
        long m = 0;
        int r;
        // 比较第一个ip项
        readIP(ipBegin, b4);
        r = compareIP(ip, b4);
        if (r == 0)
            return ipBegin;
        else if (r < 0)
            return -1;
        // 开始二分搜索
        for (long i = ipBegin, j = ipEnd; i < j;) {
            m = getMiddleOffset(i, j);
            readIP(m, b4);
            r = compareIP(ip, b4);
            // log.debug(Utils.getIpStringFromBytes(b));
            if (r > 0)
                i = m;
            else if (r < 0) {
                if (m == j) {
                    j -= IP_RECORD_LENGTH;
                    m = j;
                } else
                    j = m;
            } else
                return readLong3(m + 4);
        }
        // 如果循环结束了，那么i和j必定是相等的，这个记录为最可能的记录，但是并非
        // 肯定就是，还要检查一下，如果是，就返回结束地址区的绝对偏移
        m = readLong3(m + 4);
        readIP(m, b4);
        r = compareIP(ip, b4);
        if (r <= 0)
            return m;
        else
            return -1;
    }

    /**
     * 得到begin偏移和end偏移中间位置记录的偏移
     * 
     * @param begin
     * @param end
     * @return
     */
    private long getMiddleOffset(long begin, long end) {
        long records = (end - begin) / IP_RECORD_LENGTH;
        records >>= 1;
        if (records == 0)
            records = 1;
        return begin + records * IP_RECORD_LENGTH;
    }

    /**
     * 给定一个ip地址记录的偏移
     * 
     * @param offset
     * @return
     */
    private String getLocation(long offset) {
        String result = null;
        try {
            // 跳过4字节ip
            ipFile.seek(offset + 4);
            // 读取第一个字节判断是否标志字节
            byte b = ipFile.readByte();
            if (b == AREA_FOLLOWED) {
                // 读取国家偏移
                long countryOffset = readLong3();
                // 跳转至偏移处
                ipFile.seek(countryOffset);
                // 再检查一次标志字节，因为这个时候这个地方仍然可能是个重定向
                b = ipFile.readByte();
                if (b == NO_AREA) {
                    result = readString(readLong3());
                    ipFile.seek(countryOffset + 4);
                } else {
                    result = readString(countryOffset);
                }
            } else if (b == NO_AREA) {
                result = readString(readLong3());
            } else {
                result = readString(ipFile.getFilePointer() - 1);
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从offset偏移处读取一个以0结束的字符串
     * 
     * @param offset
     * @return 读取的字符串，出错返回空字符串
     */
    private String readString(long offset) {
        try {
            ipFile.seek(offset);
            int i;
            for (i = 0, buf[i] = ipFile.readByte(); buf[i] != 0; buf[++i] = ipFile.readByte()) {
                if (i >= buf.length - 1) {
                    byte[] tmp = new byte[i + 100];
                    System.arraycopy(buf, 0, tmp, 0, i);
                    buf = tmp;
                }
            }
            if (i != 0)
                return getString(buf, 0, i, "GBK");
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 从ip的字符串形式得到字节数组形式
     * 
     * @param ip
     *            字符串形式的ip
     * @return 字节数组形式的ip
     */
    private byte[] getIpByteArrayFromString(String ip) {
        byte[] ret = new byte[4];
        java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
        try {
            ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 根据某种编码方式将字节数组转换成字符串
     * 
     * @param b
     *            字节数组
     * @param offset
     *            要转换的起始位置
     * @param len
     *            要转换的长度
     * @param encoding
     *            编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    private String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }

    /**
     * @param ip
     *            ip的字节数组形式
     * @return 字符串形式的ip
     */
    private String getIpStringFromBytes(byte[] ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(ip[0] & 0xFF);
        sb.append('.');
        sb.append(ip[1] & 0xFF);
        sb.append('.');
        sb.append(ip[2] & 0xFF);
        sb.append('.');
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }

    @Override
    public Map<String, String> getIpLocation(String ip) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            String address = getAddress(ip);
            String provinceName = null;
            String cityName = null;
            String key = null;
            if (address != null) {
                if (address.contains("北京")) {
                    key = "北京市";
                    provinceName = "北京市";
                    cityName = "北京市";
                } else if (address.contains("天津")) {
                    key = "天津市";
                    provinceName = "天津市";
                    cityName = "天津市";
                } else if (address.contains("上海")) {
                    key = "上海市";
                    provinceName = "上海市";
                    cityName = "上海市";
                } else if (address.contains("重庆")) {
                    key = "重庆市";
                    provinceName = "重庆市";
                    cityName = "重庆市";
                } else if (address.contains("香港")) {
                    key = "香港特别行政区";
                    provinceName = "香港特别行政区";
                    cityName = "香港特别行政区";
                } else if (address.contains("澳门")) {
                    key = "澳门特别行政区";
                    provinceName = "澳门特别行政区";
                    cityName = "澳门特别行政区";
                } else if (address.contains("台湾")) {
                    key = "台湾省";
                    provinceName = "台湾省";
                    cityName = "台湾省";
                } else if (address.contains("新疆")) {
                    provinceName = "新疆维吾尔自治区";
                    cityName = address.substring(2);
                } else if (address.contains("宁夏")) {
                    provinceName = "宁夏回族自治区";
                    cityName = address.substring(2);
                } else if (address.contains("广西")) {
                    provinceName = "广西壮族自治区";
                    cityName = address.substring(2);
                } else if (address.contains("西藏")) {
                    provinceName = "西藏自治区";
                    cityName = address.substring(2);
                } else if (address.contains("内蒙古")) {
                    provinceName = "内蒙古自治区";
                    cityName = address.substring(3);
                } else if (address.startsWith("吉林")) {
                    // 有很多吉林省的记录错写成吉林市了，如“吉林市延边州”
                    provinceName = "吉林省";
                    cityName = address.substring(3);
                } else {
                    provinceName = address.substring(0, address.indexOf("省") + 1);
                    cityName = address.substring(address.indexOf("省") + 1);
                }
                if (!StringUtils.isEmpty(provinceName) && StringUtils.isEmpty(cityName)) {
                    cityName = getProvinceCaptical(provinceName);
                }
                if (cityMapQQToCellMap.containsKey("cityName")) {
                    cityName = cityMapQQToCellMap.get("cityName");
                }
                if (key == null) {
                    key = provinceName + cityName;
                }
                String CityBak = cityName;
                int flag = 0;
                if (null == getCityGeoCenterLanLon(key)) {
                    if (CityBak.indexOf("州") > 0) {
                        cityName = CityBak.substring(0, cityName.indexOf("州") + 1);
                        if (cityMapQQToCellMap.containsKey("cityName")) {
                            cityName = cityMapQQToCellMap.get("cityName");
                        }
                        key = provinceName + cityName;
                    }
                } else {
                    flag = 1;
                }
                if (flag == 0 && null == getCityGeoCenterLanLon(key)) {
                    if (CityBak.indexOf("市") > 0) {
                        cityName = CityBak.substring(0, cityName.indexOf("市") + 1);
                        if (cityMapQQToCellMap.containsKey("cityName")) {
                            cityName = cityMapQQToCellMap.get("cityName");
                        }
                        key = provinceName + cityName;
                    }
                } else {
                    flag = 1;
                }
                if (flag == 0 && null == getCityGeoCenterLanLon(key)) {
                    if (CityBak.indexOf("地区") > 0) {
                        cityName = CityBak.substring(0, cityName.indexOf("地区") + 2);
                        key = provinceName + cityName;
                    }
                } else {
                    flag = 1;
                }
                if (flag == 0 && null == getCityGeoCenterLanLon(key)) {
                    if (CityBak.indexOf("盟") > 0) {
                        cityName = CityBak.substring(0, cityName.indexOf("盟") + 1);
                        key = provinceName + cityName;
                    }
                } else {
                    flag = 1;
                }
                /**
                 * 如果省后面有一个不在城市列表中的市，用该省省会代替
                 */
                if (flag == 0 && null == getCityGeoCenterLanLon(key)) {
                    cityName = getProvinceCaptical(provinceName);
                    key = provinceName + cityName;
                } else {
                    flag = 1;
                }
                if (flag == 1 || null != getCityGeoCenterLanLon(key)) {
                    result.put("province", provinceName);
                    result.put("city", cityName);
                }
            }
        } catch (Exception e) {
            logger.debug(e.toString(), e);
        }
        return result;
    }
}
