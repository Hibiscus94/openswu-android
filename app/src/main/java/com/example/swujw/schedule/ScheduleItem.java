package com.example.swujw.schedule;

/**
 * Created by 张孟尧 on 2016/3/4.
 */
public class ScheduleItem
{
    /*教室*/
    private String cdmc;
    /*上课节带节*/
    private String jc;

    /*课程名称*/
    private String kcmc;
    /*任课老师姓名*/
    private String xm;
    /*星期*/
    private String xqjmc;
    /*上课地区*/
    private String xqmc;
    /*上课周*/
    private String zcd;

    public String getKcmc()
    {
        return kcmc;
    }

    public String getCdmc()
    {
        return cdmc;
    }

    public String getJc()
    {
        return jc;
    }


    public String getXm()
    {
        return xm;
    }

    public String getXqjmc()
    {
        return xqjmc;
    }

    public String getXqmc()
    {
        return xqmc;
    }

    public String getZcd()
    {
        return zcd;
    }

    public void setCdmc(String cdmc)
    {
        this.cdmc = cdmc;
    }

    public void setJc(String jc)
    {
        this.jc = jc;
    }


    public void setKcmc(String kcmc)
    {
        this.kcmc = kcmc;
    }

    public void setXm(String xm)
    {
        this.xm = xm;
    }

    public void setXqjmc(String xqjmc)
    {
        this.xqjmc = xqjmc;
    }

    public void setXqmc(String xqmc)
    {
        this.xqmc = xqmc;
    }

    public void setZcd(String zcd)
    {
        this.zcd = zcd;
    }
}
