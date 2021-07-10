package com.example.bodongpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bodongpractice.Class.GMailSender;
import com.example.bodongpractice.Class.TitleBar;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SendEmailCode extends AppCompatActivity {


    EditText emailCode;
    TextView emailCodeCheckText;
    Button emailCodeCheckButton;

    //03:00시간초 그려주는 핸들러
    MainHandler mainHandler;

    //인증코드
    String GmailCode;

    //이전 화면에서 넘어온 이메일 값
    String email;

    static int value = 180;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email_code);

        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("비밀번호 재설정");



        emailCode= findViewById(R.id.emailCode);
        emailCodeCheckText = findViewById(R.id.emailCodeCheckText);
        emailCodeCheckButton = findViewById(R.id.emailCodeCheckButton);

        emailCodeCheckText.setVisibility(View.GONE);
        emailCodeCheckButton.setEnabled(false);



        Intent secondIntent = getIntent();
        email = secondIntent.getStringExtra("이메일");
        System.out.println("받아온 이메일" + email);
        //메일을 보내주는 쓰레드
        MailTread mailTread = new MailTread();
        mailTread.start();




        //쓰레드 객체 생성
        BackgrounThread backgroundThread = new BackgrounThread();
        //쓰레드 스타트
        backgroundThread.start();



        //핸들러 객체 생성
        mainHandler=new MainHandler();


        //인증코드 입력 시 인증확인 버튼 활성화
        //비밀번호 실시간 입력 정규식 확인
        TextWatcher watcher = new TextWatcher(){
            //텍스트 길이가 변경될때마다 발생하는 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //텍스트가 변경될때마다 실행되는 메서드
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailText = emailCode.getText().toString();
                emailCodeCheckText.setVisibility(View.GONE);
                //누르는 순간 인증버튼이 활성화되야 한다.
                emailCodeCheckButton.setEnabled(true);
                emailCodeCheckButton.setBackgroundColor(Color.parseColor("#E67A00"));

                if(emailText.isEmpty()){
                    System.out.println("비어있음");
                    emailCodeCheckButton.setEnabled(false);
                    emailCodeCheckButton.setBackgroundColor(Color.parseColor("#808080"));
                }
            }
            //입력이 완료된 후에 처리하는 메서드
            @Override
            public void afterTextChanged(Editable s) {




            }
        };
        emailCode.addTextChangedListener(watcher);




        //코드 인증 버튼
        //인증이 성공하면 다음 비밀번호 변경 액티비티로 이동해야하는데
        //이때 이메일을 같이 넘겨서 진행한다
        emailCodeCheckButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                 Intent intent = new Intent(Login.this, Register.class);
//                 startActivity(intent);

                 if(emailCode.getText().toString().equals(GmailCode)){

                     System.out.println("인증됨");
                     Intent intent = new Intent(SendEmailCode.this, PasswordChange.class);
                     intent.putExtra("이메일", email);
                     startActivity(intent);
                     finish();
                 }else{
                     emailCodeCheckText.setVisibility(View.VISIBLE);
                 }
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
                gMailSender.sendMail("보동보동 비밀번호 재설정 이메일 인증", html , email);
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
    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            int min, sec;

            Bundle bundle = message.getData();
            int value = bundle.getInt("value");

            min = value/60;
            sec = value % 60;
            //텍스트뷰에 시간초가 카운팅
//            textView.setText(min+" : "+value);
//            emailCode.setHint("0"+min+" : "+value);
            if(sec<10){
                //텍스트뷰에 시간초가 카운팅
                emailCode.setHint("0"+min+" : 0"+sec);
            }else {
                emailCode.setHint("0"+min+" : "+sec);
            }
        }
    }

    private String mailContent(String code){
        String content="<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:700px;margin:0 auto;padding:0;border: 1px solid #eee;\">\n" +
                "        <tbody>\n" +
                "        <tr><td style=\"width:100%;max-width:700px;padding:20px 15px;background:#4b58a7;text-align:center;font-size:32px;line-height:1.3;color:#fff;letter-spacing:-0.025em;font-family:AppleSDGothicNeo-Regular,HelveticaNeue,'맑은고딕',Malgun Gothic,나눔고딕,NanumGothic,'돋움',Dotum,Sans-serif;font-weight: bold; word-break: keep-all\">\n" +
                "            비밀번호 재설정\n" +
                "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;max-width:700px;margin:0 auto;padding:0;\">\n" +
                "                    <tbody><tr><td style=\"width:100%;max-width:700px;text-align:center;font-size:14px;line-height:1.3;color:#dfe3fe;letter-spacing:-0.025em;font-family:AppleSDGothicNeo-Regular,HelveticaNeue,'맑은고딕',Malgun Gothic,나눔고딕,NanumGothic,'돋움',Dotum,Sans-serif;font-weight: normal; padding-top: 3px;word-break: keep-all\">비밀번호 재설정을 위해 아래 인증번호를 입력하여주세요</td></tr>\n" +
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