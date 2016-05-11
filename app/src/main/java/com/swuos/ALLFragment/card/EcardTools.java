package com.swuos.ALLFragment.card;

import android.util.Log;

import com.swuos.net.OkhttpNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by codekk on 2016/5/3.
 * Email:  645326280@qq.com
 */
public class EcardTools implements Serializable {
    private OkhttpNet okhttpNet;

    public EcardTools() {
        okhttpNet = new OkhttpNet();
        Log.d("kklog", "!!!!!Inits()!!!!!!");
    }

    public List<EcardInfo> GetEcardInfos(String id, String pd) {
        List<EcardInfo> ecardInfos;
        InputStream in = okhttpNet.doGetInputStream("http://ecard.swu.edu.cn/search/oracle/queryresult.asp?cardno=" + id + "&password=" + pd);
        String s = "none";
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = null;
            if (in == null) {
                return null;
            } else {
                reader = new BufferedReader(new InputStreamReader(in, "gb2312"));
            }
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            s = builder.toString();
        } catch (UnsupportedEncodingException e) {
            Log.d("kklog", "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ecardInfos = ParserTools.parserHtmlToEcardInfos(s, "td");
        return ecardInfos;
    }

    public List<ConsumeInfo> GetConsumeInfos(String index) {
        List<ConsumeInfo> consumeInfos;
        InputStream in = okhttpNet.doGetInputStream(" http://ecard.swu.edu.cn/search/oracle/finance.asp?offset="+index);
        String s = "none";
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = null;
            if (in == null) {
                return null;
            } else {
                reader = new BufferedReader(new InputStreamReader(in, "gb2312"));
            }
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            s = builder.toString();
        } catch (UnsupportedEncodingException e) {
            Log.d("kklog", "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        consumeInfos = ParserTools.parserHtmlToConsumeInfos(s, "TD");
        return consumeInfos;
    }

    public String GetLastIndex() throws StringIndexOutOfBoundsException{
        InputStream in = okhttpNet.doGetInputStream("http://ecard.swu.edu.cn/search/oracle/finance.asp");
        String s = "none";
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = null;
            if (in == null) {
                return null;
            } else {
                reader = new BufferedReader(new InputStreamReader(in, "gb2312"));
            }
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            s = builder.toString();
        } catch (UnsupportedEncodingException e) {
            Log.d("kklog", "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int start=s.indexOf("共有<font color=red>");
        int end=s.indexOf("</font>页&");
        String ss=s.substring(start+18,end);
        return ss;
    }
}
