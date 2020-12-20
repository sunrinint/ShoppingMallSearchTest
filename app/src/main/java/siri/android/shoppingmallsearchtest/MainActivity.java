package siri.android.shoppingmallsearchtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        if (NetworkConnection() == false)
        {
            NotConnected_showAlert();
        }
        else
        {
            initView();
        }




//
    }


    private void initView() {

        textView = findViewById(R.id.editTextTextPersonName);

        button = findViewById(R.id.button);



        button.setOnClickListener(v ->{
            link = "https://search.shopping.naver.com/search/all?query=" + textView.getText().toString();
            new getLink().execute();
        } );


    }

    private class getLink extends AsyncTask<String, Void, String> //상품명 가져오기
    {

        @Override
        protected String doInBackground(String... params)
        {
            try {
                //기본 코드
                Connection.Response response = Jsoup.connect(link).method(Connection.Method.GET).execute();
                Document document = (Document) response.parse();


                //상품명 가져오기 => nameNew에 저장
                Element name = (Element) document.select("li[class=basicList_item__2XT81]").first();

                String nameNew = name.toString();
                nameNew = nameNew.substring(nameNew.indexOf("<a"), nameNew.indexOf("target=\"_blank\"")).replace("<a href=", "").replace("\"", "");
                System.out.println(nameNew);
                //nameNew = nameNew.replace("<h3>", "").replace("</h3>", "");


                return nameNew;


            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            // 결과값을 화면에 표시함.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
            intent.setPackage("com.nhn.android.search");
            startActivity(intent);


        }
    }





    private void NotConnected_showAlert() //네트워크 연결 오류 시 어플리케이션 종료
    {
        Toast.makeText(getApplicationContext(), "네트워크 연결 오류", Toast.LENGTH_LONG).show();
        finish();


    }

    private boolean NetworkConnection() { //네트워크 연결 확인하는 코드
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.getType() == networkType) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}