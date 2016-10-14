package com.muli.utils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zhouxc
 * Date: 7/5/12
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
    public static final String TOKEN_DANGEROUS_PHONE = "6f40aed7002fca0d323db419a2c42088";
    /**
     * 响铃一声数据，程序自动上报TOKEN；
     */
    public static final String TOKEN_RINGONCE_PHONE = "e5dbac14c0d863d4561b8600c2a99677";
    /**
     * 公共标签，响铃一声；
     */
    public static final int REPORT_TYPE_RINGONCE = 0;
    public static final String REPORT_TYPE_RINGONCE_LABEL = "响铃一声";
    /**
     * 公共标签：其他
     */
    public static final int REPORT_TYPE_OTHER = 9;
    /**
     * 公共标签：诈骗
     */
    public static final int REPORT_TYPE_FRAUD= 1;
    /**
     * 标签：私有标签
     */
    public static final int REPORT_TYPE_PRIVATE = -1;
    /**
     * 公共标签：骚扰电话
     */
    public static final int REPORT_TYPE_SPAM_DIAL = 10;
    /**
     * 占位符号
     */
    public static final String CACHED_PLACEHOLDER = "$$";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String TOKEN = "token";
    public static final String DATA = "data";
    public static final String COUNT = "count";
    public static final String TAG = "tag";
    public static final String TAG_ID = "tagId";
    public static final String LOG_URL = "logourl";
    public static final String TAG_CRED = "tagCred";
    public static final String TAG_SOURCE = "tagSource";
    public static final String NET_PHONE = "netphone";
    public static final String CHINA_MOBILE_FAMILY_TEL = "chinamobilefamilytel";
    public static final String CHINA_MOBILE_GROUP_TEL = "chinamobilegrouptel";
    public static final String PHONE = "phone";
    public static final String SCORE = "score";
    public static final String THRESHOLD = "threshold";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_LOGID = "logid";
    public static final String TAG_CLUE = "clue";
    public static final String TAG_NAME = "name";
    public static final String DETAIL_TAG_NAME = "detail_name";
    public static final String CITY = "city";
    public static final String SOURCE = "source";
    

    public static final String LEVEL_CURRENT_LEVEL_SUM = "currlevelsum";
    public static final String LEVEL_NEXT_LEVEL_SUM = "nextlevelsum";
    public static final String FIELD_UNDO = "undo";

    public static final String FIELD_PHONE_SUM = "phone_sum";
    public static final String FIELD_RANK = "rank";
    public static final String FIELD_SHARE = "share";
    public static final String FIELD_UPGRADE = "upgrade";

    public static final String FIELD_PHONES = "phones";
    /**
     * 垃圾短信条数；
     */
    public static final String FIELD_SPAM_MSG_SUM = "msg_sum";
    /**
     * 用户当前等级；
     */
    public static final String FIELD_LEVEL = "level";
    /**
     * 用户总数；
     */
    public static final int FIELD_USER_TOTAL = -1;

    public static final int PHONE_SHARE_MAX = 9999999;

    public static class Report {
        /**
         * 误拦截短信 – 1；
         */
        public static final int TYPE_UNSPAM_MESSAGE = 1;
        /**
         * 陌生来电 – 2
         */
        public static final int TYPE_STRANGE_PHONE = 2;
        /**
         * 漏拦短信 – 3；
         */
        public static final int TYPE_USER_REPORT_MESSAGE = 3;
        /**
         * 号码标记 – 4；
         */
        public static final int TYPE_MARK_PHONE = 4;
        /**
         * 拦截引擎拦截短信 – 5；
         */
        public static final int TYPE_ENGINE_INTERCEPT_MESSAGE = 5;
        /**
         * 举报号码 – 6
         */
        public static final int TYPE_REPORT_PHONE = 6;

        /**
         * 拦截引擎拦截来电 -- 7；
         */
        public static final int TYPE_ENGINE_INTERCEPT_PHONE = 7;

        /**
         * 响铃一声 --8；
         */
        public static final int TYPE_RINGONCE_PHONE = 8;
        /**
         * 广告应用 --9；
         */
        public static final int TYPE_AD_APP = 9;
        /**
         * 防吸费实验室分享:14
         */
        public static final int TYPE_LABS_SHARE = 14;

        /**
         * 撤销：1
         */
        public static final int UNDO = 1;
    }

    public static class OpCode {
        public static final int DELETE = 0;
        public static final int ADD = 1;
        public static final int MODIFY = 2;
    }

    public static class QueryType {
        public static final String BY = "by";
        public static final String LIST = "list";
    }

    // 号码认证申诉

    public static  final  String  AUTH_FILEDIRE = "/home/work/local/dianhua_api/upload/";

    public  static final String AUTH_CDNDIR = org.restlet.engine.util.DateUtils.format(new Date(), "yyyy-MM-dd") + "/" ;



}
