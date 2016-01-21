package com.example.swuassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tool.Login;
import com.example.tool.TotalInfo;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener
{


    /*账号框*/
    private EditText mUserNAmeView;
    /*密码框*/
    private EditText mPasswordView;
    /*账号*/
    private String userName;
    /*密码*/
    private String password;
    /*接收返回信息*/
    private String response;
    /*等待窗口*/
    private static ProgressDialog progressDialogLoading;
    /*保存个人信息*/
    private TotalInfo totalInfo = new TotalInfo();
    /*ui更新*/
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constant.ID_SUCCESE:
                    /*成功则关闭登陆等待窗口*/
                    progressDialogLoading.cancel();
                    /*开启下一个窗口*/
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("password", password);
                    intent.putExtra("name", totalInfo.getName());
                    intent.putExtra("swuID", totalInfo.getSwuID());

                    startActivity(intent);
                    finish();
                    break;
                case Constant.ID_FAILED:
                    /*登陆失败*/
                    progressDialogLoading.setMessage("登陆失败");
                    progressDialogLoading.setCancelable(true);
                    progressDialogLoading.show();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserNAmeView = (EditText) findViewById(R.id.username);
        progressDialogLoading = new ProgressDialog(LoginActivity.this);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                return id == R.id.login || id == EditorInfo.IME_NULL;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        /*从登陆框获取账号和密码*/
        userName = mUserNAmeView.getText().toString();
        password = mPasswordView.getText().toString();
        /*显示登陆过程窗口*/
        progressDialogLoading.setTitle("登录");
        progressDialogLoading.setMessage("正在登录请稍后");
        progressDialogLoading.setCancelable(false);
        progressDialogLoading.show();
        if (v.getId() == R.id.sign_in_button)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Login login = new Login();
                    /*尝试登陆并获取登陆信息*/
                    response = login.doLogin(userName, password);
                    Message message = new Message();
                    if (response.contains("Successed"))
                    {
                        /*登陆成功获得名字和学号*/
                        totalInfo = login.getTotalInfo();
                        /*发送ui更新*/
                        message.what = Constant.ID_SUCCESE;
                        handler.sendMessage(message);
                    } else
                    {
                        /*登陆失败更新ui*/
                        message.what = Constant.ID_FAILED;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

