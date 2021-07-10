package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.UserInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class ProfileChange extends AppCompatActivity {

    ImageView userProfile;


    EditText userNicknameText;

    TextView profileCommitText;

    ArrayList<User> currentUserArrayList = new ArrayList();
    ArrayList<User> autoLoginUserArrayList = new ArrayList();


    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private File tempFile;

    Uri savingUri;

    TextView nicknameCheckText;

    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("프로필 수정");


        //닉네임 중복 텍스트 안보이게
        nicknameCheckText = findViewById(R.id.nicknameCheckText);
        nicknameCheckText.setVisibility(View.GONE);


        //프로필 이미지 수정
        userProfile=findViewById(R.id.userProfile);

        userNicknameText=findViewById(R.id.userNicknameText);

        //일단 유저정보를 가지고 온다.
        //유저정보를 가지고 와서 닉네임을 표시해준다.
        currentUserLoadData();

        //정보창에 이메일 닉네임 표시
        //유저 이메일
        User user = currentUserArrayList.get(0);

        userEmail = user.getEmail();
        System.out.println("프로필수정 유저 이메일"+userEmail);

        //유저 닉네임 찾아오기
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        //유저가 입력한 닉네임을 맴버변수로 넣어준다
        Call<User> call = userInterface.getUserProfile(userEmail);
        call.enqueue(new Callback<User>()
        {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    //통신이 잘 되었을 때 결과 확인 로그
                    Log.e("onSuccess", response.body().getStatus());


                    String status = response.body().getStatus();
                    System.out.println(status);
                    if(status.contains("true")){
                        //닉네임 세팅
                        userNicknameText.setHint(response.body().getNickname());


                        Glide.with(ProfileChange.this)
                                .load("http://49.247.213.240/UserProfileImg/"+response.body().getProfile())
                                .circleCrop()
                                .into(userProfile);
                    }
                    else{

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

        //이미지 업로드
        //추후에 완료 버튼 눌렀을때 같이 수정되도록
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("사진");

                tedPermission();

                photoDialogList();




            }
        });




        //수정 완료 버튼
        profileCommitText = findViewById(R.id.profileCommitText);

        profileCommitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //닉네임 스트링을 받아온다
                String nickname = userNicknameText.getText().toString();

                if(nickname.isEmpty()){
                    System.out.println("닉네임이 비었음");
                }else{
                    UserInterface userInterface =  ApiClient.getApiClient().create(UserInterface.class);
                    Call<User> call = userInterface.getUserProfileCommit(userEmail,nickname);
                    call.enqueue(new Callback<User>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                //통신이 잘 되었을 때 결과 확인 로그
                                Log.e("onSuccess", response.body().getStatus());


                                String status = response.body().getStatus();
                                System.out.println(status);
                                if(status.contains("true")){
                                    System.out.println(response.body().getMessage());
                                    finish();
                                }
                                //닉네임이 중복됐을 때
                                else{
                                    System.out.println(response.body().getMessage());
                                    nicknameCheckText.setVisibility(View.VISIBLE);
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


            }
        });





        //비밀번호 실시간 입력 정규식 확인
        TextWatcher watcher = new TextWatcher(){
            //텍스트 길이가 변경될때마다 발생하는 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //텍스트가 변경될때마다 실행되는 메서드
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                nicknameCheckText.setVisibility(View.GONE);
            }
            //입력이 완료된 후에 처리하는 메서드
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        userNicknameText.addTextChangedListener(watcher);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }



        switch (requestCode) {
            case PICK_FROM_ALBUM: {

                Uri photoUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(photoUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);


                    instream.close();   // 스트림 닫아주기
                    //여기서 템프파일에 이미지가 저장이 된다
                    saveBitmapToJpeg(imgBitmap);    // 내부 저장소에 저장
                    Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
                }



                setImage();


                break;
            }
            case PICK_FROM_CAMERA: {

                Uri photoUri = Uri.fromFile(tempFile);


                setImage();



                break;
            }

        }
    }


    //앨범으로로
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //앨범에서 선택한 이미지를 파일로 만들어서 서버에 넘겨주기 위해서 만들어준다
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        try {
            tempFile = createImageFile();
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    //카메라로
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.bodongpractice.fileprovider", tempFile);
                //이 코드가 이미지 파일을 저장하는 코드인가보다
                //tempFile에는 빈파일이 만들어져 있고 photoUri로 위치만 지정되어 있는데
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 를 하면서 MediaStore.EXTRA_OUTPUT을 내용으로 채워주나보다
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    //사진 선택 다이얼로그
    void photoDialogList()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("갤러리");
        ListItems.add("카메라");

        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필사진 설정");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();

                if(selectedText.contains("갤러리")){
                    goToAlbum();
                }else{
                    takePhoto();
                }


            }
        });
        builder.show();
    }

    //이미지 세팅
    private void setImage() {



//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);


        //이미지 회전 방지용 코드
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);


        // 찍은 사진 mPhotoFile을 bitmap으로 decodeFile
        Bitmap cameraphoto = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

        // 찍은 사진 rotate
        Bitmap cameraRotated = rotateBitmap(cameraphoto, orientation);


        // imageview 사진 뜨게함
//        image.setImageBitmap(cameraRotated);
        Glide.with(this)
                .load(cameraRotated)
                .circleCrop()
                .into(userProfile);

        imageUpload(tempFile);


    }

    private void imageUpload(File tempFile) {

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", tempFile.getName(), requestFile);
        UserInterface userInterface =  ApiClient.getApiClient().create(UserInterface.class);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),
                userEmail);
        Call<User> resultCall = userInterface.uploadImage(body,email);
        resultCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e("onSuccess", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("onfail", t.toString());
            }
        });
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //사진을 파일로 만들어서 저장시킨다
    //content: 형식을 File:로 만들어준다.
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "bodong" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }


    //권한 받는 부분
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    //데이터 불러오기
    void currentUserLoadData() {


        //먼저 지슨을 로드해주고
        Gson loadGson = new Gson();
        //리스트의 경우 타입을 지정해줘야한다.
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();


        // 문자열 불러오기
        String loadSharedName = "shared"; // 가져올 SharedPreferences 이름 지정
        String loadKey = "유저정보"; // 가져올 데이터의 Key값 지정
        //여기에 json어레이가 담기도록 해보자
        String loadValue = ""; // 가져올 데이터가 담기는 변수
        String defaultValue = ""; // 가져오는것에 실패 했을 경우 기본 지정 텍스트. 보통 기본 값은 널 값으로 준다.

        SharedPreferences loadShared = getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        currentUserArrayList = loadGson.fromJson(loadValue, type);

        if (currentUserArrayList == null) {
            currentUserArrayList = new ArrayList<>();
        }
    }


    //데이터 불러오기
    void autoLoginUserLoadData() {

        //먼저 지슨을 로드해주고
        Gson loadGson = new Gson();
        //리스트의 경우 타입을 지정해줘야한다.
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();


        // 문자열 불러오기
        String loadSharedName = "shared"; // 가져올 SharedPreferences 이름 지정
        String loadKey = "자동로그인"; // 가져올 데이터의 Key값 지정
        //여기에 json어레이가 담기도록 해보자
        String loadValue = ""; // 가져올 데이터가 담기는 변수
        String defaultValue = ""; // 가져오는것에 실패 했을 경우 기본 지정 텍스트. 보통 기본 값은 널 값으로 준다.

        SharedPreferences loadShared = getSharedPreferences(loadSharedName, MODE_PRIVATE);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        autoLoginUserArrayList = loadGson.fromJson(loadValue, type);

        if (autoLoginUserArrayList == null) {
            autoLoginUserArrayList = new ArrayList<>();
        }
    }


    //자동 저장 체크 시에 쉐어드에 저장한다.
    void autoLoginSaveData(ArrayList<User> arrayList){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "자동로그인"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = getSharedPreferences(saveSharedName,MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);

        sharedEditor.commit();
    }

    //로그인 성공하면 유저의 정보를 저장한다.
    void currentUserSaveData(ArrayList<User> arrayList){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "유저정보"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = getSharedPreferences(saveSharedName,MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);
        sharedEditor.commit();
    }

}