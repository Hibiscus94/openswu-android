package com.swuos.ALLFragment.swujw.grade.persenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.swuos.ALLFragment.swujw.Login;
import com.swuos.ALLFragment.swujw.TotalInfos;
import com.swuos.ALLFragment.swujw.grade.model.GradeItem;
import com.swuos.ALLFragment.swujw.grade.model.Grades;
import com.swuos.ALLFragment.swujw.grade.view.IGradeview;
import com.swuos.swuassistant.Constant;
import com.swuos.swuassistant.R;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
        sharedPreferences = mContext.getSharedPreferences("userInf0", Context.MODE_PRIVATE);
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
        iGradeview.showDialog(true);

        Observable.create(new Observable.OnSubscribe<List<GradeItem>>() {
            @Override
            public void call(Subscriber<? super List<GradeItem>> subscriber) {
                String cache = getGradesDataJsonFromCache(xnm, xqm);
                if (cache != null && !isFroceFromNet) {
                    totalInfos.setGradesDataJson(getGradesDataJsonFromCache(xnm, xqm));
                    gradeItemList = Grades.getGradesList(totalInfos);
                    subscriber.onNext(gradeItemList);
                } else {
                    Login login = new Login();
                    String response = login.doLogin(username, password);
                    if (response.contains("LoginSuccessed")) {
                        Grades grades = new Grades(login.okhttpNet);
                        grades.setGrades(totalInfos, xnm, xqm);
                        gradeItemList = grades.getGradesList(totalInfos);
                        saveGradesJson(xnm, xqm);
                        subscriber.onNext(gradeItemList);
                    } else if (response.contains("LoginFailure")) {
                        subscriber.onError(new Throwable(mContext.getResources().getString(R.string.no_user_or_password_error)));
                    } else {
                        subscriber.onError(new Throwable(response));
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Subscriber<List<GradeItem>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        iGradeview.showDialog(false);
                        iGradeview.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<GradeItem> gradeItems) {
                        iGradeview.showDialog(false);
                        iGradeview.showResult(gradeItems);
                    }
                });
    }


    @Override
    public void getGradeDetial(final String username, final String password, final String xqm, final String xnm, final int position) {
        iGradeview.showDialog(true);
        Observable.create(new Observable.OnSubscribe<GradeItem>() {
            @Override
            public void call(Subscriber<? super GradeItem> subscriber) {
                Login login = new Login();
                String response = login.doLogin(username, password);
                if (response.contains("LoginSuccessed")) {
                    Grades grades = new Grades(login.okhttpNet);
                    //                    grades.setGrades(totalInfos, xnm, xqm);
                    GradeItem gradeItem = grades.getGradeDetial(xnm, xqm, gradeItemList.get(position));
                    subscriber.onNext(gradeItem);
                } else if (response.contains("LoginFailure")) {
                    subscriber.onError(new Throwable(mContext.getResources().getString(R.string.no_user_or_password_error)));
                } else {
                    subscriber.onError(new Throwable(response));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GradeItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        iGradeview.showDialog(false);
                        iGradeview.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(GradeItem gradeItem) {
                        iGradeview.showDialog(false);
                        iGradeview.showGradeDetial(gradeItem);
                    }
                });
    }


    @Override
    public void initData() {
        xnmPosition = getLastxnmPosition();
        xqmPosition = getXqmPosition();
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

    void saveGradesJson(String xnm, String xqm) {
        editor.putString(xnm + xqm, totalInfos.getGradesDataJson());
        editor.commit();
    }

}
