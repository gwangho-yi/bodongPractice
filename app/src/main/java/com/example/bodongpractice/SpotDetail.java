package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Adapter.ReviewAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Review;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.ReviewInterface;
import com.example.bodongpractice.Interface.SpotInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SpotDetail extends AppCompatActivity implements OnMapReadyCallback {

    Spot spotItem;

    TextView spotNameText;
    TextView spotContentText;
    TextView locationText;
    double lat;
    double lng;
    LinearLayout layout;

    GoogleMap mMap;


    Button searchRoadButton;
    TextView titleName;

    TextView optionText;

    String spotId;
    Button addReviewButton;

    TextView ratingText;
    TextView reviewCountText;

    LinearLayout noReviewLayout;

    RecyclerView reviewRecyclerView;

    ReviewAdapter reviewAdapter;

    Button allReviewButton;

    ArrayList<User> currentUserArrayList = new ArrayList<User>();

    String currentUserEmail;

    ImageView favoriteButton;

    TextView favoriteCountText;

    ImageView favoriteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);



        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.

        titleName=findViewById(R.id.titleName);


        spotNameText = findViewById(R.id.spotNameText);
        spotContentText = findViewById(R.id.spotContentText);
        locationText = findViewById(R.id.locationText);

        //별점텍스트
        ratingText = findViewById(R.id.ratingText);
        //리뷰갯수 텍스트
        reviewCountText = findViewById(R.id.reviewCountText);


        layout = findViewById(R.id.layout);


        addReviewButton = findViewById(R.id.addReviewButton);


        //리뷰가 없으면 보여줄 레이아웃이다. 이 스팟의 리뷰를 남겨주세요 라는 텍스트가 잇다.
        noReviewLayout = findViewById(R.id.noReviewLayout);


        //스팟의 아이디를 받아온다
        Intent intent = getIntent();
        spotId=intent.getStringExtra("spotId");

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);

        //즐찾 카운트 수 변경하는 텍스트뷰
        favoriteCountText= findViewById(R.id.favoriteCountText);
        favoriteImage = findViewById(R.id.favoriteImage);

        //전체리뷰를 보러 들어가는 버튼
        allReviewButton = findViewById(R.id.allReviewButton);
        allReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpotDetail.this, AllReview.class);

                intent.putExtra("spotId",spotId);
                intent.putExtra("spotName", titleName.getText().toString());
                startActivity(intent);
            }
        });

        allReviewButton.setVisibility(View.GONE);
        //리뷰 추가 버튼
        addReviewButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(SpotDetail.this, AddReview.class);
                 intent.putExtra("spotId", spotId);
                 startActivity(intent);
             }
         });

        favoriteButton=findViewById(R.id.favoriteButton);
        favoriteButton.setColorFilter(Color.parseColor("#E67A00"));
        //즐찾 버튼
        favoriteButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 currentUserLoadData();
                 currentUserEmail =  currentUserArrayList.get(0).getEmail();

                 SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);
                 Call<Spot> call = spotInterface.favoriteSpot(spotId, currentUserEmail);

                 call.enqueue(new Callback<Spot>()
                 {
                     //연결 성공 시에 싱행되는 부분
                     @Override
                     public void onResponse(@NonNull Call<Spot> call, @NonNull Response<Spot> response)
                     {

                         Log.e("onSuccess", response.body().getMessage());
//                         Log.e("onSuccess", response.body().getMessage());

                         int favoriteCount = response.body().getSpotFavoriteCount();

                         favoriteCountText.setText(Integer.toString(favoriteCount));

                         if(response.body().getMessage().contains("추천함")){
                             favoriteButton.setImageResource(R.drawable.bookmark);
                             favoriteButton.setColorFilter(Color.parseColor("#E67A00"));
                         }else{
                             favoriteButton.setImageResource(R.drawable.bookmark_white);
//                             favoriteButton.setColorFilter(null);
                         }







                     }



                     @Override
                     public void onFailure(@NonNull Call<Spot> call, @NonNull Throwable t)
                     {
                         Log.e("onfail", "에러 = " + t.getMessage());
                     }
                 });
             }
         });





        //수정 삭제 메뉴
        optionText=findViewById(R.id.optionText);
        optionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //팝업메뉴 생성
                PopupMenu popup = new PopupMenu(SpotDetail.this, optionText);
                popup.inflate(R.menu.popup);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //데이터 수정
                            case R.id.dataChange:

                                Intent intent = new Intent(SpotDetail.this, EditSpot.class);
                                //수정창에 넘어가면 원래 적어놓았던 정보들이 텍스트뷰에 그대로 있어야하기 때문에 그대로 넘겨준다
                                intent.putExtra("spotId", spotId);
                                //현재 보고 있는 이 장소가 리스트에서 몇번째에 있는지 식별하기위해 넘겨준다.

                                startActivity(intent);

                                return true;


                            //데이터 삭제
                            case R.id.dataDelete:
                                OnClickHandler();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //스팟의 아이디를 이용해서 아이템을 가지고 온다.
        SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);

        currentUserLoadData();
        currentUserEmail =  currentUserArrayList.get(0).getEmail();

        System.out.println("현재로그인한 유저 : "+currentUserEmail);

        Call<List<Spot>> call = spotInterface.SpotDetail(spotId,currentUserEmail);

        call.enqueue(new Callback<List<Spot>>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Spot>> call, @NonNull Response<List<Spot>> response)
            {

//                Log.e("onSuccess", response.body().get(0).getSpotId());
                currentUserLoadData();
                currentUserEmail =  currentUserArrayList.get(0).getEmail();
                String getUserEmail = response.body().get(0).getUserEmail();
                //현재 접속한 유저의 이메일과 아이템을 등록한 유저의 이메일이 같으면 수정 삭제 버튼이 생기도록 한다.
                if(currentUserEmail.contains(getUserEmail)){
                    System.out.println("등록한 유저와 같은 이메일입니다.");
                    optionText.setVisibility(View.VISIBLE);
                }else{
                    optionText.setVisibility(View.GONE);
                }

                onGetResult((ArrayList<Spot>) response.body());
//                System.out.println(response.body().size());
//                onGetResult(response.body());

            }



            @Override
            public void onFailure(@NonNull Call<List<Spot>> call, @NonNull Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });

    }

    public void OnClickHandler()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("스팟을 삭제하시겠습니까?");

        //삭제버튼
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
//                System.out.println("이 아이템의 번호는!!!"+number);
//                MainSpotArrayList.remove(number);
//                saveData(MainSpotArrayList);

                //스팟의 아이디를 이용해서 아이템을 가지고 온다.
                SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);

                Call<Spot> call = spotInterface.DeleteSpot(spotId);

                call.enqueue(new Callback<Spot> ()
                {
                    //연결 성공 시에 싱행되는 부분
                    @Override
                    public void onResponse(@NonNull Call<Spot>  call, @NonNull Response<Spot>  response)
                    {

                        String sueccess = response.body().getMessage();
                        Log.e("onSuccess", response.body().getMessage());

                        if(sueccess.contains("성공")){
                            System.out.println("성공");
                        }


//                System.out.println(response.body().size());
//                onGetResult(response.body());

                    }



                    @Override
                    public void onFailure(Call<Spot> call, Throwable t)
                    {
                        Log.e("onfail", "에러 = " + t.getMessage());
                    }
                });
                finish();

            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //여기서 세팅을 다 해준다.
    private void onGetResult(ArrayList<Spot> lists)
    {
        spotItem =  lists.get(0);

        lat = spotItem.getSpotLat();
        System.out.println("위도는 : "+spotItem.getSpotLat());
        lng = spotItem.getSpotLng();
        System.out.println("경도는 : "+spotItem.getSpotLng());
        ArrayList<String> spotImg = spotItem.getSpotImg();
        System.out.println("이미지의 사이즈는 : "+ spotImg.size());
        for (int i = 0; i < spotImg.size(); i++){

            ImageView imageView = new ImageView(this);

            String img =  "http://49.247.213.240/SpotImg/"+spotImg.get(i);
            //글라이드로 이미지 크기 조정
            Glide.with(imageView)
                    .load(img)
                    .override(MATCH_PARENT,MATCH_PARENT)
                    .into(imageView);
            //이미지뷰의 크기 조절
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);

            params.leftMargin=20;
            imageView.setLayoutParams(params);
//                            imageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


//                            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            layout.addView(imageView);
//            System.out.println("현재 이미지 리스트의 데이터들 : "+imgFiles);

            // 지도 세팅
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            View view = mapFragment.getView();
            view.setClickable(false);
        }




        titleName.setText(spotItem.getSpotName());
        System.out.println(spotItem.getSpotName());
        System.out.println(spotItem.getSpotContent());
        System.out.println(spotItem.getSpotLocation());
        spotNameText.setText(spotItem.getSpotName());
        spotContentText.setText(spotItem.getSpotContent());
        locationText.setText(spotItem.getSpotLocation());

        //리뷰가 하나라도 있다면
        if(spotItem.getSpotReviewCount()>0){


                ratingText.setText(String.valueOf(spotItem.getSpotRating()));

            //리뷰가 2개 이상이면 보여줘라
            if(spotItem.getSpotReviewCount()>1){
                System.out.println("2개 이상이면");
                allReviewButton.setVisibility(View.VISIBLE);
            }

            System.out.println("1개 이상이면");
            reviewCountText.setText(String.valueOf(spotItem.getSpotReviewCount()));
            noReviewLayout.setVisibility(View.GONE);
            reviewRecyclerView.setVisibility(View.VISIBLE);

            //리뷰가 있기 때문에 리사이클러뷰 세팅한다.
            getReview(spotId);
        }

        else if(spotItem.getSpotReviewCount()<1){
            //리뷰가 없다면
            noReviewLayout.setVisibility(View.VISIBLE);
            reviewRecyclerView.setVisibility(View.GONE);
            allReviewButton.setVisibility(View.GONE);
        }


        //즐찾 버튼을 내가 눌렀다면 1이 오고 아니면 0이 온다.
        int myFavoriteCount = spotItem.getMyFavoriteCount();
        if(myFavoriteCount==1){
            favoriteButton.setImageResource(R.drawable.bookmark);
            favoriteButton.setColorFilter(Color.parseColor("#E67A00"));
        }else{
            favoriteButton.setImageResource(R.drawable.bookmark_white);
//                             favoriteButton.setColorFilter(null);
        }

        //즐겨찾기 수를 표시해준다.
        favoriteCountText.setText(Integer.toString(spotItem.getSpotFavoriteCount()));


    }

    //리뷰를 가지고 온다. 리뷰는 스팟의 아이디를 사용해서 가져온다.
    private void getReview(String spotId){
        //스팟의 아이디를 이용해서 아이템을 가지고 온다.
        ReviewInterface reviewInterface =  ApiClient.getApiClient().create(ReviewInterface.class);

        Call<List<Review>> call = reviewInterface.selectReview(spotId,currentUserEmail);

        call.enqueue(new Callback<List<Review>> ()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Review>>  call, @NonNull Response<List<Review>>  response)
            {


                //리뷰 객체가 담긴 리스트로 서버에서 받아온다.
                Log.e("onSuccess", String.valueOf(response.body()));
                System.out.println(response.body().size());
                //일단 첫번재 리뷰만 넣어서 보여준다.
                ArrayList<Review> firstReview = new ArrayList<Review>();
                firstReview.add(response.body().get(0));

                //어댑터를 바로 여기에 끼운다.
                reviewAdapter = new ReviewAdapter(SpotDetail.this, firstReview);

                reviewRecyclerView.setLayoutManager(new LinearLayoutManager(SpotDetail.this, LinearLayoutManager.VERTICAL, false));
                reviewRecyclerView.setAdapter(reviewAdapter);
            }



            @Override
            public void onFailure(Call<List<Review>> call, Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        GoogleMapOptions options = new GoogleMapOptions()
                .liteMode(true);

        LatLng point = new LatLng(lat, lng);
        System.out.println("지도의 포인드는 : "+lat);
        System.out.println("지도의 포인드는 : "+lng);
        mMap.addMarker(new MarkerOptions().position(point));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));


        searchRoadButton = findViewById(R.id.searchRoadButton);

        searchRoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getCurrentAddress(point);
                //'지도보기'를 위한 intent 생성
                Uri location = Uri.parse("geo:0,0?q="+path); // Map point based on address
//                Uri location = Uri.parse("geo:"+String.valueOf(lat)+", "+String.valueOf(lng)+"?z=14"); //map point based on latitude/longitude. z param is zoom level
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                //인텐트를 수신할 앱이 있는지 확인
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                //인텐트를 처리할수있는 앱을 시작
                if (isIntentSafe) {
                    startActivity(mapIntent);
                }
            }
        });

    }

    //지도 좌표를 주소로 변환
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(SpotDetail.this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제

            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {

            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
//                Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();

            return "주소 미발견";

        } else {
            System.out.println("전체 주소값"+addresses);
//                [Address[addressLines=[0:"대한민국 서울특별시 동작구 사당동 사당로 244"],feature=２４４,admin=서울특별시,sub-admin=null,locality=null,thoroughfare=사당동,postalCode=156-094,countryCode=KR,countryName=대한민국,hasLatitude=true,latitude=37.4829742,hasLongitude=true,longitude=126.97485250000001,phone=null,url=null,extras=null]]

            //이런식으로 정의가 되어 있어서 인덱스 0번째에 있는걸 가지고 온다.
            //가지고 와서 대한민구 서울 특별시는 제거를 해준다.
            //제거를 해준 스트링 값을 리턴 시킨다.
            Address address = addresses.get(0);

//                System.out.println("전체 주소는 어떻게 받아오나 :"+addresses);
            String str = address.getAddressLine(0);
            //띄워쓰기를 제거 해서 반복문으로 구와 동만 주소로 받아온다.
            String[] array = str.split(" ");
            String addressName;
            StringBuilder sb= new StringBuilder();
            System.out.println("주소 랭스는 : "+ array.length);
            for(int i=2; i<array.length; i++){

                sb.append(array[i]+ " ");
            }
            addressName=sb.toString();
//                System.out.println("현재의 주소"+addressName);
            return addressName;
        }

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