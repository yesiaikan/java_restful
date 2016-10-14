package com.muli.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.util.Series;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class CommonUtils {

    public static final String __CONF_DIR__ = "conf";


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    public static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();
//    public static final JsonFactory JSON_FACTORY = new JsonFactory(JACKSON_OBJECT_MAPPER);

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>();

    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };

    private static HashMap<String, String> tagMaps = new HashMap<String, String>();

    static Pattern mobilePattern = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\\\d{8}$",Pattern.CASE_INSENSITIVE);

    static Pattern blackCharactersPattern = Pattern.compile("啊|啦|操|爹|娘|婆|公公|猪|淫|傻|煞笔|哥|逼|朋友|弟|老|媳妇|孬蛋|爸|妈|奶奶|爷|姥|舅|叔|姨|贱|老公|丈夫|女儿|儿子|姑|甥|侄|表|蛋|小人|同学|同事|姐|妹|陌生|认识|熟悉|婊|骚|狗|鸭|奸|罪|黄色|工友|闺|不想|我日|水货|老师|知道|放屁|不祥|皮肉生意|无感|公话|街道|蒋|打|扔|草泥马|同性恋|没接|还不|晓得|刚发|没事|妈逼|狗叼|法伦|小吃|本地号|省区|日龙|陌|放屁|扔|打|凶手|举报|的|土鳖|锦记|开幕力量开口|向荣|回顾|一卜卜");
    
    static Pattern containPattern = Pattern.compile("骏佳保险|泸州老窖|六合彩|宏伟防盗门老板厨房电器专卖|吉安市保育院|上海赛途仪器仪表|南粤银行|广州正一品|搜狗公司|米老头|北京大学|为民幼儿园|老百姓|布丁酒店|微道云商|欧亚公司|远洋公司|拍拍网|电子狗|深圳市苓贯|妈妈网装修公司|深圳摩客商贸|上海奇鸿仪器仪表|壹药网|1号药网|广州恩倍高生物科技|益盟操盘手|bear煮蛋器|天翼电子商务|搬家|Booking.com|上海赛途仪器仪表|长安铃木|瑞丁国际|新东方|远洋公司|芬芳公司|皇家新娘照相館|华润新农医药|书店|家政|教育|百世汇通|商城|比亚迪|鑫方盛|驾校|耐克|交通银行|富士康|百丽|聚橙网|血液中心|歌华有线|公利医院|九通别克|昂立|华图|宽带|虎跃快客|人大附小|地产|美的|保险|理财|投资|客服|惠丰弹香食品|武侠|网游|速8|有限公司|打车|锦江之星|办事处|酒店|宾馆|售后|珍缘网|教育|惜缘网|老干部局|云顶之星|77动漫|华数|旅行社|申浙大众|卫生院|综改办|俱乐部|邮政局|沙颂钰|沙颂钰|计生办|王老吉|街道办|集团|蛋糕|举报中心|吉百利|婚纱|摄影|服务中心|龙泉之星|公寓|火锅|清真|餐饮|百合|学院|中介|快的|经销部|服装店|小吃城|维修|信用卡|可颂坊|广州老实人安装维修净水机冰箱|会所|推销|出租车|装修|股票|如来信息科技|尚学堂|老上海城隍庙|打印机|速贷邦|开发公司|有限责任|贸易公司|黄石捷德万达金卡|欧艾斯设施管理服务|人力资源|网通|杜邦|江门南安小汽车出租|哥弟服装公司|办事处|服务公司|热线|外婆家|易初莲花|老凤祥|申佰圣|哥倫比亞|一品香|旗舰店|鼓浪屿揽海听风|营业厅|快餐店|专卖店|开开杯奶茶|大嘴巴|汽车站|服务部|照明|服饰店|宝财鸭脖王|德化伊采化妆品|陌上花开|时哥电厨房|小吃店|地道店|外婆印象|大悦城|世博店|五角场店|KKMALL店|订座电话|金牌外婆家|我爱我家|地产|21世纪不动产|链家|上海房屋置换|快递|人事部|58同城|老板电器|藏珍阁|谷哥|京东|兄弟商会|搜狗|禽老大|天宫院|儿童医院|哥华|中国光大银行|居委会|同一首歌|理赔中心|服务电话|电视台|街道|亨得利|全国网络诈骗举报电话|萧邦手表|钟表|群众举报|钱柜|刘老根|知妈堂|烤鸭|餐厅|派出所|大鸭梨|巢妈团|植物医生|农民工工资拖欠问题举报|邮局|栏目|老城一锅|安安宝贝|母婴用品|科技园|中艺影校|Nissan|老佛爷|租赁|猫和老鼠童装|敬老院|TATA木门|淘宝店|哥弟服饰|老番街服装市场|幼儿园|马可波罗|农商银行|发表论文期刊|健客网|物流|北大青鸟|贷款|麦可爷爷|美赞臣|莫泰168|社保|哥弟服装|邦顿|酷狗|眼之悦|委员会|警所|联合利华|固安捷|富士施乐|淳通|党校|美丽妈妈|吉的堡|金山税务|哥伦比亚|天人租车|五芳斋|海派书画|VIPABC|vipabc|代表处|顶正小吃培训|猎头|鼎福|恒大3M|中华财经网|供应商|柯尔特|ATMEL|旅游网|LVMH|神豆妈咪|友谊股份|厚味香辣馆|卫生中心|报社|编辑部|管理所|北方人才|58人事|大铁勺|老人头|奥的斯|老宅院|高速管理|公路管理|管理局|兄弟装饰|体育协会|耐德仪表|妈妈乡冒菜|乙泰.万千|武装部|妈乐购|新华电脑|360妈妈网|送餐|物业办|东方红娘|外婆人家|绝味鸭脖|个体经营|货运部|分公司|百事通|装表|友邦|思顿药业|服务店|老年大学|卫生局|葫芦兄弟|妇女儿童中心医院|九龙医院|操盘手|国税|老年健康协会|平安银行|阳光美语|介绍所|公安局|管理中心|商务部|河津消费广场|福景鸿城|真金耐火材料|珊瑚海建筑景观表现|巴老三|市政局|建行|老板厨卫|综治办|水岸香樟|订餐|鸭河电厂|饭店|沙发厂|香格里拉|公园|保障局|老北京羊蝎子|中国民用航空省局机场问询");
    /**
     *
     Primitive Type	Size	Minimum Value	Maximum Value	Wrapper Type
     char	   16-bit  	   Unicode 0	   Unicode 216-1	   Character
     byte	   8-bit  	   -128	   +127	   Byte
     short	   16-bit  	   -215
     (-32,768)	   +215-1
     (32,767)	   Short
     int	   32-bit  	   -231
     (-2,147,483,648)	   +231-1
     (2,147,483,647)	   Integer
     long	   64-bit  	   -263
     (-9,223,372,036,854,775,808)	   +263-1
     (9,223,372,036,854,775,807)	   Long
     float	   32-bit  	   32-bit IEEE 754 floating-point numbers	   Float
     double	   64-bit  	   64-bit IEEE 754 floating-point numbers	   Double
     boolean	   1-bit  	   true or false	   Boolean
     void	   -----  	   -----  	   -----  	   Void
     */
    static {
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(char.class, Character.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(byte.class, Byte.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(short.class, Short.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(int.class, Integer.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(long.class, Long.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(float.class, Float.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(double.class, Double.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(void.class, Void.class);
        JACKSON_OBJECT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        tagMaps.put("taxi", "出租车");
        tagMaps.put("ktv", "ktv");
        tagMaps.put("ems", "中国邮政速递物流");
        tagMaps.put("sony", "索尼");
        tagMaps.put("dell", "戴尔");
        tagMaps.put("baidu", "百度");
        tagMaps.put("semir", "森马");
        tagMaps.put("ibm", "IBM");
        tagMaps.put("tcl", "TCL");
        tagMaps.put("kfc", "肯德基KFC");
        tagMaps.put("sgs", "SGS通标标准技术服务有限公司");
        tagMaps.put("lg", "LG集团");
        tagMaps.put("haier", "海尔");
        tagMaps.put("fesco", "北京外企人力资源服务有限公司");
        tagMaps.put("hr", "招聘hr");
        tagMaps.put("sohu", "搜狐");
        tagMaps.put("csdn", "CSDN");
        tagMaps.put("intel", "英特尔");
        tagMaps.put("bcdtravel", "BCD Travel公司");
        tagMaps.put("sinotrans", "中国外运集团");
        tagMaps.put("hotel", "宾馆");
        tagMaps.put("husky", "husky公司");
        tagMaps.put("htc", "宏达国际电子股份有限公司");
        tagMaps.put("zhaopin", "招聘hr");
        tagMaps.put("jiaxiao", "驾校");
        tagMaps.put("zara", "zara公司");
        tagMaps.put("recruitment", "招聘hr");
        tagMaps.put("interview", "招聘hr");
        tagMaps.put("emc", "EMC中国卓越研发集团");
        tagMaps.put("ibmhr", "IBM招聘hr");
        tagMaps.put("emchr", "EMC中国卓越研发集团招聘hr");
        tagMaps.put("headhunter", "猎头");
        tagMaps.put("avis", "avis汽车租赁公司");
        tagMaps.put("兼职>o<招工", "兼职招聘");
        tagMaps.put("nvidia", "NVIDIA");
        tagMaps.put("3m", "3M公司");
        tagMaps.put("airtel", "Airtel公司");
        tagMaps.put("amazon", "亚马逊");
        tagMaps.put("amkor", "amkor公司");
        tagMaps.put("aco", "冠捷科技");
        tagMaps.put("apple", "苹果公司");
        tagMaps.put("google", "谷歌公司");
        tagMaps.put("igg", "IGG娱乐媒体");
        tagMaps.put("hp", "惠普");
        tagMaps.put("microsoft", "微软");
        tagMaps.put("nike", "耐克NIKE");
        tagMaps.put("nicebaby", "Nicebaby母婴护理");
        tagMaps.put("nubia", "努比亚手机提供商");
        tagMaps.put("oppo", "oppo手机提供商");
        tagMaps.put("oracle", "oracle甲骨文");
        tagMaps.put("pansonic", "松下公司");
        tagMaps.put("paypal", "PayPal支付");
        tagMaps.put("pccw", "pccw电讯盈科");
        tagMaps.put("pchome", "电脑之家");
        tagMaps.put("suning", "苏宁易购");
        tagMaps.put("华润新农医药公司", "华润新龙医药有限公司");
        tagMaps.put("中国移动广东惠州公公司", "中国移动广东惠州分公司");
        tagMaps.put("娥姐茗厨品味", "娥姐名厨品味");
        tagMaps.put("沃尔的国际英语", "沃尔得国际英语");
        tagMaps.put("冠松打大众", "冠松大众");
        
    }

    public static String formatDateToGMT(Date date) {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

    public static Date parseDateInGMT(String gmt) {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return df.parse(gmt);
        } catch (ParseException e) {
            LOGGER.warn("illegal gmt string:" + gmt, e);
        }
        return null;
    }

    public static String toJson(Object obj) {
        try {
            return JACKSON_OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            return e.toString();
        }
    }

    /**
     * Split a string in the form of "host:port host2:port" into a List of
     * InetSocketAddress instances suitable for instantiating a MemcachedClient.
     * <p/>
     * Note that colon-delimited IPv6 is also supported. For example: ::1:11211
     */
    public static List<InetSocketAddress> getAddresses(String s) {
        if (s == null) {
            throw new NullPointerException("Null host list");
        }
        if (s.trim().equals("")) {
            throw new IllegalArgumentException("No hosts in list:  ``" + s
                    + "''");
        }
        ArrayList<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();

        for (String hoststuff : s.split(",")) {
            int finalColon = hoststuff.lastIndexOf(':');
            if (finalColon < 1) {
                throw new IllegalArgumentException("Invalid server ``"
                        + hoststuff + "'' in list:  " + s);

            }
            String hostPart = hoststuff.substring(0, finalColon);
            String portNum = hoststuff.substring(finalColon + 1);

            addrs
                    .add(new InetSocketAddress(hostPart, Integer
                            .parseInt(portNum)));
        }
        assert !addrs.isEmpty() : "No addrs found";
        return addrs;
    }


    public static InetSocketAddress getAddress(String address) {
        int finalColon = address.lastIndexOf(':');
        if (finalColon < 1) {
            throw new IllegalArgumentException("Invalid address:"
                    + address);

        }
        String hostPart = address.substring(0, finalColon);
        String portNum = address.substring(finalColon + 1);

        return new InetSocketAddress(hostPart, Integer
                .parseInt(portNum));
    }

    @SuppressWarnings("unchecked")
	public static void copyParams(Form form, Map<String, Object> params) {
        Parameter param;
        Object currentValue = null;
        for (final Iterator<Parameter> iter = form.iterator(); iter.hasNext(); ) {
            param = iter.next();
            currentValue = params.get(param.getName());
            if (currentValue != null) {
                List<Object> values = null;
                if (currentValue instanceof List) {
                    // Multiple values already found for this entry
                    values = ((List<Object>) currentValue);
                } else {
                    // Second value found for this entry
                    // Create a list of values
                    values = new ArrayList<Object>();
                    values.add(currentValue);
                    params.put(param.getName(), values);
                }
                if (param.getValue() == null) {
                    values.add(Series.EMPTY_VALUE);
                } else {
                    values.add(param.getValue());
                }
            } else {
                if (param.getValue() != null) {
                    params.put(param.getName(), param.getValue());
                }
            }
        }
    }

//    public static String toJson(Object obj, boolean pretty) {
//        try {
//            if (pretty) {
//                return JACKSON_OBJECT_MAPPER.defaultPrettyPrintingWriter().writeValueAsString(obj);
//            } else {
//                return JACKSON_OBJECT_MAPPER.writeValueAsString(obj);
//            }
//
//        } catch (IOException e) {
//            return e.toString();
//        }
//    }

//    public static Representation buildEncodedRepresentation(Request request, String json) {
//        return new StringRepresentation(json, MediaType.APPLICATION_JSON);
//    }

    /**
     * @param str
     * @return
     * @throws IOException
     */
    public static byte[] zip(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return out.toByteArray();
    }

    public static final String getFileMD5(File file) throws IOException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return DigestUtils.md5Hex(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * @param bytes
     * @return
     * @throws IOException
     */
    public static String unzip(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
        return out.toString("UTF-8");
    }


    public static CompositeConfiguration getConfiguration(String confDir,
                                                          String prop) {
        CompositeConfiguration config = new CompositeConfiguration();
        File file = new File(confDir + "/" + prop);
        FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
        reloadingStrategy.setRefreshDelay(10000);// 10s
        URL url = null;
        try {
            if (file.exists()) {
                url = file.toURI().toURL();
            } else {
                url = org.apache.commons.configuration.ConfigurationUtils
                        .locate(prop);

            }
            LOGGER.info("loading conf from:" + url);
            PropertiesConfiguration fileConfiguraton = new PropertiesConfiguration(
            );
            
            fileConfiguraton.setEncoding("utf-8");
            fileConfiguraton.setDelimiterParsingDisabled(true);
            fileConfiguraton.load(url);
            fileConfiguraton.setReloadingStrategy(reloadingStrategy);
            config.addConfiguration(fileConfiguraton);
            
        } catch (Exception e) {
            LOGGER.error("Failed to load config:" + prop, e);
        }
        return config;
    }

    /**
     * Log配置文件名必须为logback.xml
     *
     * @param confDir
     */
    public static void loadLogbackConfiguration(String confDir) {
        try {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            File file = new File(confDir + "/logback.xml");
            if (file.exists()) {
                configurator.doConfigure(file);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("loading logback conf from:{}", file.getAbsolutePath());
                }
            } else {
                URL url = org.apache.commons.configuration.ConfigurationUtils.locate("logback.xml");
                configurator.doConfigure(url);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("loading logback conf from:{}", url);
                }
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (JoranException e) {
            LOGGER.error("failed to load logback config from:" + confDir, e);
        }

    }
    
    public static Map<String, String> isConSpecCharacters(String string) {
    	Map<String, String> ret = new HashMap<String, String>();
        string = string.replaceAll(" |\t|\n|\r|：|0", "");
     	if ( containPattern.matcher(string).find() ){
     		ret.put("tag", string);
     		return ret;
     	}
     	
     	int len = string.getBytes().length;
     	if (  len > 63 || len <=2 ){	
     		return ret;
     	}
     	
     	if (  tagMaps.containsKey(string.toLowerCase()) ){
 			string = tagMaps.get(string.toLowerCase());
 			ret.put("tag", string);
 			return ret;
 		}
     	
     	String english = string.replaceAll("[a-z]*[A-Z]*", "");
     	if( english.length() == 0 ){
     		return ret;
     	}
     	
 		String left = string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*[()-（）.—]*", "");
 		if( left.length() ==0 ){
 				Matcher m = blackCharactersPattern.matcher(string);
 				if (! m.find()){
 					ret.put("tag", string);
 				}
 				return ret;
 		}
 		
 		return ret;
	}

    public static boolean validMobile( String phone ){
        return mobilePattern.matcher(phone).matches();
    }

    public static String listToString(List<String> stringList){
        if (stringList==null) {
            return null;
        }
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            }else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }
}
