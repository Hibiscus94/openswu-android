package com.example.swujw.grade;

import com.example.swujw.TotalInfo;
import com.example.swujw.Login;
import com.example.swuassistant.Constant;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张孟尧 on 2016/1/23.
 */
public class Grades extends Login
{

    public  String setGrades(TotalInfo totalInfo)
    {
        /*构建一个post的参数*/
        List<NameValuePair> postNameValuePairs = new ArrayList<>();
        postNameValuePairs.add(new BasicNameValuePair("_search", "false"));
        postNameValuePairs.add(new BasicNameValuePair("nd", "1451922678091"));
        postNameValuePairs.add(new BasicNameValuePair("queryModel.currentPage", "1"));
        postNameValuePairs.add(new BasicNameValuePair("queryModel.showCount", "30"));
        postNameValuePairs.add(new BasicNameValuePair("queryModel.sortName", ""));
        postNameValuePairs.add(new BasicNameValuePair("queryModel.sortOrder", "asc"));
        postNameValuePairs.add(new BasicNameValuePair("time", "0"));
        postNameValuePairs.add(new BasicNameValuePair("xnm", "2015"));
        postNameValuePairs.add(new BasicNameValuePair("xqm", "3"));
        /*构建目标网址*/
        String url = "http://jw.swu.edu.cn/jwglxt/cjcx/cjcx_cxDgXscj.html?" + "doType=query&gnmkdmKey=N305005&sessionUserKey=" + totalInfo.getSwuID();
        /*发送请求*/
        String respones = Login.client.doPost(url, postNameValuePairs);
        if (!respones.contains(Constant.NO_NET))
        {
            /*因为获得数据前面有一个"null"所以对获得的内容进行整理*/
            respones = respones.substring(4);
            /*构建gson数据来解析json数据*/
            Gson gson = new Gson();
            totalInfo.setGrades(gson.fromJson(respones, GradesData.class));
        } else return respones;
        return Constant.CLIENT_OK;
    }

    public List<GradeItem> getGradesList(TotalInfo totalInfo)
    {

        /*储存成绩的列表*/
        List<GradeItem> gradeItemList = new ArrayList<>();
        /*成绩总和*/
        double cjCount = 0.0;
        /*学分总和*/
        double xfCount = 0.0;
        /*绩点总和*/
        double jdCount = 0.0;
        /*已获得的成绩信息进行整理*/
        GradesData gradesData = totalInfo.getGrades();
        /*设置列表的头部*/
        GradeItem gradeItemHeader = new GradeItem();
        gradeItemHeader.setKcmc("科目   ");
        gradeItemHeader.setCj("成绩");
        gradeItemHeader.setXf("学分");
        gradeItemHeader.setJd("绩点");
        gradeItemList.add(gradeItemHeader);
        GradesData.Items items;
        for (int i = 0; i < gradesData.getItems().size(); i++)
        {
            /*获得单个的科目成绩*/
            items = gradesData.getItems().get(i);
            String kcmc = items.getKcmc();
            String cj = items.getCj();
            String xf = items.getXf();
            String jd = items.getJd();
            /*用来处理成绩是按照ＡＢＣＤ评定的情况*/
            switch (cj)
            {
                case "A":
                    cjCount += 95;
                    break;
                case "B":
                    cjCount += 85;
                    break;
                case "C":
                    cjCount += 75;
                    break;
                case "D":
                    cjCount += 65;
                    break;
                case "E":
                    cjCount += 55;
                    break;
                default:
                    cjCount += Double.valueOf(cj);
                    break;
            }
            /*对成绩进行总和相加*/
            xfCount += Double.valueOf(xf);
            jdCount += Double.valueOf(jd);
            GradeItem gradeItem = new GradeItem();
            gradeItem.setKcmc(kcmc);
            gradeItem.setCj(cj);
            gradeItem.setXf(xf);
            gradeItem.setJd(jd);
            /*添加到列表中*/
            gradeItemList.add(gradeItem);
        }
        /*设置列表的尾部，显示平均成绩和总成绩*/
        GradeItem gradeItemFooter1 = new GradeItem();
        gradeItemFooter1.setKcmc("平均成绩");
        gradeItemFooter1.setCj(String.valueOf(cjCount / gradesData.getItems().size()));
        gradeItemFooter1.setXf(String.valueOf(xfCount / gradesData.getItems().size()));
        gradeItemFooter1.setJd(String.valueOf(jdCount / gradesData.getItems().size()));
        gradeItemList.add(gradeItemFooter1);

        GradeItem gradeItemFooter2 = new GradeItem();
        gradeItemFooter2.setKcmc("总成绩");
        gradeItemFooter2.setCj(String.valueOf(cjCount));
        gradeItemFooter2.setXf(String.valueOf(xfCount));
        gradeItemFooter2.setJd(String.valueOf(jdCount));
        gradeItemList.add(gradeItemFooter2);
        return gradeItemList;

    }
}
