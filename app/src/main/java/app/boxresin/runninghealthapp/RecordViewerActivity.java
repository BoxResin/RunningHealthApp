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
import util.DaumMapView;
import util.LocationConverter;

/**
 * 기록을 지도 위에 보여주는 액티비티
 */
public class RecordViewerActivity extends AppCompatActivity
{
	private ActivityRecordViewerBinding binding;
	private MapPolyline poly;

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

		// 더미 위치 데이터 넣기 NOTE 나중에 지울 것
		poly = new MapPolyline();
		poly.setLineColor(Color.argb(128, 255, 51, 0));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.494514, 126.959816));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.494642, 126.960012));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.494770, 126.960012));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.494832, 126.959569));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.494904, 126.958920));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.495028, 126.958843));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.495343, 126.958882));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.495482, 126.958926));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.495699, 126.958887));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.495771, 126.958694));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.495879, 126.958178));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.496071, 126.957836));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.496118, 126.957396));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.496188, 126.957387));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.496418, 126.957323));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.496541, 126.957039));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.496822, 126.957200));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.497218, 126.957248));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.497444, 126.957210));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.497625, 126.957017));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.497665, 126.956797));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.497693, 126.956521));
		poly.addPoint(MapPoint.mapPointWithGeoCoord(37.497719, 126.956164));
		DaumMapView.get(this).addPolyline(poly);

		MapPOIItem startPoint = new MapPOIItem();
		startPoint.setMapPoint(MapPoint.mapPointWithGeoCoord(37.494514, 126.959816));
		startPoint.setItemName("출발점");
		DaumMapView.get(this).addPOIItem(startPoint);


		MapPOIItem endPoint = new MapPOIItem();
		endPoint.setMapPoint(MapPoint.mapPointWithGeoCoord(37.497719, 126.956164));
		endPoint.setItemName("도착점");
		DaumMapView.get(this).addPOIItem(endPoint);

		// 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
		DaumMapView.get(this).moveCamera(CameraUpdateFactory.newMapPointBounds(new MapPointBounds(poly.getMapPoints()), 100));

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