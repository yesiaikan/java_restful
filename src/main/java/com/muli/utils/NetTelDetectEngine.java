package com.muli.utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * description:
 * author: shijunbo
 * date: 19/06/14
 */
@Singleton
public class NetTelDetectEngine {
	private Configuration configuration;
	private List<Pattern>  netTelPatterns;

    public static final Map<String, String> overseas = new HashMap<String, String>(){
        {
            put("0060", "马来西亚");
            put("0062", "印度尼西亚");
            put("0063", "菲律宾");
            put("0065", "新加坡");
            put("0066", "泰国");
            put("00673", "文莱");
            put("0081", "日本");
            put("0082", "韩国");
            put("0084", "越南");
            put("00850", "朝鲜");
            put("00852", "香港");
            put("00853", "澳门");
            put("00855", "柬埔寨");
            put("00856", "老挝");
            put("00886", "台湾");
            put("00880", "孟加拉国");
            put("0090", "土耳其");
            put("0091", "印度");
            put("0092", "巴基斯坦");
            put("0093", "阿富汗");
            put("0094", "斯里兰卡");
            put("0095", "缅甸");
            put("00960", "马尔代夫");
            put("00961", "黎巴嫩");
            put("00962", "约旦");
            put("00963", "叙利亚");
            put("00964", "伊拉克");
            put("00965", "科威特");
            put("00966", "沙特阿拉伯");
            put("00968", "阿曼");
            put("00972", "以色列");
            put("00973", "巴林");
            put("00974", "卡塔尔");
            put("00975", "不丹");
            put("00976", "蒙古");
            put("00977", "尼泊尔");
            put("0098", "伊朗");
            put("0020", "埃及");
            put("00210", "摩洛哥");
            put("00213", "阿尔及利亚");
            put("00216", "突尼斯");
            put("00218", "利比亚");
            put("00220", "冈比亚");
            put("00221", "塞内加尔");
            put("00222", "毛里塔尼亚");
            put("00223", "马里");
            put("00224", "几内亚");
            put("00225", "科特迪瓦");
            put("00226", "布基拉法索");
            put("00227", "尼日尔");
            put("00228", "多哥");
            put("00229", "贝宁");
            put("00230", "毛里求斯");
            put("00231", "利比里亚");
            put("00232", "塞拉利昂");
            put("00233", "加纳");
            put("00234", "尼日利亚");
            put("00235", "乍得");
            put("00236", "中非");
            put("00237", "喀麦隆");
            put("00238", "佛得角");
            put("00239", "圣多美|普林西比");
            put("00240", "赤道几内亚");
            put("00241", "加蓬");
            put("00242", "刚果");
            put("00243", "扎伊尔");
            put("00244", "安哥拉");
            put("00245", "几内亚比绍");
            put("00247", "阿森松");
            put("00248", "塞舌尔");
            put("00249", "苏丹");
            put("00250", "卢旺达");
            put("00251", "埃塞俄比亚");
            put("00252", "索马里");
            put("00253", "吉布提");
            put("00254", "肯尼亚");
            put("00255", "坦桑尼亚");
            put("00256", "乌干达");
            put("00257", "布隆迪");
            put("00258", "莫桑比克");
            put("00260", "赞比亚");
            put("00261", "马达加斯加");
            put("00262", "留尼旺岛");
            put("00263", "津巴布韦");
            put("00264", "纳米比亚");
            put("00265", "马拉维");
            put("00266", "莱索托");
            put("00267", "博茨瓦纳");
            put("00268", "斯威士兰");
            put("00269", "科摩罗");
            put("0027", "南非");
            put("00290", "圣赫勒拿");
            put("00297", "阿鲁巴岛");
            put("00298", "法罗群岛");
            put("007", "俄罗斯");
            put("0030", "希腊");
            put("0031", "荷兰");
            put("0032", "比利时");
            put("0033", "法国");
            put("0034", "西班牙");
            put("00350", "直布罗陀");
            put("00351", "葡萄牙");
            put("00352", "卢森堡");
            put("00353", "爱尔兰");
            put("00354", "冰岛");
            put("00355", "阿尔巴尼亚");
            put("00356", "马耳他");
            put("00357", "塞浦路斯");
            put("00358", "芬兰");
            put("00359", "保加利亚");
            put("00336", "匈牙利");
            put("00349", "德国");
            put("00338", "南斯拉夫");
            put("0039", "意大利");
            put("00223", "圣马力诺");
            put("00396", "梵蒂冈");
            put("0040", "罗马尼亚");
            put("0041", "瑞士");
            put("004175", "列支敦士登");
            put("0043", "奥地利");
            put("0044", "英国");
            put("0045", "丹麦");
            put("0046", "瑞典");
            put("0047", "挪威");
            put("0048", "波兰");
            put("001", "美国|加拿大");
            put("001808", "中途岛|夏威夷|威克岛");
            put("001809", "安圭拉岛|维尔京群岛|圣卢西亚|波多黎各|牙买加|巴哈马|巴巴多斯");
            put("001907", "阿拉斯加");
            put("00299", "格陵兰岛");
            put("00500", "福克兰群岛");
            put("00501", "伯利兹");
            put("00502", "危地马拉");
            put("00503", "萨尔瓦多");
            put("00504", "洪都拉斯");
            put("00505", "尼加拉瓜");
            put("00506", "哥斯达黎加");
            put("00507", "巴拿马");
            put("00509", "海地");
            put("0051", "秘鲁");
            put("0052", "墨西哥");
            put("0053", "古巴");
            put("0054", "阿根廷");
            put("0055", "巴西");
            put("0056", "智利");
            put("0057", "哥伦比亚");
            put("0058", "委内瑞拉");
            put("00591", "玻利维亚");
            put("00592", "圭亚那");
            put("00593", "厄瓜多尔");
            put("00594", "法属圭亚那");
            put("00595", "巴拉圭");
            put("00596", "马提尼克");
            put("00597", "苏里南");
            put("00598", "乌拉圭");
            put("0061", "澳大利亚");
            put("0064", "新西兰");
            put("00671", "关岛");
            put("006722", "科科斯岛");
            put("006723", "诺福克岛");
            put("006724", "圣诞岛");
            put("00674", "瑙鲁");
            put("00676", "汤加");
            put("00677", "所罗门群岛");
            put("00678", "瓦努阿图");
            put("00679", "斐济");
            put("00682", "科克群岛");
            put("00683", "纽埃岛");
            put("00684", "东萨摩亚");
            put("00685", "西萨摩亚");
            put("00686", "基里巴斯");
            put("00688", "图瓦卢");
        }
    };

    public static boolean isOverseasTel(String phone){
        int len = phone.length();
        for(int index = 1; index <= len && index < 6; index ++){
            if ( overseas.containsKey(phone.substring(0, index)) ){
                return true;
            }
        }
        return false;
    }
	
	@Inject
	public NetTelDetectEngine(Configuration configuration){
		this.configuration = configuration;
		this.netTelPatterns = new ArrayList<Pattern>();
		getPattern();
	}
	 
	private void getPattern(){ 
		String regex = StringUtils.stripToNull(configuration.getString("net.phone.regex"));
		if( regex != null ){
			String  [] regexs  = regex.split(";");	
			for(String r: regexs){
				netTelPatterns.add(Pattern.compile(r)) ;
			}
		}
	}
	
	 /**
     * 检测网络电话；
     *
     * @param text
     * @return
     */
	public boolean isNetTel(String phone){
		
		for( Pattern p : netTelPatterns ){
			if( p.matcher(phone).find() ){
				return true;
			}
		}
		return false;
	}
	
	
	
	
}
