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
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.FindPasswordInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPassword extends AppCompatActivity {

    TextView emailCheckText;
    EditText email;
    Button emailSendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        email=findViewById(R.id.email);
        //가입되지 않은 이메일 입력시 가입하라는 이메일 출력부분
        emailCheckText= findViewById(R.id.emailCheckText);
        emailCheckText.setVisibility(View.GONE);
        emailSendButton=findViewById(R.id.emailSendButton);


        //버튼 비활성화
        emailSendButton.setEnabled(false);
        emailSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일은 당연히 누르는 순간 텍스트로 반환이 되어야 하기 때문에 클릭메서드 안에 있어야한다.ㅜㅜ

                String emailText = email.getText().toString();
//                Intent intent = new Intent(Login.this, Register.class);
//                startActivity(intent);
                //중복 체크 후 이메일 보내주는 메서드

                //레트로핏 객체 생성
                //여기서 회원가입 url에 접근한다.
                //ApiClient를 스태틱으로 만들었기 때문에 객체가 생성되었고 회원가입 인터페이스를 붙여서 baseURL에 접근시킨다.
                FindPasswordInterface findPasswordInterface = ApiClient.getApiClient().create(FindPasswordInterface.class);

                //getUserRegist의 인자값으로 유저의 정보를 넣어주고 registerInterface가 회원가입 db에 접근하여 데이터를 파싱해온다.
                //Call<String>을 Call<Person> 과 같이 객체를 넣어서 객체에 대한 정보를 가지고 올 수 있다.
                Call<User> call = findPasswordInterface.getUserRegist(emailText);

                call.enqueue(new Callback<User>()
                {
                    //연결 성공 시에 싱행되는 부분
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
                    {

                        Log.e("onSuccess", response.body().getStatus());



                        String status = response.body().getStatus();
                        //존재하지 않은 이메일일 경우 실행
                        if(status.contains("true")){
                            //존재안한다고 텍스트 출력
                            emailCheckText.setVisibility(View.VISIBLE);
                            System.out.println("존재하지 않는 이메일");
                        }
                        //존재하는 이메일일 경우 다음 창으로 이동
                        else{
                            System.out.println("존재하는 이메일");

                            Intent intent = new Intent(FindPassword.this, SendEmailCode.class);
                            intent.putExtra("이메일", emailText);
                            startActivity(intent);
                            finish();
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






        //이메일이 정규식에 맞는지 알아내는 패턴
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

        //비밀번호 실시간 입력 정규식 확인
        TextWatcher watcher = new TextWatcher(){
            //텍스트 길이가 변경될때마다 발생하는 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //텍스트가 변경될때마다 실행되는 메서드
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailText = email.getText().toString();
                emailCheckText.setVisibility(View.GONE);
                if(pattern.matcher(emailText).matches()){
                    //이메일이 맞다
                    System.out.println("이메일 맞다");
                    emailSendButton.setEnabled(true);
                    emailSendButton.setBackgroundColor(Color.parseColor("#E67A00"));
                }else{
                    //이메일 아니다
                    emailSendButton.setEnabled(false);
                    emailSendButton.setBackgroundColor(Color.parseColor("#808080"));
                    System.out.println("이메일 아니다");
                }
            }
            //입력이 완료된 후에 처리하는 메서드
            @Override
            public void afterTextChanged(Editable s) {




            }
        };
        email.addTextChangedListener(watcher);
    }
}