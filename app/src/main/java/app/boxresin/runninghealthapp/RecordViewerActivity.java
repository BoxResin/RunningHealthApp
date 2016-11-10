package app.boxresin.runninghealthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Locale;

import app.boxresin.runninghealthapp.databinding.ActivityRecordViewerBinding;
import util.DaumMapView;

/**
 * 기록을 지도 위에 보여주는 액티비티
 */
public class RecordViewerActivity extends AppCompatActivity
{
	private ActivityRecordViewerBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_record_viewer);

		// 툴바를 초기화한다.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// 화면에 맵뷰를 추가한다.
		DaumMapView.changeParent(this, binding.mapViewParent);

		// 인텐트에서 전달받은 데이터를 꺼내고 뷰에 적용한다.
		Intent intent = getIntent();
		setTitle(intent.getStringExtra("record_name"));
		double elasped = intent.getDoubleExtra("elapsed", 0);
		double moved = intent.getDoubleExtra("moved", 0);
		double consumed = intent.getDoubleExtra("consumed", 0);

		binding.txtElasped.setText(String.format(Locale.KOREAN, "%.1f분 동안", elasped));
		binding.txtMoved.setText(String.format(Locale.KOREAN, "총 %.2f ㎞ 이동", moved));
		binding.txtConsumed.setText(String.format(Locale.KOREAN, "총 %.2f ㎉ 소모", consumed));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// 사용자가 뒤로가기 버튼을 누르면 액티비티를 종료한다.
		case android.R.id.home:
			onBackPressed();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}