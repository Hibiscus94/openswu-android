package com.example.swuassistant;

/**
 * Created by 张孟尧 on 2016/1/10.
 */
public class Constant
{
    public static final int TIMEOUT = 4000;
    //    设置Message标记
    public static final int SHOW_RESPONSE = 0;
    public static final int ERROR = 1;
    public static final int DELETE = 2;

    public static final int LOGIN_SUCCESE = 3;
    public static final int LOGIN_FAILED = 4;
    public static final int LOGIN_TIMEOUT = 10;
    public static final int LOGIN_CLIENT_ERROR = 11;
    public static final int LOGIN_NO_NET = 12;


    public static final int GRADES_OK = 5;
    public static final int UPDATA = 6;
    public static final int MAIN = 7;
    public static final int SHOW = 8;
    public static final int DISSHOW = 9;
    public static final int SCHEDULE_OK = 13;
    public static final int SCHEDULE__LOADING = 14;
    public static final int SHOW_NOTIFYCATION = 15;
    public static final long DEFAULHEADWAY = 1000*60;
    public static final long DEFAULTIME = 1000*60;//60秒
    public static final long ONE_CLASS_TIME = 1000*60*40;



    public static final String NO_NET = "网络出现了问题";
    public static final String CLIENT_OK = "成功";
    public static final String CLIENT_ERROR = "连接出错";
    public static final String CLIENT_TIMEOUT = "连接超时";

    public static final String XQM_ONE = "3";
    public static final String XQM_TWO = "12";
    public static final String XQM_THREE = "16";
    public static final String XQM_ALL = "";
    public static final String[] ALL_XQM = {XQM_ALL, XQM_ONE, XQM_TWO, XQM_THREE};
    public static final String[] ALL_XNM = {"", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010"};
    public static final String NET_TIMEOUT = "NET_TIMEOUT";
    public static final String[] FRAGMENTTAG = {"mainPageFragment", "scheduleFragment", "gradesFragment", "studyMaterialsFragment", "findLostFragment", "chargeFragment", "libraryFragrment"};
    //    校内门户地址
    public static final String urlUrp = "http://urp6.swu.edu.cn/login.portal";
    //    用户信息发送目标地址
    public static final String urlLogin = "http://urp6.swu.edu.cn/userPasswordValidate.portal";
    //    登陆后跳转网页
    public static final String urlPortal = "http://urp6.swu.edu.cn/index.portal";
    //    #教务系统网站 Ems意为swu Educational management system
    public static final String urlEms = "http://jw.swu.edu.cn/jwglxt/idstar/index.jsp";
    public static final String urlJW = "http://jw.swu.edu.cn/jwglxt/xtgl/index_initMenu.html";
    /*登陆校内门户是post的两个重要参数*/
    public static final String gotos = "http://urp6.swu.edu.cn/loginSuccess.portal";
    public static final String gotoOnFail = "http://urp6.swu.edu.cn/loginFailure.portal";

    //登陆图书馆的地址
    public static final String loginLibrary = "http://www.lib.swu.edu.cn/webs/user_loginLib.action";
    //我的图书馆主页
    public static final String libraryIndexUri = "http://mylib.swu.edu.cn/space.jsp?do=home";
    //我的书架
    public static final String libraryBorrowUri = "http://mylib.swu.edu.cn/borry.jsp";
    //我的借阅历史
    public static final String libraryBorrowHistoryUri = "http://mylib.swu.edu.cn/borry.jsp?ac=userHistory";
    //我的借阅信息
    public static final String libraryBorrowInfo = "http://mylib.swu.edu.cn/borry.jsp?ac=borryInofor";
    public static final int[] background = {R.color.colorclass1, R.color.colorclass2, R.color.colorclass3, R.color.colorclass4, R.color.colorclass5, R.color.colorclass6};
    public static final String[] SCHEDULE_WEEK_TITLE = {"整学期", "第一周", "第二周", "第三周", "第四周", "第五周", "第六周", "第七周", "第八周", "第九周", "第十周", "第十一周", "第十二周", "第十三周", "第十四周", "第十五周", "第十六周", "第十七周", "第十八周", "第十九周", "第二十周"};
    public static final String[] STARTIME ={"8:00","8:50","9:40","10:40","11:30","12:30","13:50","14:30","15:20","16:10","17:00","19:00","19:50","20:40"};
public static final int[] STARTtIMEHOUR={8,8,9,10,11,12,13,14,15,16,17,19,19,20};
    public static final int[] STARTtIMES={8*60,8*60+50,9*60+40,10*60+40,11*60+30,12*60+30,13*60+20,14*60+30,15*60+20,16*60+10,17*60,19*60,19*60+50,20*60+40};

    public static final int [] STARTtIMEMIN={0,50,40,40,30,30,50,30,20,10,0,0,50,40};
}

