package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.bodongpractice.Class.GMailSender;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.NicknameCheckInterface;
import com.example.bodongpractice.Interface.UserInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;
import android.os.Message;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;


import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {

    EditText emailText;
    EditText nicknameText;
    EditText passwordText;
    EditText passwordCheckText;

    TextView nicknameResultText;
    TextView passwordResultText;
    TextView passwordCheckResultText;
    //닉네임 중복 버튼
    Button nicknameCheckButton;

    //인증번호 발송 버튼
    Button sendEmailButton;
    //인증번호 입력하는 곳
    EditText emailCodeText;
    Button emailCodeButton;
    //이메일 보내면 메일을 보냈다고 뜨는 텍스트
    TextView sendEmailText;
    TextView emailCodeResultText;

    //메일을 보내고 나서 시간이 바뀌는 변수
    //메일을 한번 더 보내면 이 값이 시간만 변하게 한다.
    static int value;

    //메일을 한번 보냈는지 다시 적어서 보냈는지를 분기로 사용하는 int변수
    //메일을 두번이상 보내게 되면 시간이 흐르는 쓰레드를 다시 생성하지 않게 한다.
    int mailSend=0;


    MainHandler mainHandler;

    //인증코드
    String GmailCode;

    //회원가입 버튼
    Button registerButton;


    boolean emailIsTrue=false;
    boolean nicknameIsTrue=false;
    boolean passwordIsTrue=false;
    boolean passwordCheckIsTrue=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("회원가입");



        emailText = findViewById(R.id.email);
        nicknameText = findViewById(R.id.nickname);
        passwordText = findViewById(R.id.password);
        passwordCheckText = findViewById(R.id.passwordCheck);
        passwordCheckResultText = findViewById(R.id.passwordCheckResultText);



        //닉네임 중복확인 결과 텍스트
        nicknameResultText= findViewById(R.id.nicknameResultText);
        nicknameResultText.setVisibility(View.GONE);



        //이메일 인증 번호 텍스트
        emailText = findViewById(R.id.email);
        //이메일 보내고 보냈다는 메세지를 출력하는 텍스트
        sendEmailText = findViewById(R.id.sendEmailText);
        emailCodeText= findViewById(R.id.emailCodeText);
        sendEmailButton = findViewById(R.id.sendEmailButton);
        emailCodeButton= findViewById(R.id.emailCodeButton);

        //비밀번호 결과 텍스트
        passwordResultText=findViewById(R.id.passwordResultText);
        passwordResultText.setVisibility(View.GONE);

        emailCodeResultText = findViewById(R.id.emailCodeResultText);
        //이메일 인증하는 창을 지워준다
        emailCodeText.setVisibility(View.GONE);
        emailCodeButton.setVisibility(View.GONE);
        sendEmailText.setVisibility(View.GONE);
        emailCodeResultText.setVisibility(View.GONE);

        //닉네임 중복확인
        nicknameCheckButton = findViewById(R.id.nicknameCheckButton);
        nicknameCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nicknameCheck();
            }
        });





        //이메일 인증 보내기
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                이메일 인증부분을 보여준다.
//메일을 보내주는 쓰레드
                MailTread mailTread = new MailTread();
                mailTread.start();

                if(mailSend==0){
                    value=180;
                    //쓰레드 객체 생성
                    BackgrounThread backgroundThread = new BackgrounThread();
                    //쓰레드 스타트
                    backgroundThread.start();
                    mailSend+=1;
                }else{
                    value = 180;
                }


                //이메일이 보내지면 이 부분을 실행시킨다.
                sendEmailText.setVisibility(View.VISIBLE);
                emailCodeText.setVisibility(View.VISIBLE);
                emailCodeButton.setVisibility(View.VISIBLE);
                sendEmailText.setText("입력하신 이메일로 인증번호를 보냈습니다");
                sendEmailText.setTextColor(Color.parseColor("#E67A00"));

//핸들러 객체 생성
                mainHandler=new MainHandler();



            }
        });




//인증하는 버튼이다
        //혹시 이거랑 같으면 인증을 성공시켜라라
        emailCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //이메일로 전송한 인증코드와 내가 입력한 인증코드가 같을 때
                if(emailCodeText.getText().toString().equals(GmailCode)){
                    emailCodeResultText.setVisibility(View.VISIBLE);
                    emailCodeResultText.setText("이메일이 인증되었습니다");
                    emailCodeResultText.setTextColor(Color.parseColor("#E67A00"));
                    emailIsTrue=true;
                    registerOk();
                    emailCodeButton.setEnabled(false);
                    sendEmailButton.setEnabled(false);
                }else{
                    emailCodeResultText.setVisibility(View.VISIBLE);
                    emailCodeResultText.setText("인증번호를 다시 입력해주세요");
                    emailCodeResultText.setTextColor(Color.parseColor("#ff0000"));


                }
            }
        });

        //회원가입이 활성화 되고 나서 닉네임을 변경할 수 있기 때문에 닉네임을 다시 입력할때 회원가입을 비활성화 시켜준다
        TextWatcher nicknameWatcher = new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerButton.setEnabled(false);
                registerButton.setBackgroundColor(Color.parseColor("#808080"));
                nicknameResultText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        nicknameText.addTextChangedListener(nicknameWatcher);









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
                String passwordResultCheckText =  passwordText.getText().toString();
                String pattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$";
                boolean check = Pattern.matches(pattern,passwordResultCheckText);
                if(check){
                    System.out.println("정규식에 맞음");
                    passwordResultText.setVisibility(View.VISIBLE);
                    passwordResultText.setText("사용가능한 비밀번호입니다");
                    passwordResultText.setTextColor(Color.parseColor("#E67A00"));
                    passwordIsTrue=true;
                    registerOk();
                }else{
                    System.out.println("정규식에 안맞음");
                    passwordResultText.setVisibility(View.VISIBLE);

                    passwordResultText.setText("사용불가능한 비밀번호입니다");
                    passwordResultText.setTextColor(Color.parseColor("#ff0000"));
                    passwordIsTrue=false;
                    registerOk();
                }
            }
        };
        passwordText.addTextChangedListener(watcher);



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
                String passwordResultCheckText2 =  passwordCheckText.getText().toString();

                boolean check = Pattern.matches(passwordText.getText().toString(),passwordResultCheckText2);
                if(check){

                    passwordCheckResultText.setText("비밀번호가 서로 일치합니다");
                    passwordCheckResultText.setTextColor(Color.parseColor("#E67A00"));
                    passwordCheckIsTrue=true;
                    registerOk();
                }else{

                    passwordCheckResultText.setText("비밀번호가 서로 다릅니다");
                    passwordCheckResultText.setTextColor(Color.parseColor("#ff0000"));
                    passwordCheckIsTrue=false;
                    registerOk();
                }
            }
        };
        passwordCheckText.addTextChangedListener(watcher2);

        //회원가입 버튼
        registerButton=findViewById(R.id.registerButton);
        //회원가입 버튼 비 활성화
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMe();
            }
        });





    }

    //닉네임 중복 확인 메서드
    private void nicknameCheck(){
        //닉네임 텍스트 받아온다.
        final String nickname = nicknameText.getText().toString();
        NicknameCheckInterface nicknameInterface = ApiClient.getApiClient().create(NicknameCheckInterface.class);
        //유저가 입력한 닉네임을 맴버변수로 넣어준다
        Call<User> call = nicknameInterface.getUserNickname(nickname);
        call.enqueue(new Callback<User>()
        {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    //통신이 잘 되었을 때 결과 확인 로그
                    Log.e("onSuccess", response.body().getStatus());
                    //텍스트를 잡아온다

                    //닉네임 중복이 안 될때

                    String status = response.body().getStatus();
                    System.out.println(status);
                    if(status.contains("true")){
                        System.out.println("사용가능");
                        nicknameResultText.setVisibility(View.VISIBLE);
                        nicknameResultText.setText(response.body().getMessage());
                        nicknameResultText.setTextColor(Color.parseColor("#E67A00"));
                        nicknameIsTrue=true;
                        registerOk();
                    }
                    //닉네임이 중복됐을 때
                    else{
                        System.out.println("중복됨");
                        nicknameResultText.setVisibility(View.VISIBLE);
                        nicknameResultText.setText(response.body().getMessage());
                        nicknameResultText.setTextColor(Color.parseColor("#ff0000"));
                        nicknameIsTrue=false;
                        registerOk();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    //회원가입 전부 맞으면 실행되는 메서드
    private void registerOk(){
        if(emailIsTrue && nicknameIsTrue && passwordIsTrue && passwordCheckIsTrue){
            System.out.println("전부 맞음");
            registerButton.setEnabled(true);
            registerButton.setBackgroundColor(Color.parseColor("#E67A00"));
        }else{
            System.out.println("아님");
            registerButton.setEnabled(false);
        }
    }



    private void registerMe()
    {
        final String email = emailText.getText().toString();
        final String nickname = nicknameText.getText().toString();
        final String password = passwordText.getText().toString();


        //레트로핏 객체 생성
        //여기서 회원가입 url에 접근한다.
        //ApiClient를 스태틱으로 만들었기 때문에 객체가 생성되었고 회원가입 인터페이스를 붙여서 baseURL에 접근시킨다.
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);

        //getUserRegist의 인자값으로 유저의 정보를 넣어주고 registerInterface가 회원가입 db에 접근하여 데이터를 파싱해온다.
        //Call<String>을 Call<Person> 과 같이 객체를 넣어서 객체에 대한 정보를 가지고 올 수 있다.
        Call<User> call = userInterface.getUserRegist(email, nickname, password);

        call.enqueue(new Callback<User>()
        {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("onSuccess", response.body().getMessage());
                    finish();

                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }


    //메일 보내는 쓰레드
    class MailTread extends Thread{

        public void run(){
            GMailSender gMailSender = new GMailSender("dlrhkdgh21@gmail.com", "sgmzhgbyyzmszijw");
            //GMailSender.sendMail(제목, 본문내용, 받는사람);


            //인증코드
            GmailCode=gMailSender.getEmailCode();
            try {
                String html= mailContent(GmailCode);
                gMailSender.sendMail("보동보동 회원가입 이메일 인증", html , emailText.getText().toString());
            } catch (SendFailedException e) {

            } catch (MessagingException e) {
                System.out.println("인터넷 문제"+e);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    //시간초가 카운트 되는 쓰레드
    class BackgrounThread extends Thread{
        //180초는 3분
        //메인 쓰레드에 value를 전달하여 시간초가 카운트다운 되게 한다.

        public void run(){
            //180초 보다 밸류값이 작거나 같으면 계속 실행시켜라
            while(true){
                value-=1;
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }

                Message message = mainHandler.obtainMessage();
                //메세지는 번들의 객체 담아서 메인 핸들러에 전달한다.
                Bundle bundle = new Bundle();
                bundle.putInt("value", value);
                message.setData(bundle);

                //핸들러에 메세지 객체 보내기기

                mainHandler.sendMessage(message);

                if(value<=0){
                    GmailCode="";
                    break;
                }
            }



        }
    }



    //쓰레드로부터 메시지를 받아 처리하는 핸들러
    //메인에서 생성된 핸들러만이 Ui를 컨트롤 할 수 있다.
    class MainHandler extends Handler{
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            int min, sec;

            Bundle bundle = message.getData();
            int value = bundle.getInt("value");

            min = value/60;
            sec = value % 60;
            //초가 10보다 작으면 앞에 0이 더 붙어서 나오도록한다.
            if(sec<10){
                //텍스트뷰에 시간초가 카운팅
                emailCodeText.setHint("0"+min+" : 0"+sec);
            }else {
                emailCodeText.setHint("0"+min+" : "+sec);
            }

        }
    }




    private String mailContent(String code){
        String content="<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:700px;margin:0 auto;padding:0;border: 1px solid #eee;\">\n" +
                "        <tbody>\n" +
                "        <tr><td style=\"width:100%;max-width:700px;padding:20px 15px;background:#4b58a7;text-align:center;font-size:32px;line-height:1.3;color:#fff;letter-spacing:-0.025em;font-family:AppleSDGothicNeo-Regular,HelveticaNeue,'맑은고딕',Malgun Gothic,나눔고딕,NanumGothic,'돋움',Dotum,Sans-serif;font-weight: bold; word-break: keep-all\">\n" +
                "            보동보동 회원가입\n" +
                "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:700px;margin:0 auto;padding:0;\">\n" +
                "                    <tbody><tr><td style=\"width:100%;max-width:700px;text-align:center;font-size:14px;line-height:1.3;color:#dfe3fe;letter-spacing:-0.025em;font-family:AppleSDGothicNeo-Regular,HelveticaNeue,'맑은고딕',Malgun Gothic,나눔고딕,NanumGothic,'돋움',Dotum,Sans-serif;font-weight: normal; padding-top: 3px;word-break: keep-all\">회원가입을 위해 아래 인증번호를 입력하여주세요</td></tr>\n" +
                "                </tbody></table>\n" +
                "            </td></tr>\n" +
                "        <tr><td>\n" +
                "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:700px;\">\n" +
                "                    <tbody><tr><td style=\"width:100%;max-width:740px;padding:0 25px\">\n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:740px;\">\n" +
                "                                <tbody><tr><td style=\"font-weight:bold; text-align:center;font-size:18px;color:#000;font-family:AppleSDGothicNeo-Regular,HelveticaNeue,'맑은고딕',Malgun Gothic,나눔고딕,NanumGothic,'돋움',Dotum,Sans-serif;letter-spacing:-0.025em;padding-top: 20px\">인증 코드 : <span style=\"color: #4b58a7;\">"+code+"</span></td></tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                        <br>\n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:700px\">\n" +
                "                                <tbody>\n" +
                "                                <tr><td style=\"font-size:14px;line-height:20px; text-align:center;color:#555;font-family:AppleSDGothicNeo-Regular,HelveticaNeue,'맑은고딕',Malgun Gothic,나눔고딕,NanumGothic,'돋움',Dotum,Sans-serif;padding-left:2px;letter-spacing: -0.025em\">\n" +
                "                                        *  이메일 인증 코드는 <span style=\"font-weight: bold;\">3분간만</span> 유효합니다.\n" +
                "                                    </td></tr>\n" +
                "                            </tbody></table>\n" +
                "                        </td></tr>\n" +
                "                </tbody></table>\n" +
                "            </td></tr>\n" +
                "    </tbody>\n" +
                " </table>";
        return content;
    }

}



