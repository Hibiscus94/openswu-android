package com.swuos.ALLFragment.swujw.grade.persenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.swuos.ALLFragment.swujw.TotalInfos;
import com.swuos.ALLFragment.swujw.grade.model.GradeItem;
import com.swuos.ALLFragment.swujw.grade.model.Grades;
import com.swuos.ALLFragment.swujw.grade.model.SwuJwParse;
import com.swuos.ALLFragment.swujw.grade.model.TeacherJudgementItem;
import com.swuos.ALLFragment.swujw.grade.view.IGradeview;
import com.swuos.ALLFragment.swujw.net.api.SwuJwApi;
import com.swuos.ALLFragment.swujw.net.jsona.LoginJson;
import com.swuos.ALLFragment.swujw.net.util.RSAUtil;
import com.swuos.swuassistant.Constant;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by 张孟尧 on 2016/7/27.
 */
public class GradePresenterCompl implements IGradePersenter {
    /*用户当前选择的学期和学年*/
    private static String xnm;
    private static String xqm;
    private static int xnmPosition = Constant.XNMPOSITION;
    private static int xqmPosition = Constant.XQMPOSITION;
    private static GradeItem gradeItem;
    private Context mContext;
    private IGradeview iGradeview;
    private TotalInfos totalInfos;
    private List<GradeItem> gradeItemList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public GradePresenterCompl(Context mContext, IGradeview iGradeview) {
        this.mContext = mContext;
        this.iGradeview = iGradeview;
        this.totalInfos = TotalInfos.getInstance();
        sharedPreferences = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public String getXnm() {
        return xnm;
    }

    @Override
    public void setXnm(String xnm) {
        GradePresenterCompl.xnm = xnm;
    }

    @Override
    public String getXqm() {
        return xqm;
    }

    @Override
    public void setXqm(String xqm) {
        GradePresenterCompl.xqm = xqm;
    }

    @Override
    public int getXnmPosition() {
        return xnmPosition;
    }

    @Override
    public void setXnmPosition(int xnmPosition) {
        GradePresenterCompl.xnmPosition = xnmPosition;
    }

    @Override
    public int getXqmPosition() {
        return xqmPosition;
    }

    @Override
    public void setXqmPosition(int xqmPosition) {
        GradePresenterCompl.xqmPosition = xqmPosition;
    }


    @Override
    public void getGrades(final String username, final String password, final String xqm, final String xnm, final boolean isFroceFromNet) {

        final Map<String, String> data = new HashMap<>();

        data.put("_search", "false");
        data.put("nd", Long.toString(new Date().getTime()));
        data.put("queryModel.currentPage", "1");
        data.put("queryModel.showCount", "1000");
        data.put("queryModel.sortName", "");
        data.put("queryModel.sortOrder", "asc");
        data.put("time", "0");
        data.put("xnm", xnm);
        data.put("xqm", xqm);
        String cache = getGradesDataJsonFromCache(xnm, xqm);
        if (cache != null && !isFroceFromNet) {
            totalInfos.setGradesDataJson(getGradesDataJsonFromCache(xnm, xqm));
            gradeItemList = Grades.getGradesList(totalInfos);
            Observable.just(gradeItemList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<GradeItem>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    String error = e.getMessage();
                    if (e instanceof UnknownHostException) {
                        error = Constant.CLIENT_ERROR;
                    } else if (e instanceof SocketTimeoutException) {
                        error = Constant.CLIENT_TIMEOUT;
                    }
                    iGradeview.showError(error);
                }

                @Override
                public void onNext(List<GradeItem> gradeItems) {
                    iGradeview.showResult(gradeItems);
                }
            });
        } else {
            SwuJwApi.jwGrade().getSchedule(totalInfos.getSwuID(), data).flatMap(new Func1<String, Observable<List<GradeItem>>>() {
                @Override
                public Observable<List<GradeItem>> call(String s) {
                    if (s.contains("登录超时"))
                        return Observable.error(new Throwable("登录超时"));
                    else {
                        totalInfos.setGradesDataJson(s);
                        gradeItemList = Grades.getGradesList(totalInfos);
                        saveGradesJson(xnm, xqm);
                    }
                    return Observable.just(gradeItemList);
                }
            }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                @Override
                public Observable<?> call(Observable<? extends Throwable> observable) {
                    return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            if (throwable.getMessage().contains("登录超时")) {
                                String swuLoginjsons = String.format("{\"serviceAddress\":\"https://uaaap.swu.edu.cn/cas/ws/acpInfoManagerWS\",\"serviceType\":\"soap\",\"serviceSource\":\"td\",\"paramDataFormat\":\"xml\",\"httpMethod\":\"POST\",\"soapInterface\":\"getUserInfoByUserName\",\"params\":{\"userName\":\"%s\",\"passwd\":\"%s\",\"clientId\":\"yzsfwmh\",\"clientSecret\":\"1qazz@WSX3edc$RFV\",\"url\":\"http://i.swu.edu.cn\"},\"cDataPath\":[],\"namespace\":\"\",\"xml_json\":\"\",\"businessServiceName\":\"uaaplogin\"}", username, password);

                                return SwuJwApi.loginIswu().login(RSAUtil.encrypt(swuLoginjsons)).flatMap(new Func1<LoginJson, Observable<?>>() {
                                    @Override
                                    public Observable<?> call(LoginJson loginJson) {
                                        String tgt = loginJson.getData().getGetUserInfoByUserNameResponse().getReturnX().getInfo().getAttributes().getTgt();
                                        String cookie = String.format("CASTGC=\"%s\"; rtx_rep=no", new String(Base64.decode(tgt, Base64.DEFAULT)));
                                        return SwuJwApi.loginJw(cookie).login();
                                    }
                                });
                            } else {
                                return Observable.error(throwable);
                            }
                        }
                    });
                }

            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<GradeItem>>() {
                @Override
                public void onCompleted() {


                }

                @Override
                public void onError(Throwable e) {
                    String error = e.getMessage();

                    if (e instanceof UnknownHostException) {
                        error = Constant.CLIENT_ERROR;
                    } else if (e instanceof SocketTimeoutException) {
                        error = Constant.CLIENT_TIMEOUT;
                    }
                    iGradeview.showError(error);
                }

                @Override
                public void onNext(List<GradeItem> gradeItems) {

                    iGradeview.showResult(gradeItems);
                }
            });
        }
    }


    @Override
    public void getGradeDetial(final String username, final String password, final int position) {

        gradeItem = gradeItemList.get(position);
        Map<String, String> data = new HashMap<String, String>();
        data.put("jxb_id", gradeItem.getJxb_id());
        data.put("kcmc", gradeItem.getKcmc());
        data.put("xh_id", gradeItem.getXh_id());
        data.put("xnm", gradeItem.getXnm());
        data.put("xqm", gradeItem.getXqm());

        SwuJwApi.getJwGradeDetail().getSchedule(String.valueOf(System.currentTimeMillis()), totalInfos.getSwuID(), data).flatMap(new Func1<String, Observable<GradeItem>>() {
            @Override
            public Observable<GradeItem> call(String s) {
                if (s.contains("登录超时"))
                    return Observable.error(new Throwable("登录超时"));
                else {
                    gradeItem = Grades.getGradeDetial(s, gradeItemList.get(position));
                }
                return Observable.just(gradeItem);
            }
        }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {
                return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        if (throwable.getMessage().contains("登录超时")) {
                            String swuLoginjsons = String.format("{\"serviceAddress\":\"https://uaaap.swu.edu.cn/cas/ws/acpInfoManagerWS\",\"serviceType\":\"soap\",\"serviceSource\":\"td\",\"paramDataFormat\":\"xml\",\"httpMethod\":\"POST\",\"soapInterface\":\"getUserInfoByUserName\",\"params\":{\"userName\":\"%s\",\"passwd\":\"%s\",\"clientId\":\"yzsfwmh\",\"clientSecret\":\"1qazz@WSX3edc$RFV\",\"url\":\"http://i.swu.edu.cn\"},\"cDataPath\":[],\"namespace\":\"\",\"xml_json\":\"\",\"businessServiceName\":\"uaaplogin\"}", username, password);

                            return SwuJwApi.loginIswu().login(RSAUtil.encrypt(swuLoginjsons)).flatMap(new Func1<LoginJson, Observable<?>>() {
                                @Override
                                public Observable<?> call(LoginJson loginJson) {
                                    String tgt = loginJson.getData().getGetUserInfoByUserNameResponse().getReturnX().getInfo().getAttributes().getTgt();
                                    String cookie = String.format("CASTGC=\"%s\"; rtx_rep=no", new String(Base64.decode(tgt, Base64.DEFAULT)));
                                    return SwuJwApi.loginJw(cookie).login();
                                }
                            });
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GradeItem>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                String error = e.getMessage();

                if (e instanceof UnknownHostException) {
                    error = Constant.CLIENT_ERROR;
                } else if (e instanceof SocketTimeoutException) {
                    error = Constant.CLIENT_TIMEOUT;
                }
                iGradeview.showError(error);
            }

            @Override
            public void onNext(GradeItem gradeItem) {

                iGradeview.showGradeDetial(gradeItem);
            }
        });

    }


    @Override
    public void initData() {
        xnmPosition = getLastxnmPosition();
        xqmPosition = getLastxqmPosition();
    }

    @Override
    public String getUsername() {
        return totalInfos.getUserName();
    }

    @Override
    public String getPassword() {
        return totalInfos.getPassword();
    }

    @Override
    public void saveUserLastCLick(int xnm, int xqm) {
        editor.putInt("lastxnm", xnm);
        editor.putInt("lastxqm", xqm);
        editor.commit();
    }

    @Override
    public int getLastxnmPosition() {
        return sharedPreferences.getInt("lastxnm", Constant.XNMPOSITION);
    }

    @Override
    public int getLastxqmPosition() {
        return sharedPreferences.getInt("lastxqm", Constant.XQMPOSITION);

    }

    @Override
    public String getGradesDataJsonFromCache(String xnm, String xqm) {
        return sharedPreferences.getString(xnm + xqm, null);
    }

    @Override
    public void judgement(final String username, final String password, final String iscommit) {
        SwuJwApi.getJwJudgement().getTeacherList(totalInfos.getSwuID(), System.currentTimeMillis(), "N4010", "00").flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                if (s.contains("登录超时"))
                    return Observable.error(new Throwable("登录超时"));
                else {
                    return Observable.just(s);
                }
            }
        }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {
                return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        if (throwable.getMessage().contains("登录超时")) {
                            String swuLoginjsons = String.format("{\"serviceAddress\":\"https://uaaap.swu.edu.cn/cas/ws/acpInfoManagerWS\",\"serviceType\":\"soap\",\"serviceSource\":\"td\",\"paramDataFormat\":\"xml\",\"httpMethod\":\"POST\",\"soapInterface\":\"getUserInfoByUserName\",\"params\":{\"userName\":\"%s\",\"passwd\":\"%s\",\"clientId\":\"yzsfwmh\",\"clientSecret\":\"1qazz@WSX3edc$RFV\",\"url\":\"http://i.swu.edu.cn\"},\"cDataPath\":[],\"namespace\":\"\",\"xml_json\":\"\",\"businessServiceName\":\"uaaplogin\"}", username, password);

                            return SwuJwApi.loginIswu().login(RSAUtil.encrypt(swuLoginjsons)).flatMap(new Func1<LoginJson, Observable<?>>() {
                                @Override
                                public Observable<?> call(LoginJson loginJson) {
                                    String tgt = loginJson.getData().getGetUserInfoByUserNameResponse().getReturnX().getInfo().getAttributes().getTgt();
                                    String cookie = String.format("CASTGC=\"%s\"; rtx_rep=no", new String(Base64.decode(tgt, Base64.DEFAULT)));
                                    return SwuJwApi.loginJw(cookie).login();
                                }
                            });
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }

        }).flatMap(new Func1<String, Observable<TeacherJudgementItem>>() {
            @Override
            public Observable<TeacherJudgementItem> call(String s) {
                return Observable.from(SwuJwParse.getTeachList(s));
            }
        }).flatMap(new Func1<TeacherJudgementItem, Observable<String>>() {
            @Override
            public Observable<String> call(TeacherJudgementItem teacherJudgementItems) {
                System.out.println(teacherJudgementItems.getClassName());
                System.out.println(teacherJudgementItems.getTeacherName());
                Map<String, String> data = new HashMap<String, String>();
                data.put("kch_id", teacherJudgementItems.getKch_id());
                data.put("ztpjbl", "100");
                data.put("jszdpjbl", "0");
                data.put("xykzpjbl", "0");
                data.put("jgh_id", teacherJudgementItems.getJgh_id());
                data.put("modelList[0].pjdxdm", teacherJudgementItems.getXsdm());
                data.put("jxb_id", teacherJudgementItems.getJxb_id());
                data.put("modelList[0].pjmbmcb_id", Constant.pjmbmcb_id);
                data.put("modelList[0].xspfb_id", Constant.xspfb_id);
                data.put("modelList[0].py", "");
                //                data.put(# 这里为0的话就是保存,,为1的话就是提);
                data.put("tjzt", iscommit);
                data.put("modelList[0].xspjList[0].pjzbxm_id", Constant.F);
                data.put("modelList[0].xspjList[0].childXspjList[0].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[0].childXspjList[0].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[0].childXspjList[0].pjzbxm_id", Constant.F1);
                data.put("modelList[0].xspjList[0].childXspjList[1].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[0].childXspjList[1].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[0].childXspjList[1].pjzbxm_id", Constant.F2);
                data.put("modelList[0].xspjList[0].childXspjList[2].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[0].childXspjList[2].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[0].childXspjList[2].pjzbxm_id", Constant.F3);
                data.put("modelList[0].xspjList[0].childXspjList[3].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[0].childXspjList[3].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[0].childXspjList[3].pjzbxm_id", Constant.F3);

                data.put("modelList[0].xspjList[1].pjzbxm_id", Constant.G);
                data.put("modelList[0].xspjList[1].childXspjList[0].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[1].childXspjList[0].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[1].childXspjList[0].pjzbxm_id", Constant.G1);
                data.put("modelList[0].xspjList[1].childXspjList[1].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[1].childXspjList[1].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[1].childXspjList[1].pjzbxm_id", Constant.G2);
                data.put("modelList[0].xspjList[1].childXspjList[2].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[1].childXspjList[2].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[1].childXspjList[2].pjzbxm_id", Constant.G3);
                data.put("modelList[0].xspjList[1].childXspjList[3].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[1].childXspjList[3].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[1].childXspjList[3].pjzbxm_id", Constant.G4);
                data.put("modelList[0].xspjList[1].childXspjList[4].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[1].childXspjList[4].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[1].childXspjList[4].pjzbxm_id", Constant.G5);
                data.put("modelList[0].xspjList[1].childXspjList[5].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[1].childXspjList[5].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[1].childXspjList[5].pjzbxm_id", Constant.G6);
                data.put("modelList[0].xspjList[2].pjzbxm_id", Constant.H);
                data.put("modelList[0].xspjList[2].childXspjList[0].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[2].childXspjList[0].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[2].childXspjList[0].pjzbxm_id", Constant.H1);
                data.put("modelList[0].xspjList[2].childXspjList[1].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[2].childXspjList[1].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[2].childXspjList[1].pjzbxm_id", Constant.H2);

                data.put("modelList[0].xspjList[3].pjzbxm_id", Constant.I);
                data.put("modelList[0].xspjList[3].childXspjList[0].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[3].childXspjList[0].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[3].childXspjList[0].pjzbxm_id", Constant.I1);
                data.put("modelList[0].xspjList[3].childXspjList[1].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[3].childXspjList[1].pfdjdmxmb_id", Constant.A);
                data.put("modelList[0].xspjList[3].childXspjList[1].pjzbxm_id", Constant.I2);
                data.put("modelList[0].xspjList[3].childXspjList[2].pfdjdmb_id", Constant.pfdjdmb_id);
                data.put("modelList[0].xspjList[3].childXspjList[2].pfdjdmxmb_id", Constant.B);
                data.put("modelList[0].xspjList[3].childXspjList[2].pjzbxm_id", Constant.I);


                return SwuJwApi.getJwJudgement().setJudgement(totalInfos.getSwuID(), data);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                iGradeview.showError("评教成功,请进入教务系统查看");

            }

            @Override
            public void onError(Throwable throwable) {
                iGradeview.showError(throwable.getMessage());
            }

            @Override
            public void onNext(String s) {
            }
        });

    }

    @Override
    public void filterGrades(final boolean isCheckedNormalExam, final boolean isCheckedMakeupExam, final boolean isCheckedProfessionalRequiredCourse, final boolean isCheckedProfessionalElectiveCourse, final boolean isCheckedGeneralRequiredCourse, final boolean isCheckedGeneralElectiveCourse, final boolean isCheckedSubjectRequiredCourse, final float gradeMin, final float gradeMax, final float gradePointMin, final float gradePointMax) {


        Observable.just(gradeItemList)
                .flatMap(new Func1<List<GradeItem>, Observable<List<GradeItem>>>() {


                             @Override
                             public Observable<List<GradeItem>> call(List<GradeItem> gradeItems) {
                                 final List<GradeItem> filterGradeItems = new ArrayList<>();
                                 GradeItem headGradeItem = new GradeItem();
                                 headGradeItem.setKcmc("科目名称");
                                 headGradeItem.setCj("成绩");
                                 headGradeItem.setXf("学分");
                                 headGradeItem.setJd("绩点");
                                 headGradeItem.setKsxzText("考试性质");
                                 headGradeItem.setKcxzText("课程性质");
                                 filterGradeItems.add(headGradeItem);
                                 //成绩总和
                                 float gradesCount = 0;
                                 //学分总和
                                 float studyPointCount = 0;
                                 //绩点总和
                                 float gradesPointCount = 0;
                                 String ksxzTag = "";
                                 if (isCheckedNormalExam) { ksxzTag += "正常考试";}
                                 if (isCheckedMakeupExam) {ksxzTag += "补考一";}
                                 if (isCheckedMakeupExam == isCheckedNormalExam) {
                                     ksxzTag = "正常考试补考一";
                                 }

                                 String kcxzTag = "";

                                 if (isCheckedProfessionalRequiredCourse)
                                     kcxzTag += "专业必修课";
                                 if (isCheckedProfessionalElectiveCourse)
                                     kcxzTag += "专业选修课";
                                 if (isCheckedGeneralRequiredCourse)
                                     kcxzTag += "通识必修课";
                                 if (isCheckedGeneralElectiveCourse)
                                     kcxzTag += "通识选修课";
                                 if (isCheckedSubjectRequiredCourse)
                                     kcxzTag += "学科必修课";
                                 if (!isCheckedProfessionalRequiredCourse &&
                                         !isCheckedProfessionalElectiveCourse &&
                                         !isCheckedGeneralRequiredCourse &&
                                         !isCheckedGeneralElectiveCourse &&
                                         !isCheckedSubjectRequiredCourse) {
                                     kcxzTag = "专业必修课专业选修课通识必修课通识选修课学科必修课";
                                 }
                                 for (int i = 1; i < gradeItemList.size() - 2; i++) {
                                     GradeItem gradeItem = gradeItemList.get(i);
                                     String cj = gradeItem.getCj();
                                     float grades = 0;
                                     float gradesPoint = Float.parseFloat(gradeItem.getJd());
                                     float studyPoint = Float.parseFloat(gradeItem.getXf());

                                     switch (cj) {
                                         case "A":
                                             grades = 95;
                                             break;
                                         case "B":
                                             grades = 85;
                                             break;
                                         case "C":
                                             grades = 75;
                                             break;
                                         case "D":
                                             grades = 65;
                                             break;
                                         case "E":
                                             grades = 55;
                                             break;
                                         default:
                                             grades = Float.valueOf(cj);
                                             break;
                                     }

                                     if (ksxzTag.contains(gradeItem.getKsxzText()))
                                         if (kcxzTag.contains(gradeItem.getKcxzText()))
                                             if (grades >= gradeMin && grades <= gradeMax)
                                                 if (gradesPoint >= gradePointMin && gradesPoint <= gradePointMax) {
                                                     gradesPointCount += gradesPoint;
                                                     gradesCount += grades;
                                     /*绩点不等于0时加学分*/
                                                     if (gradesPoint != 0) {
                                                         studyPointCount += studyPoint;
                                                     }
                                                     filterGradeItems.add(gradeItem);
                                                 }
                                 }
                                 if (filterGradeItems.size() != 0) {
                                 /*设置列表的尾部，显示平均成绩和总成绩*/
                                     GradeItem gradeItemFooter1 = new GradeItem();
                                     gradeItemFooter1.setKcmc("平均");
                                     gradeItemFooter1.setCj(String.format("%.2f", gradesCount / (filterGradeItems.size() - 1)));
                                     gradeItemFooter1.setXf(String.format("%.2f", studyPointCount / (filterGradeItems.size() - 1)));
                                     gradeItemFooter1.setJd(String.format("%.2f", gradesPointCount / (filterGradeItems.size() - 1)));
                                     filterGradeItems.add(gradeItemFooter1);

                                     GradeItem gradeItemFooter2 = new GradeItem();
                                     gradeItemFooter2.setKcmc("总和");
                                     gradeItemFooter2.setCj(String.format("%.2f", gradesCount));
                                     gradeItemFooter2.setXf(String.format("%.2f", studyPointCount));
                                     gradeItemFooter2.setJd(String.format("%.2f", gradesPointCount));
                                     filterGradeItems.add(gradeItemFooter2);
                                 }
                                 return Observable.just(filterGradeItems);
                             }
                         }

                ).subscribe(new Subscriber<List<GradeItem>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    iGradeview.showError(e.getMessage());
                                }

                                @Override
                                public void onNext(List<GradeItem> gradeItems) {
                                    iGradeview.showResult(gradeItems);
                                }
                            }

        );

    }

    void saveGradesJson(String xnm, String xqm) {
        editor.putString(xnm + xqm, totalInfos.getGradesDataJson());
        editor.commit();
    }

}
