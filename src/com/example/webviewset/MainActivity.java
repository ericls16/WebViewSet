package com.example.webviewset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	Button btn_wv_general;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		btn_wv_general=(Button) findViewById(R.id.btn_wv_general);
	}

	private void initListener() {
		btn_wv_general.setOnClickListener(this);
	}

	private void initData() {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_wv_general:
			startActivity(new Intent(this,GeneralActivity.class));
			break;

		default:
			break;
		}
	}

}
