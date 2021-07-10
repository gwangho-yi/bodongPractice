package com.example.bodongpractice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Group;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.GroupInterface;
import com.example.bodongpractice.Interface.SpotInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.bodongpractice.ProfileChange.rotateBitmap;

public class AddGroup extends AppCompatActivity {


    EditText addGroupNameText;
    TextView locationText;
    ImageView locationSearchButton;
    EditText addGroupContentText;
    ImageView addImageButton;
    EditText addGroupPeopleNumber;
    Button addGroupButton;

    String locationName;

    File tempFile;

    String category;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    ArrayList<User> currentUserArrayList = new ArrayList<User>();


    String userEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");


        //유저의 이메일
        currentUserLoadData();
        userEmailText = currentUserArrayList.get(0).getEmail();

        System.out.println("들어있는 유저 이메일 : "+ userEmailText);


        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("모임 개설");

        //그룹이름
        addGroupNameText=findViewById(R.id.addGroupNameText);
        //위치 텍스트
        locationText = findViewById(R.id.locationText);
        //지도 버튼
        locationSearchButton = findViewById(R.id.locationSearchButton);
        locationSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGroup.this, AddGroupLocationSearch.class);
                startActivityForResult(intent, 101);
            }
        });

        //모임소개 내용
        addGroupContentText = findViewById(R.id.addGroupContentText);
        //이미지추가버튼
        addImageButton = findViewById(R.id.addImageButton);
        //이미지 업로드
        //추후에 완료 버튼 눌렀을때 같이 수정되도록
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("사진");

                tedPermission();

                photoDialogList();




            }
        });
        //모임인원
        addGroupPeopleNumber = findViewById(R.id.addGroupPeopleNumber);
        //모임 추가 버튼
        addGroupButton = findViewById(R.id.addGroupButton);

        addGroupButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     Upload(tempFile);
                 } catch (UnsupportedEncodingException e) {
                     e.printStackTrace();
                 }
             }
         });


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
    //앨범으로로
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
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

    //앨범에서 선택한 이미지를 파일로 만들어서 서버에 넘겨주기 위해서 만들어준다
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        try {
            tempFile = createImageFile();
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
//            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
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
                .into(addImageButton);

//        imageUpload(tempFile);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {


                //시도를 가지고 온다.
                locationName = data.getStringExtra("주소명");
                System.out.println("주소는 어디냐 : "+ locationName);
                locationText.setText(locationName);


            }
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
//                    Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
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
    private void Upload(File files) throws UnsupportedEncodingException {


        //스팟 이름 내용 주소명 위경도 사진 태그리스트
        //그룹의 이름
        String addGroupName = addGroupNameText.getText().toString();
        //그룹 내용
        String addGroupContent = addGroupContentText.getText().toString();



        //그룹인원
        String addGroupPeopleNumberText = addGroupPeopleNumber.getText().toString();





        //현재시간 설정
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = mFormat.format(mReDate);


        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
        MultipartBody.Part groupImg = MultipartBody.Part.createFormData("uploaded_file", tempFile.getName(), requestFile);




        //스팟이름
        RequestBody groupName = RequestBody.create(MediaType.parse("text/plain"),
                addGroupName);
        //스팟내용
        RequestBody groupContent = RequestBody.create(MediaType.parse("text/plain"),
                addGroupContent);

        //카테고리
        RequestBody groupCategory = RequestBody.create(MediaType.parse("text/plain"),
                category);

        //주소명
        RequestBody locationText = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(locationName));

        //현재시간
        RequestBody createTime = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(currentTime));


        //그룹인원
        //주소명
        RequestBody groupPeople = RequestBody.create(MediaType.parse("text/plain"),
                addGroupPeopleNumberText);


        // 유저의 이메일을 넣는다
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"),
                userEmailText);
        GroupInterface groupInterface =  ApiClient.getApiClient().create(GroupInterface.class);

        //사진, 이메일, 이름 , 컨텐트, 이미지 갯수, 주소명, 태그리스트, 위도, 경도
        Call<Group> resultCall = groupInterface.AddGroup(groupImg, groupName, groupContent, groupCategory, locationText, createTime, groupPeople, userEmail);
        resultCall.enqueue(new Callback<Group>() {

            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                Log.e("onSuccess", response.body().getMessage());
                System.out.println("리퀘스트 : "+response.body().getMessage());
                //업로드 완료되면 아이디를 이용해서 상세페이지로 이동한다.
                Intent intent = new Intent(AddGroup.this, GroupDetail.class);
                intent.putExtra("groupId", response.body().getMessage());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.e("onfail", t.toString());
            }
        });
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
}