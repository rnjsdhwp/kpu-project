package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Sebin extends AppCompatActivity {
    //로그인 화면
    private String jsonString;
    private TextView text;
    ArrayList<test> memberArrayList;  //회원정보를 저장할 ArrayList
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sebin);
        //setContentView(R.layout.map);
        text=findViewById(R.id.text);



        final JsonParse jsonParse = new JsonParse();    //AsyncTask 생성
        jsonParse.execute("http://hostsoo.dothome.co.kr/sebin.php");   //AsycTask 실행

    }

    public class JsonParse extends AsyncTask<String, Void, String>{
        String TAG = "JsonParseTest";
        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];    //excute의 매개변수를 받아와 사용
            try{
                String selectData = "tem=TEM";

                URL serverURL = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)serverURL.openConnection();

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                Log.d(TAG, sb.toString().trim());

                return sb.toString().trim();    //받아온 JSON의 공백 제거
            }catch (Exception e){
                Log.d(TAG, "InsertData: Error",e);
                return  null;
            }
        }

        //doInBackgroundString에서 return한 값을 받음
        @Override
        protected void onPostExecute(String fromdoInBackgroundString) {
            super.onPostExecute(fromdoInBackgroundString);

            if(fromdoInBackgroundString == null){text.setText("1");}
            else{
                jsonString = fromdoInBackgroundString;
                memberArrayList = doParse();
                if(memberArrayList.size() == 0) Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                  text.setText("온도: "+test.getTemp1()+", 습도: "+test.getHumi1());
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
        private ArrayList<test> doParse(){
            ArrayList<test> tmpMemberArray = new ArrayList<test>();

            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("arduino");

                for(int i=0; i<jsonArray.length(); i++){
                    test tmpTest = new test();
                    JSONObject item = jsonArray.getJSONObject(i);

                    tmpTest.setTemp1(item.getString("tem"));
                    tmpTest.setHumi1(item.getString("hum"));


                    tmpMemberArray.add(tmpTest);

                }


            }catch (JSONException e){
                e.printStackTrace();
            }
            return tmpMemberArray;
        }
    }
}