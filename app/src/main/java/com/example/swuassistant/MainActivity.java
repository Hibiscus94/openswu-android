package com.example.swuassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charge.ChargeFragment;
import com.example.find_lost.FindLostFragment;
import com.example.library.GetMyLibraryInfo;
import com.example.library.LibraryFragment;
import com.example.main.MainPageFragment;
import com.example.study_materials.StudyMaterialsFragment;
import com.example.swujw.TotalInfo;
import com.example.swujw.grade.GradesFragment;
import com.example.swujw.schedule.ScheduleTableFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

//import com.example.swujw.schedule.ScheduleFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private List<NameValuePair> nameValuePairsLoginLibrary;
    /*账户名*/
    private static String userName;
    /*密码*/
    private static String password;
    TextView nameTextView;
    TextView swuIDTextView;
    /*保存用户信息*/
    private static TotalInfo totalInfo = new TotalInfo();
//
//    /*刷新菜单按钮状态,初始化为不显示*/
//    private static int freshMenuStatus = Constant.DISSHOW;

    /*用户信息的本地储存文件*/
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    /*主界面布局*/
    private static MainPageFragment mainPageFragment;
    /*课程表界面布局*/
//    private static ScheduleFragment scheduleFragment;
    private static ScheduleTableFragment scheduleTableFragment;

    /*成绩界面布局*/
    private static GradesFragment gradesFragment;
    /*学习资料界面布局*/
    private static StudyMaterialsFragment studyMaterialsFragment;
    /*图书馆界面布局*/
    private static LibraryFragment libraryFragment;
    /*水电费界面布局*/
    private static ChargeFragment chargeFragment;
    /*失物找寻界面布局*/
    private static FindLostFragment findLostFragment;
    private static int fragmentPosition = R.id.nav_main;
    /*对fragmengt进行管理*/
    private FragmentManager fragmentManager;
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

 /*打开保存用户信息的文件*/
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userName = sharedPreferences.getString("userName", "");
        password = sharedPreferences.getString("password", "");

        editor.commit();


        fragmentManager = getSupportFragmentManager();
        nameValuePairsLoginLibrary = new ArrayList<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        /*侧边栏显示姓名学号*/
        nameTextView = (TextView) view.findViewById(R.id.name);
        swuIDTextView = (TextView) view.findViewById(R.id.swuid);
        /*显示退出按钮*/
        ImageButton imageButtonLoginOut = (ImageButton) view.findViewById(R.id.login_out);
        imageButtonLoginOut.setOnClickListener(MainActivity.this);
        if (userName.equals(""))
        {
            nameTextView.setOnClickListener(this);
            Toast.makeText(this, "您还没有登录呦", Toast.LENGTH_SHORT).show();
        } else
        {
            totalInfo.setName(sharedPreferences.getString("name", ""));
            totalInfo.setSwuID(sharedPreferences.getString("swuID", ""));
            /*对侧边栏的姓名和学号进行配置*/
            swuIDTextView.setText(totalInfo.getSwuID());
            nameTextView.setText(totalInfo.getName());
            nameValuePairsLoginLibrary.add(new BasicNameValuePair("passWord", password));
            nameValuePairsLoginLibrary.add(new BasicNameValuePair("userName", userName));
            //登陆图书馆
            loginLibrary(nameValuePairsLoginLibrary);
        }
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
//
        fragmentStateCheck(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    /*获得某个活动的回复信息*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    userName = data.getStringExtra("userName");
                    password = data.getStringExtra("password");
                    totalInfo.setName(data.getStringExtra("name"));
                    totalInfo.setSwuID(data.getStringExtra("swuID"));
                      /*对侧边栏的姓名和学号进行配置*/
                    swuIDTextView.setText(totalInfo.getSwuID());
                    nameTextView.setText(totalInfo.getName());
                    nameTextView.setClickable(false);
                    //登陆图书馆
                    nameValuePairsLoginLibrary.add(new BasicNameValuePair("passWord", password));
                    nameValuePairsLoginLibrary.add(new BasicNameValuePair("userName", userName));
                    loginLibrary(nameValuePairsLoginLibrary);
                }
        }
    }

    private void fragmentStateCheck(Bundle saveInstanceState)
    {
        if (saveInstanceState == null)
        {
            initFragment();
            fragmentSelection(fragmentPosition);
        } else
        {
            if (mainPageFragment != null)
            {
                mainPageFragment = (MainPageFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[0]);
            }

            if (scheduleTableFragment != null)
            {
                scheduleTableFragment = (ScheduleTableFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[1]);
            }
            if (gradesFragment != null)
            {
                gradesFragment = (GradesFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[2]);
            }
            if (studyMaterialsFragment != null)
            {
                studyMaterialsFragment = (StudyMaterialsFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[3]);
            }
            if (findLostFragment != null)
            {
                findLostFragment = (FindLostFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[4]);
            }
            if (chargeFragment != null)
            {
                chargeFragment = (ChargeFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[5]);
            }
            if (libraryFragment != null)
            {
                libraryFragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENTTAG[6]);
            }
//            hideFragments(fragmentManager.beginTransaction());
            fragmentSelection(fragmentPosition);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_main)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_grades)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_schedule)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_study_materials)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_library)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_charge)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_find_lost)
        {
            fragmentSelection(id);
        } else if (id == R.id.nav_share)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        /*阻止活动被销毁*/
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            /*点击退出按钮*/
            case R.id.login_out:

            /*退出确认框*/
                final AlertDialog.Builder dialogsQuit;
                dialogsQuit = new AlertDialog.Builder(MainActivity.this);

                dialogsQuit.setMessage("确认退出");
                dialogsQuit.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                dialogsQuit.setPositiveButton("确认", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    /*确认退出,清除保存的用户信息,并退出应用*/
                        editor.clear();
                        editor.commit();
                        System.exit(0);
                    }
                });
            /*显示警告框*/
                dialogsQuit.show();
                break;
            case R.id.name:
//             开启登陆活动,并要求获得回复信息
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);

        }
    }


    private void initFragment()
    {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        mainPageFragment = new MainPageFragment();
        transaction.add(R.id.content, mainPageFragment, Constant.FRAGMENTTAG[0]);


        scheduleTableFragment = new ScheduleTableFragment();
        transaction.add(R.id.content, scheduleTableFragment, Constant.FRAGMENTTAG[1]);

        gradesFragment = new GradesFragment();
        transaction.add(R.id.content, gradesFragment, Constant.FRAGMENTTAG[2]);

        studyMaterialsFragment = new StudyMaterialsFragment();
        transaction.add(R.id.content, studyMaterialsFragment, Constant.FRAGMENTTAG[3]);

        findLostFragment = new FindLostFragment();
        transaction.add(R.id.content, findLostFragment, Constant.FRAGMENTTAG[4]);

        chargeFragment = new ChargeFragment();
        transaction.add(R.id.content, chargeFragment, Constant.FRAGMENTTAG[5]);

        libraryFragment = new LibraryFragment();
        transaction.add(R.id.content, libraryFragment, Constant.FRAGMENTTAG[6]);
        transaction.commit();
    }

    private void fragmentSelection(int id)
    {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (id)
        {
            case R.id.nav_main:
                if (mainPageFragment == null)
                {
                    // 如果mainPageFragment为空，则创建一个并添加到界面上
                    mainPageFragment = new MainPageFragment();
                    transaction.add(R.id.content, mainPageFragment, Constant.FRAGMENTTAG[0]);
                } else
                {
                    // 如果mainPageFragment不为空，则直接将它显示出来
                    transaction.show(mainPageFragment);
                }
                /*记录当前显示页面*/
                fragmentPosition = id;
                break;
            case R.id.nav_schedule:
                if (scheduleTableFragment == null)
                {
                    // 如果scheduleTableFragment为空，则创建一个并添加到界面上
                    scheduleTableFragment = new ScheduleTableFragment();
                    transaction.add(R.id.content, scheduleTableFragment, Constant.FRAGMENTTAG[1]);
                } else
                {
                    // 如果scheduleFragment不为空，则直接将它显示出来
                    transaction.show(scheduleTableFragment);
                }
                /*记录当前显示页面*/
                fragmentPosition = id;
                break;
            case R.id.nav_grades:

                if (gradesFragment == null)
                {
                    // 如果GradesFragment为空，则创建一个并添加到界面上
                    gradesFragment = new GradesFragment();
                    transaction.add(R.id.content, gradesFragment, Constant.FRAGMENTTAG[2]);
                } else
                {
                    // 如果GradesFragment不为空，则直接将它显示出来
                    transaction.show(gradesFragment);
                }
                fragmentPosition = id;
                break;
            case R.id.nav_study_materials:
//                /*设置刷新按钮可见*/
//                freshMenuStatus = Constant.SHOW;
//                getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
//                invalidateOptionsMenu();

                if (studyMaterialsFragment == null)
                {
                    // 如果studyMaterialsFragment为空，则创建一个并添加到界面上
                    studyMaterialsFragment = new StudyMaterialsFragment();
                    transaction.add(R.id.content, studyMaterialsFragment, Constant.FRAGMENTTAG[3]);
                } else
                {
                    // 如果studyMaterialsFragment不为空，则直接将它显示出来
                    transaction.show(studyMaterialsFragment);
                }
                fragmentPosition = id;
                break;
            case R.id.nav_find_lost:
//                /*设置刷新按钮可见*/
//                freshMenuStatus = Constant.SHOW;
//                getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
//                invalidateOptionsMenu();

                if (findLostFragment == null)
                {
                    // 如果findLostFragment为空，则创建一个并添加到界面上
                    findLostFragment = new FindLostFragment();
                    transaction.add(R.id.content, findLostFragment, Constant.FRAGMENTTAG[4]);
                } else
                {
                    // 如果findLostFragment不为空，则直接将它显示出来
                    transaction.show(findLostFragment);
                }
                fragmentPosition = id;
                break;
            case R.id.nav_charge:
//                /*设置刷新按钮可见*/
//                freshMenuStatus = Constant.SHOW;
//                getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
//                invalidateOptionsMenu();

                if (chargeFragment == null)
                {
                    // 如果chargeFragment为空，则创建一个并添加到界面上
                    chargeFragment = new ChargeFragment();
                    transaction.add(R.id.content, chargeFragment, Constant.FRAGMENTTAG[5]);
                } else
                {
                    // 如果chargeFragment不为空，则直接将它显示出来
                    transaction.show(chargeFragment);
                }
                fragmentPosition = id;
                break;
            case R.id.nav_library:

                if (libraryFragment == null)
                {
                    // 如果libraryFragrment为空，则创建一个并添加到界面上
                    libraryFragment = new LibraryFragment();
                    transaction.add(R.id.content, libraryFragment, Constant.FRAGMENTTAG[6]);
                } else
                {
                    // 如果libraryFragrment不为空，则直接将它显示出来
                    transaction.show(libraryFragment);
                }
                fragmentPosition = id;
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction fragmentTransaction)
    {
        if (mainPageFragment != null)
        {
            fragmentTransaction.hide(mainPageFragment);
        }
        if (gradesFragment != null)
        {
            fragmentTransaction.hide(gradesFragment);
        }

        if (scheduleTableFragment != null)
        {
            fragmentTransaction.hide(scheduleTableFragment);
        }
        if (studyMaterialsFragment != null)
        {
            fragmentTransaction.hide(studyMaterialsFragment);
        }
        if (libraryFragment != null)
        {
            fragmentTransaction.hide(libraryFragment);
        }
        if (chargeFragment != null)
        {
            fragmentTransaction.hide(chargeFragment);
        }
        if (findLostFragment != null)
        {
            fragmentTransaction.hide(findLostFragment);
        }

    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void loginLibrary(final List<NameValuePair> nameValuePairs){
        GetMyLibraryInfo.Init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetMyLibraryInfo.libraryLogin(nameValuePairs);
            }
        }).start();
    }
}
