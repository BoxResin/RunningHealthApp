package app.boxresin.runninghealthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;

import java.util.Locale;

import app.boxresin.runninghealthapp.databinding.ActivityRecordViewerBinding;
import data.Record;
import global.DaumMapView;
import global.Settings;
import util.LocationConverter;

/**
 * 기록을 지도 위에 보여주는 액티비티
 */
public class RecordViewerActivity extends AppCompatActivity
{
	private ActivityRecordViewerBinding binding;
	private MapPolyline poly;
	private Record record;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_record_viewer);

		record = Settings.get().currentRecord;

		// 툴바를 초기화한다.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// 화면에 맵뷰를 추가한다.
		DaumMapView.changeParent(this, binding.mapViewParent);

		// 더미 위치 데이터 넣기 NOTE 나중에 지울 것
		poly = new MapPolyline();
		poly.setLineColor(Color.argb(128, 255, 51, 0));
		for (int i = 0; i < record.getPointCount(); i++)
			poly.addPoint(MapPoint.mapPointWithGeoCoord(record.getLatitudes().get(i), record.getLongitudes().get(i)));
		DaumMapView.get(this).addPolyline(poly);

		MapPOIItem startPoint = new MapPOIItem();
		startPoint.setMapPoint(MapPoint.mapPointWithGeoCoord(record.getLatitudes().get(0), record.getLongitudes().get(0)));
		startPoint.setMarkerType(MapPOIItem.MarkerType.CustomImage);
		startPoint.setCustomImageResourceId(R.drawable.custom_poi_marker_start);
		startPoint.setCustomImageAutoscale(false);
		startPoint.setCustomImageAnchor(0.5f, 1.0f);
		startPoint.setItemName("출발점");
		DaumMapView.get(this).addPOIItem(startPoint);

		MapPOIItem endPoint = new MapPOIItem();
		endPoint.setMapPoint(MapPoint.mapPointWithGeoCoord(record.getLatitudes().get(record.getPointCount() - 1),
				record.getLongitudes().get(record.getPointCount() - 1)));
		endPoint.setMarkerType(MapPOIItem.MarkerType.CustomImage);
		endPoint.setCustomImageResourceId(R.drawable.custom_poi_marker_end);
		endPoint.setCustomImageAutoscale(false);
		endPoint.setCustomImageAnchor(0.5f, 1.0f);
		endPoint.setItemName("도착점");
		DaumMapView.get(this).addPOIItem(endPoint);

		// 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
		DaumMapView.get(this).moveCamera(CameraUpdateFactory.newMapPointBounds(new MapPointBounds(poly.getMapPoints()), 100));

		binding.txtElasped.setText(String.format(Locale.KOREAN, "%.1f분 동안", record.getElapsed()));
		binding.txtMoved.setText(String.format(Locale.KOREAN, "총 %.2f ㎞ 이동", record.getMoved()));
		binding.txtConsumed.setText(String.format(Locale.KOREAN, "총 %.2f ㎉ 소모", record.getConsumed()));
		binding.txtFastest.setText(String.format(Locale.KOREAN, "%.2f ㎞/h", record.getFastest()));
		binding.txtSlowest.setText(String.format(Locale.KOREAN, "%.2f ㎞/h", record.getSlowest()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_record_viewer, menu);
		return true;
	}

	/**
	 * 툴바의 메뉴를 눌렀을 때 호출되는 메서드
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// 사용자가 뒤로가기 버튼을 누르면 액티비티를 종료한다.
		case android.R.id.home:
			onBackPressed();
			return true;

		// 지도로 불러오기 메뉴
		case R.id.action_load_to_map:
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("nav", "map");
			intent.putExtra("location_data", LocationConverter.toMyLocations(poly));
			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}