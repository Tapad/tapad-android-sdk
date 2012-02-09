package com.tapad.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.tapad.tracking.Tracking;

public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Tracking.init(this, "AndroidEventSDK_testapp");

        Button custom = (Button) findViewById(R.id.custom_event);
        custom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Tracking.get().onEvent("custom1");
            }
        });
    }
}
