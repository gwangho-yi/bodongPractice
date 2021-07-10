package com.example.bodongpractice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.SpotInterface;
import com.google.android.gms.maps.model.LatLng;
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
import java.net.URLEncoder;
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
import retrofit2.http.Multipart;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class AddSpot extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private static final String TAG = "사진";


    //이미지 파일 새로 만들어주는 버튼
    private File tempFile;
    //현재 로그인한 유저의 정보를 넘겨주기 위해 만들어준다.
    ArrayList<User> currentUserArrayList = new ArrayList<User>();



    //이미지 추가 버튼
    ImageView addImageButton;

    EditText addSpotNameText;
    EditText addSpotContentText;


    //등록 버튼
    Button AddSpotButton;

    ImageButton mapSearchButton;

    //동적뷰를 넣을 레이아웃
    LinearLayout layout;
    //이미지파일 여러장 전송할 리스트
    ArrayList<File> imgFiles = new ArrayList<File>();
    //업로드버튼
    Button uploadButton;
    //위치를 검색하고 난 다음에 가지고 오는 위경도
    LatLng latLng;
    //위치를 검색하고 난 다음에 가지고 오는 위치명
    String locationName;
    //위치를 검색하고 난 다음에 가지고 오는 위치명 ㅇ텍스트
    TextView locationText;

    //만들어진 시간을 설정하는 변수
    String currentTime;



    //태그버튼들
    Button trickTagButton;
    Button dancingTagButton;
    Button downHillTagButton;
    Button slalomTagButton;
    Button freeRidingTagButton;


    //태그 어레이를 써서 태그 안에 값이 있다면 버튼색상을 바꿔준다.
    //그리고 버튼을 눌렀을때 리스트안에 태그가 없다면 태그를 추가해준다.
    //TODO: 수정할때 참고사항 태그리스트에서 값을 가져와서 태그 리스트에 넣는다 태그가 리스트에 있는지 여부에 따라 버튼 색상을 바꿔준다.
    ArrayList<String> tagArray = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);




        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("스팟 등록");

        //스팟의 이름
        addSpotNameText = findViewById(R.id.addSpotNameText);
        //스팟 내용
        addSpotContentText = findViewById(R.id.addSpotContentText);

        layout = findViewById(R.id.layout);

        //위치를 검색하고 난 다음에 가지고 오는 위치명 ㅇ텍스트
        locationText = findViewById(R.id.locationText);

        addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("사진");
                tedPermission();
                photoDialogList();
            }
        });

        AddSpotButton=findViewById(R.id.AddSpotButton);
        AddSpotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Upload(imgFiles);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println(tagArray);
                System.out.println(latLng);
                System.out.println(locationName);

            }
        });

        mapSearchButton = findViewById(R.id.mapSearchButton);
        mapSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSpot.this, AddSpotMap.class);
                startActivityForResult(intent, 101);
            }
        });


        //태그버튼들 넣어주기
        trickTagButton = findViewById(R.id.trickTagButton);
        trickTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tagArray.contains("트릭")){
                    System.out.println("태그뻄");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag));
                    trickTagButton.setTextColor(Color.parseColor("#808080"));
                    tagArray.remove("트릭");
                }else{
                    System.out.println("태그추가됨");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag_check));
                    trickTagButton.setTextColor(Color.parseColor("#ffffff"));
                    tagArray.add("트릭");
                }

            }
        });
        dancingTagButton = findViewById(R.id.dancingTagButton);
        dancingTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagArray.contains("댄싱")){
                    System.out.println("태그뻄");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag));
                    dancingTagButton.setTextColor(Color.parseColor("#808080"));
                    tagArray.remove("댄싱");
                }else{
                    System.out.println("태그추가됨");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag_check));
                    dancingTagButton.setTextColor(Color.parseColor("#ffffff"));
                    tagArray.add("댄싱");
                }
            }
        });
        downHillTagButton = findViewById(R.id.downHillTagButton);
        downHillTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagArray.contains("다운힐")){
                    System.out.println("태그뻄");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag));
                    downHillTagButton.setTextColor(Color.parseColor("#808080"));
                    tagArray.remove("다운힐");
                }else{
                    System.out.println("태그추가됨");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag_check));
                    downHillTagButton.setTextColor(Color.parseColor("#ffffff"));
                    tagArray.add("다운힐");
                }
            }
        });
        slalomTagButton = findViewById(R.id.slalomTagButton);
        slalomTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagArray.contains("슬라럼")){
                    System.out.println("태그뻄");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag));
                    slalomTagButton.setTextColor(Color.parseColor("#808080"));
                    tagArray.remove("슬라럼");
                }else{
                    System.out.println("태그추가됨");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag_check));
                    slalomTagButton.setTextColor(Color.parseColor("#ffffff"));
                    tagArray.add("슬라럼");
                }
            }
        });
        freeRidingTagButton = findViewById(R.id.freeRidingTagButton);
        freeRidingTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagArray.contains("프리라이딩")){
                    System.out.println("태그뻄");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag));
                    freeRidingTagButton.setTextColor(Color.parseColor("#808080"));
                    tagArray.remove("프리라이딩");
                }else{
                    System.out.println("태그추가됨");
                    v.setBackground(getResources().getDrawable(R.drawable.add_tag_check));
                    freeRidingTagButton.setTextColor(Color.parseColor("#ffffff"));
                    tagArray.add("프리라이딩");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {

                latLng = data.getParcelableExtra("위경도");
                //위경도를 받아서 위치 이름 세팅
                locationName = data.getStringExtra("주소명");
                System.out.println("위경도는 뭘로 가지고 오냐"+latLng);
                System.out.println("주소는 어디냐 : "+ locationName);
                locationText.setText(locationName);


            }
        }



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
                //사진을 다중으로 가지고 온다
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        if (i < clipData.getItemCount()) {
                            Uri urione = clipData.getItemAt(i).getUri();

                            ImageView imageView = new ImageView(this);

                            ContentResolver resolver = getContentResolver();
                            try {
                                InputStream instream = resolver.openInputStream(urione);
                                Bitmap imgBitmap = BitmapFactory.decodeStream(instream);

                                instream.close();   // 스트림 닫아주기
                                saveBitmapToJpeg(imgBitmap);    // 내부 저장소에 저장
//                                Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
//                                Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
                            }

                            imgFiles.add(tempFile);

                            if(imgFiles.get(0)!=null){
                                System.out.println(imgFiles.get(i).getName());
                            }
                            //파일을 만들어주고
                            File file = imgFiles.get(i);

                            //이미지 삭제 버튼을 넣어주기 위한 프레임 레이아웃인데 이미지를 프레임에 넣고, 삭제 버튼이미지도 프레임에 넣어준다
                            FrameLayout frameLayout = new FrameLayout(this);
                            ImageView deleteView = new ImageView(this);

                            //TODO: 포문 안에 클릭메서드를 넣으면  onClick에서는 View를 파라미터로 받기 때문에 id 값을 가지고 올 수 있다
                            // 이게 위치가 진짜 중요하다 이 위치에 넣음으로써 이미지의 uri까지 같이 찾아서 삭제를 시킬 수 있다
                            if(frameLayout!=null){
                                frameLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        layout.removeView(frameLayout);
                                        //위에서 만들 file을 사용하여 데이터를 지워준다.
                                        System.out.println("지워지는 데이터 : " + file.getName());
                                        System.out.println("리스트의 데이터 : "+ imgFiles);
                                        imgFiles.remove(file);
                                    }
                                });
                            }


                            //딜리트이미지 세팅
                            deleteView.setImageResource(R.drawable.remove);

                            //삭제 이미지뷰의 크기 조절
                            LinearLayout.LayoutParams deleteImgParams = new LinearLayout.LayoutParams(70, 70);
                            deleteView.setLayoutParams(deleteImgParams);

                            deleteImgParams.setMargins(330,0,0,0);


                            //글라이드로 이미지 크기 조정
                            Glide.with(imageView)
                                    .load(urione)
                                    .override(MATCH_PARENT,MATCH_PARENT)
                                    .into(imageView);
                            //이미지뷰의 크기 조절
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);



                            params.leftMargin=20;
                            imageView.setLayoutParams(params);
//                            imageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


//                            imageView.setAdjustViewBounds(true);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                            frameLayout.setLayoutParams(params);
                            frameLayout.addView(imageView);
                            frameLayout.addView(deleteView);
                            layout.addView(frameLayout);
                            System.out.println("현재 이미지 리스트의 데이터들 : "+imgFiles);



                        }
                    }
                } else if (photoUri != null) {
//                    image1.setImageURI(uri);
                }





//                setImage();


                break;
            }
            case PICK_FROM_CAMERA: {

                Uri photoUri = Uri.fromFile(tempFile);
                ImageView imageView = new ImageView(this);

                imgFiles.add(tempFile);
//                setImage();

                //이미지 삭제 버튼을 넣어주기 위한 프레임 레이아웃인데 이미지를 프레임에 넣고, 삭제 버튼이미지도 프레임에 넣어준다
                FrameLayout frameLayout = new FrameLayout(this);
                ImageView deleteView = new ImageView(this);

                if(frameLayout!=null){
                    frameLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout.removeView(frameLayout);
                            //위에서 만들 file을 사용하여 데이터를 지워준다.
                            System.out.println("지워지는 데이터 : " + tempFile.getName());
                            System.out.println("리스트의 데이터 : "+ imgFiles);
                            imgFiles.remove(tempFile);
                        }
                    });
                }

                //딜리트이미지 세팅
                deleteView.setImageResource(R.drawable.remove);

                //삭제 이미지뷰의 크기 조절
                LinearLayout.LayoutParams deleteImgParams = new LinearLayout.LayoutParams(70, 70);
                deleteView.setLayoutParams(deleteImgParams);

                deleteImgParams.setMargins(330,0,0,0);

                //글라이드로 이미지 크기 조정
                Glide.with(imageView)
                        .load(photoUri)
                        .override(MATCH_PARENT,MATCH_PARENT)
                        .into(imageView);
                //이미지뷰의 크기 조절
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);

                params.leftMargin=20;
                imageView.setLayoutParams(params);
//                            imageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


//                            imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                frameLayout.setLayoutParams(params);
                frameLayout.addView(imageView);
                frameLayout.addView(deleteView);
                layout.addView(frameLayout);
                System.out.println("현재 이미지 리스트의 데이터들 : "+imgFiles);


                break;
            }

        }
    }


    private void Upload(ArrayList<File> files) throws UnsupportedEncodingException {


        //스팟 이름 내용 주소명 위경도 사진 태그리스트
        //스팟의 이름
        String spotNameText = addSpotNameText.getText().toString();
        //스팟 내용
        String spotContentText = addSpotContentText.getText().toString();

        //유저의 이메일
        currentUserLoadData();
        String userEmailText = currentUserArrayList.get(0).getEmail();

        //위도
        double lat = latLng.latitude;

        //경도
        double lng = latLng.longitude;

        ArrayList<MultipartBody.Part> images = new ArrayList<MultipartBody.Part>();

        int count=0;
        for (int i = 0; i < files.size(); i++) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), files.get(i));
            MultipartBody.Part body = MultipartBody.Part.createFormData( String.valueOf(count), files.get(i).getName(), requestFile);
            images.add(body);
            System.out.println("업로드 되는 파일의 이름 : "+ files.get(i).getName());
            count+=1;
        }

        ArrayList<MultipartBody.Part> tag = new ArrayList<MultipartBody.Part>();
        int tagCount=0;
        Map<String, RequestBody> map = new HashMap<>();
        for (int i = 0; i < tagArray.size(); i++){
//            RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), tagArray.get(i));
//
//            tag.add(MultipartBody.Part.createFormData("이름", "철수"));
//            System.out.println("태그리스트는 : "+tag);


            RequestBody act = RequestBody.create(MediaType.parse("text/plain"), tagArray.get(i));
            map.put("tag"+tagCount, act);
            tagCount+=1;
        }

        //현재시간 설정
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTime = mFormat.format(mReDate);





        System.out.println("업로드 되는 파일 리스트: "+ images);
        SpotInterface userInterface =  ApiClient.getApiClient().create(SpotInterface.class);
        //유저이메일
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"),
                userEmailText);
        //스팟이름
        RequestBody spotName = RequestBody.create(MediaType.parse("text/plain"),
                spotNameText);
        //스팟내용
        RequestBody spotContent = RequestBody.create(MediaType.parse("text/plain"),
                spotContentText);
        //이미지의 갯수를 카운트하기 위한 변수다
        RequestBody number = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(count));
        //태그의 갯수를 카운트하기 위한 변수
        RequestBody tagNumber = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(tagCount));
        //주소명
        RequestBody locationText = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(locationName));
        //위도
        RequestBody latText = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(lat));
        //경도
        RequestBody lngText = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(lng));
        //현재시간
        RequestBody createTime = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(currentTime));

        //사진, 이메일, 이름 , 컨텐트, 이미지 갯수, 주소명, 태그리스트, 위도, 경도
        Call<Spot> resultCall = userInterface.AddSpot(images,userEmail,spotName,spotContent,number,tagNumber,locationText,map,latText,lngText,createTime);
        resultCall.enqueue(new Callback<Spot>() {

            @Override
            public void onResponse(Call<Spot> call, Response<Spot> response) {
                Log.e("onSuccess", response.body().getMessage());
                System.out.println("리퀘스트 : "+response.body().getMessage());
                //업로드 완료되면 아이디를 이용해서 상세페이지로 이동한다.
                Intent intent = new Intent(AddSpot.this, SpotDetail.class);
                intent.putExtra("spotId", response.body().getMessage());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Spot> call, Throwable t) {
                Log.e("onfail", t.toString());
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

    //앨범으로로
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");



        startActivityForResult(intent, PICK_FROM_ALBUM);
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