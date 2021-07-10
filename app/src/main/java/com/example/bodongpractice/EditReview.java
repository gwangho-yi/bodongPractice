package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Review;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.ReviewInterface;
import com.example.bodongpractice.Interface.SpotInterface;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.lang.Float.parseFloat;
import static java.lang.Float.valueOf;

public class EditReview extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private static final String TAG = "사진";


    //이미지 파일 새로 만들어주는 버튼
    private File tempFile;
    //현재 로그인한 유저의 정보를 넘겨주기 위해 만들어준다.
    ArrayList<User> currentUserArrayList = new ArrayList<User>();


    //동적뷰를 넣을 레이아웃
    LinearLayout layout;
    //이미지파일 여러장 전송할 리스트
    ArrayList<File> imgFiles = new ArrayList<File>();

    //만들어진 시간을 설정하는 변수
    String currentTime;

    RatingBar ratingBar;
    //리뷰 점수 숫자 바꾸는 텍스트
    TextView reviewNumberText;
    //리뷰 숫자 바뀌면 텍스트도 바뀌는 부분
    TextView ratingChangeText;

    EditText reviewContentText;

    Button addReviewButton;

    ImageView addImageButton;

    String reviewId;

    int reviewRating;

    ArrayList<String> deleteImgList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);


        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("리뷰 수정");

        //스팟의 아이디를 받아온다
        Intent intent = getIntent();
        reviewId=intent.getStringExtra("reviewId");

        System.out.println("리뷰의 아이디는 : "+reviewId);




        ReviewInterface reviewInterface =  ApiClient.getApiClient().create(ReviewInterface.class);

        Call<Review> call = reviewInterface.editGetReview(reviewId);

        call.enqueue(new Callback<Review>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<Review> call, @NonNull Response<Review> response)
            {

//                Log.e("onSuccess", response.body().getMessage());
                Review review = response.body();

                getReview(review);
//                System.out.println(response.body().size());
//                onGetResult(response.body());

            }



            @Override
            public void onFailure(@NonNull Call<Review> call, @NonNull Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });


        ratingBar = findViewById(R.id.ratingBar);
        reviewNumberText = findViewById(R.id.reviewNumberText);

        reviewContentText = findViewById(R.id.reviewContentText);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                System.out.println("점수는 : "+rating);
                if((int) rating == 1){
                    reviewNumberText.setText((int) 1+"점 (별로예요)");
                    reviewNumberText.setTextColor(Color.parseColor("#E67A00"));
                    reviewRating= (int) rating;
                }else if((int) rating == 2){
                    reviewNumberText.setText((int) 2+"점 (그저그래요)");
                    reviewNumberText.setTextColor(Color.parseColor("#E67A00"));
                    reviewRating= (int) rating;
                }else if((int) rating == 3){
                    reviewNumberText.setText((int) 3+"점 (괜찮아요)");
                    reviewNumberText.setTextColor(Color.parseColor("#E67A00"));
                    reviewRating= (int) rating;
                }else if((int) rating == 4){
                    reviewNumberText.setText((int) 4+"점 (좋아요)");
                    reviewNumberText.setTextColor(Color.parseColor("#E67A00"));
                    reviewRating= (int) rating;
                }else if((int) rating == 5){
                    reviewNumberText.setText((int) 5+"점 (최고예요)");
                    reviewNumberText.setTextColor(Color.parseColor("#E67A00"));
                    reviewRating= (int) rating;
                }
            }
        });

        addReviewButton = findViewById(R.id.addReviewButton);
        //리뷰 작성 버튼
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Upload(imgFiles);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        layout = findViewById(R.id.layout);


        addImageButton = findViewById(R.id.addImageButton);
        //이미지 추가버튼
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("사진");
                tedPermission();
                photoDialogList();

            }
        });

    }

    //리뷰 가지고 와서 수정할 창에 뿌려주는 메서드
    void getReview(Review re) {
        reviewRating = Integer.parseInt(re.getReviewRating());
        ratingBar.setRating(parseFloat(re.getReviewRating()));
        reviewContentText.setText(re.getReviewContent());



        ArrayList<String> spotImg = re.getReviewImg();


        for (int i = 0; i < spotImg.size(); i++) {

            ImageView imageView = new ImageView(this);

            //이미지 삭제 버튼을 넣어주기 위한 프레임 레이아웃인데 이미지를 프레임에 넣고, 삭제 버튼이미지도 프레임에 넣어준다
            FrameLayout frameLayout = new FrameLayout(this);
            String deleteimg = spotImg.get(i);

            ImageView deleteView = new ImageView(this);
            //이미지를 삭제를 하면 삭제를 한 리스트를 만들어줘서 서버에 그대로 날려준다.
            if (frameLayout != null) {
                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout.removeView(frameLayout);
//                        System.out.println("삭제된 이미지 : "+ deleteImg.get(j));
                        deleteImgList.add(deleteimg);

                    }
                });
            }

            //딜리트이미지 세팅
            deleteView.setImageResource(R.drawable.remove);

            //삭제 이미지뷰의 크기 조절
            LinearLayout.LayoutParams deleteImgParams = new LinearLayout.LayoutParams(70, 70);
            deleteView.setLayoutParams(deleteImgParams);

            deleteImgParams.setMargins(330, 0, 0, 0);

            String img = "http://49.247.213.240/ReviewImg/" + spotImg.get(i);
            //글라이드로 이미지 크기 조정
            Glide.with(imageView)
                    .load(img)
                    .override(MATCH_PARENT, MATCH_PARENT)
                    .into(imageView);
            //이미지뷰의 크기 조절
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);

            params.leftMargin = 20;
            imageView.setLayoutParams(params);
//                            imageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


//                            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            frameLayout.setLayoutParams(params);
            frameLayout.addView(imageView);
            frameLayout.addView(deleteView);
            layout.addView(frameLayout);
        }
    }

    private void Upload(ArrayList<File> files) throws UnsupportedEncodingException {


        //스팟 이름 내용 주소명 위경도 사진 태그리스트

        //스팟 내용
        String reviewContent = reviewContentText.getText().toString();





        ArrayList<MultipartBody.Part> images = new ArrayList<MultipartBody.Part>();

        int count=0;
        //이미지 업로드
        for (int i = 0; i < files.size(); i++) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), files.get(i));
            MultipartBody.Part body = MultipartBody.Part.createFormData( String.valueOf(count), files.get(i).getName(), requestFile);
            images.add(body);
            System.out.println("업로드 되는 파일의 이름 : "+ files.get(i).getName());
            count+=1;
        }

        //삭제한 이미지
        int deleteImgCount = 0;
        Map<String, RequestBody> deleteImgMap = new HashMap<>();
        if(deleteImgList.size()!=0){
            for (int i = 0; i < deleteImgList.size(); i++){
                RequestBody act = RequestBody.create(MediaType.parse("text/plain"), deleteImgList.get(i));
                deleteImgMap.put("delete_img"+deleteImgCount, act);
                deleteImgCount+=1;
                System.out.println("이미지 카운트 : "+deleteImgCount);
            }
        }else{

        }



        //현재시간 설정
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTime = mFormat.format(mReDate);






        System.out.println("업로드 되는 파일 리스트: "+ images);
        ReviewInterface userInterface =  ApiClient.getApiClient().create(ReviewInterface.class);



        //리뷰아이디
        RequestBody reviewid = RequestBody.create(MediaType.parse("text/plain"),
                reviewId);

        //리뷰내용
        RequestBody content = RequestBody.create(MediaType.parse("text/plain"),
                reviewContent);
        //이미지의 갯수를 카운트하기 위한 변수다
        RequestBody number = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(count));

        //현재시간
        RequestBody createTime = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(currentTime));


        //별점
        RequestBody rating = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(reviewRating));

        //삭제해줄 이미지 카운트
        RequestBody deleImgNumber = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(deleteImgCount));

        //사진, 이메일, 이름 , 컨텐트, 이미지 갯수, 주소명, 태그리스트, 위도, 경도
        Call<Review> resultCall = userInterface.editReview(images,reviewid,rating,content,number,createTime,deleteImgMap,deleImgNumber);
        resultCall.enqueue(new Callback<Review>() {

            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                Log.e("onSuccess", response.body().getMessage());
                System.out.println("리퀘스트 : "+response.body().getMessage());
                //업로드 완료되면 아이디를 이용해서 상세페이지로 이동한다.


                finish();
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
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
    //앨범으로로
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");



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
}