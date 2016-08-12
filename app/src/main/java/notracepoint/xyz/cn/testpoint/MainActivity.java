package notracepoint.xyz.cn.testpoint;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import cn.xyz.notracepoint.Statistics;

@Statistics.UserProxyTouchPattern
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ll_contair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("point","ll_contair");
            }
        });
    }

    public void onClick1(View view){
        Log.i("point","onClick1");
        startActivity(new Intent(this,MainActivity.class));
    }

    public void onClick2(View view){
        Log.i("point","onClick2");
    }

    public void onClick3(View view){
        Log.i("point","onClick3");
    }

    public void onClick4(View view){
        Log.i("point","onClick4");
    }

    public void onClick5(View view){
        Log.i("point","onClick5");
    }

    public void onClick6(View view){
        Log.i("point","onClick6");
    }

    public void onClick7(View view){
        Log.i("point","onClick7");
    }

    public void onClick8(View view){
        Log.i("point","onClick8");
    }

}
