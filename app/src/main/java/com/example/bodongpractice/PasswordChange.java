package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.ChangePasswordInterface;
import com.example.bodongpractice.Interface.FindPasswordInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordChange extends AppCompatActivity {

    EditText password;

    EditText passwordCheck;

    TextView passwordResultText;

    TextView passwordCheckResultText;

    Button passwordChangeButton;


    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);


        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("비밀번호 재설정");



        Intent secondIntent = getIntent();
        email = secondIntent.getStringExtra("이메일");
        System.out.println("받아온 이메일" + email);


        password= findViewById(R.id.password);
        passwordCheck= findViewById(R.id.passwordCheck);
        passwordResultText= findViewById(R.id.passwordResultText);
        passwordCheckResultText= findViewById(R.id.passwordCheckResultText);
        passwordChangeButton= findViewById(R.id.passwordChangeButton);

        //비밀번호 확인 텍스트 지워준다
        passwordResultText.setVisibility(View.GONE);
        passwordCheckResultText.setVisibility(View.GONE);


        //버튼 비활성화
        passwordChangeButton.setEnabled(false);

        //일단 비밀번호가 정규식에 맞는지 확인 먼저 한다.

        //비밀번호 실시간 입력 정규식 확인
        TextWatcher watcher = new TextWatcher(){
            //텍스트 길이가 변경될때마다 발생하는 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //텍스트가 변경될때마다 실행되는 메서드
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //입력이 완료된 후에 처리하는 메서드
            @Override
            public void afterTextChanged(Editable s) {
                String passwordResultCheckText =  password.getText().toString();
                String pattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$";
                boolean check = Pattern.matches(pattern,passwordResultCheckText);
                if(check){
                    System.out.println("정규식에 맞음");
                    passwordResultText.setVisibility(View.VISIBLE);
                    passwordResultText.setText("사용가능한 비밀번호입니다");
                    passwordResultText.setTextColor(Color.parseColor("#E67A00"));


                }else{
                    System.out.println("정규식에 안맞음");
                    passwordResultText.setVisibility(View.VISIBLE);
                    passwordResultText.setText("사용불가능한 비밀번호입니다");
                    passwordResultText.setTextColor(Color.parseColor("#ff0000"));

                }
            }
        };
        password.addTextChangedListener(watcher);



        //비밀번호 확인 실시간 체크
        TextWatcher watcher2 = new TextWatcher(){
            //텍스트 길이가 변경될때마다 발생하는 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //텍스트가 변경될때마다 실행되는 메서드
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //입력이 완료된 후에 처리하는 메서드
            @Override
            public void afterTextChanged(Editable s) {
                String passwordResultCheckText2 =  passwordCheck.getText().toString();

                boolean check = Pattern.matches(password.getText().toString(),passwordResultCheckText2);
                if(check){
                    passwordCheckResultText.setVisibility(View.VISIBLE);
                    passwordCheckResultText.setText("비밀번호가 서로 일치합니다");
                    passwordCheckResultText.setTextColor(Color.parseColor("#E67A00"));

                    //이때 변경버튼 활성화
                    changeButtonTrue();

                }else{
                    passwordCheckResultText.setVisibility(View.VISIBLE);
                    passwordCheckResultText.setText("비밀번호가 서로 다릅니다");
                    passwordCheckResultText.setTextColor(Color.parseColor("#ff0000"));
                    changeButtonFalse();

                }
            }
        };
        passwordCheck.addTextChangedListener(watcher2);





        //passwordChangeButton 버튼 비밀번호 변경이 이루어진다.
        passwordChangeButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                 Intent intent = new Intent(Login.this, Register.class);
//                 startActivity(intent);
                 //TODO:이메일을 이전 인텐트에 건내받아서 같이 날려줄것
//                 String emailText = email.getText().toString();
                 String emailText = email;
                 String passwordText = password.getText().toString();

                 ChangePasswordInterface changePasswordInterface = ApiClient.getApiClient().create(ChangePasswordInterface.class);

                 //getUserRegist의 인자값으로 유저의 정보를 넣어주고 registerInterface가 회원가입 db에 접근하여 데이터를 파싱해온다.
                 //Call<String>을 Call<Person> 과 같이 객체를 넣어서 객체에 대한 정보를 가지고 올 수 있다.
                 Call<User> call = changePasswordInterface.getUserRegist(emailText,passwordText);

                 call.enqueue(new Callback<User>()
                 {
                     //연결 성공 시에 싱행되는 부분
                     @Override
                     public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
                     {

                         Log.e("onSuccess", response.body().getStatus());
                         Log.e("onSuccess", response.body().getMessage());


                         String status = response.body().getStatus();

                         System.out.println(response.body().getMessage());

                         if(status.contains("true")){

                             System.out.println("비밀번호가 변경되었습니다");
                             //변경되었으면 다른 화면으로 이동
                             finish();

                         }

                         else{
                             System.out.println("비밀번호가 변경되지 않았습니다");


                         }

                     }

                     @Override
                     public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
                     {
                         Log.e("onFail", "에러 = " + t.getMessage());
                     }
                 });
             }
         });

    }

    //버튼 비활성화 메서드
    private void changeButtonTrue(){
        passwordChangeButton.setEnabled(true);
        passwordChangeButton.setBackgroundColor(Color.parseColor("#E67A00"));
    }

    //버튼 활성화 메서드

    //버튼 비활성화 메서드
    private void changeButtonFalse(){
        passwordChangeButton.setEnabled(false);
        passwordChangeButton.setBackgroundColor(Color.parseColor("#808080"));
    }
}