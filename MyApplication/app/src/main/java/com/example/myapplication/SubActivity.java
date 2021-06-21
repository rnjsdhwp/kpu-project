package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.pointAdapter;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private static NaverMap naverMap;
    private Button up;
    private Button down;
    private int i = 0;
    private int j = 0;
    int k = 0;
    private InfoWindow infoWindow3 = new InfoWindow();

    private Marker marker1 = new Marker();
    private String jsonString;
    ArrayList<test> memberArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naver_map);


        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);
        mapView = (MapView) findViewById(R.id.map_view);
        Button mark1 = (Button) findViewById(R.id.mark1);
        Button mark2 = (Button) findViewById(R.id.mark2);

        mark2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                createNotification();
            }
        });

        // TextView stream=(TextView)findViewById(R.id.stream);

   /*     Linkify.TransformFilter oje=new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };

        Pattern pattern=Pattern.compile("스트리밍");
        Linkify.addLinks(stream,pattern,"http://192.168.137.169:8000/index.html",null,oje);
*/

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("rdhfnllit2"));
        mapView.getMapAsync(this);

        mark1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (j == 0) {
                    setMarker(marker1, 37.340490, 126.733443, R.drawable.ic_place_black_24dp, 0);
                    j = 1;
                } else if (j == 1) {
                    delMaker(marker1);
                    j = 0;

                }
                marker1.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        if (k == 0) {
                            ViewGroup rootView = (ViewGroup) findViewById(R.id.fragment_container);
                            pointAdapter adapter = new pointAdapter(SubActivity.this, rootView);

                            //infoWindow3.setAdapter(adapter);
                            infoWindow3.setAdapter(new InfoWindow.DefaultViewAdapter(getApplication()) {
                                @NonNull
                                @Override
                                protected View getContentView(@NonNull InfoWindow infoWindow) {
                                    Marker marker = infoWindow.getMarker();
                                    //PlaceInfo info = (PlaceInfo) marker.getTag();
                                    View view = View.inflate(SubActivity.this, R.layout.item_point, null);
                                    TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);
                                    TextView txtAddr = (TextView) view.findViewById(R.id.txtaddr);
                                    TextView txtTel = (TextView) view.findViewById(R.id.txttel);

                                    String a = "http://192.168.137.169:8000/index.html";
                                    txtTitle.setText("한국산업기술대학교");
                                    txtAddr.setText("블랙아이스 위험 도로입니다.");
                                    txtTel.setText("온도: " + test.getTemp1() + ",  습도: " + test.getHumi1() + "  날씨: " + test.getWeather());


                                    return view;
                                }
                            });

                         /*   infoWindow3.setAdapter(new InfoWindow.DefaultTextAdapter(getApplication())
                            {
                                @NonNull
                                @Override
                                public CharSequence getText(@NonNull InfoWindow infoWindow)
                                {
                                    //String a="대학교\n"
                                    return "한국산업기술대학교\n온도: "+test.getTemp()+", 습도: "+test.getHum();
                        }
                    }); */

                            //인포창의 우선순위
                            infoWindow3.setZIndex(10);
                            //투명도 조정
                            infoWindow3.setAlpha(0.9f);
                            //인포창 표시
                            infoWindow3.open(marker1);
                            k = 1;
                            Toast.makeText(getApplication(), "정보창 켜기", Toast.LENGTH_SHORT).show();
                        } else if (k == 1) {

                            infoWindow3.close();
                            k = 0;
                            Toast.makeText(getApplication(), "정보창 끄기", Toast.LENGTH_SHORT).show();

                        }


                        return false;
                    }
                });
                final JsonParse jsonParse = new JsonParse();    //AsyncTask 생성
                jsonParse.execute("http://hostsoo.dothome.co.kr/sebin.php");
            }
        });

        //스트리밍
       /* mark2.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Oje.class);
                startActivity(intent);
            }
            });*/

    }

    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("경고");
        builder.setContentText("전방에 블랙아이스 도로입니다.");

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }

    private void setMarker(Marker marker, double lat, double lng, int resourceID, int zIndex) {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);
        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));
        //마커의 투명도
        marker.setAlpha(0.8f);
        //마커 위치
        marker.setPosition(new LatLng(lat, lng));
        //마커 우선순위
        marker.setZIndex(zIndex);
        //마커 표시

        marker.setWidth(150);
        marker.setHeight(150);
        marker.setMap(naverMap);
    }

    private void delMaker(Marker marker) {

        marker.setMap(null);
    }

    public void onMapReady(@NonNull final NaverMap naverMap) {
        this.naverMap = naverMap;

        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(37.340490, 126.733443),  // 위치 지정
                15, 0, 0// 줌 레벨
        );
        naverMap.setCameraPosition(cameraPosition);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i += 10;
                CameraPosition cameraPosition = new CameraPosition(
                        new LatLng(37.340490, 126.733443),  // 위치 지정
                        15,
                        0 + i, 0
                );

                naverMap.setCameraPosition(cameraPosition);
            }

        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i -= 10;
                CameraPosition cameraPosition = new CameraPosition(
                        new LatLng(37.340490, 126.733443),  // 위치 지정
                        15,
                        0 + i, 0
                );

                naverMap.setCameraPosition(cameraPosition);
            }
        });
    }


    public class JsonParse extends AsyncTask<String, Void, String> {
        String TAG = "JsonParseTest";

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];    //excute의 매개변수를 받아와 사용
            try {
                String selectData = "tem=TEM";

                URL serverURL = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) serverURL.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                //어플에서 데이터 전송
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(selectData.getBytes("UTF-8"));
                //et_password.setText(selectData);
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                //연결 상태 확인
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                Log.d(TAG, sb.toString().trim());

                return sb.toString().trim();    //받아온 JSON의 공백 제거
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error", e);
                return null;
            }
        }

        //doInBackgroundString에서 return한 값을 받음
        @Override
        protected void onPostExecute(String fromdoInBackgroundString) {
            super.onPostExecute(fromdoInBackgroundString);

            if (fromdoInBackgroundString == null) {//text.setText("1");
            } else {
                jsonString = fromdoInBackgroundString;
                memberArrayList = doParse();
                if (memberArrayList.size() == 0) {
                } else {

                    //text.setText("온도: "+test.getTemp()+", 습도: "+test.getHum());
                }
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        //JSON을 가공하여 ArrayList에 넣음
        private ArrayList<test> doParse() {
            ArrayList<test> tmpMemberArray = new ArrayList<test>();

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("arduino");

                for (int i = 0; i < jsonArray.length(); i++) {
                    test tmpTest = new test();
                    JSONObject item = jsonArray.getJSONObject(i);

                    tmpTest.setTemp1(item.getString("tem"));
                    tmpTest.setHumi1(item.getString("hum"));
                    tmpTest.setWeather(item.getString("weather"));


                    tmpMemberArray.add(tmpTest);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tmpMemberArray;
        }
    }
}