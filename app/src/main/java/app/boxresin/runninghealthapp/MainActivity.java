package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import app.boxresin.runninghealthapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		// 툴바를 초기화한다.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		// 맵뷰를 초기화한다.
		MapView mapView = new MapView(this);
		mapView.setDaumMapApiKey("d7430f85cbcf60aced1e2c584ae6361f");
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.494632, 126.959854), -2, true); // 정보대 위치로 이동
		mapView.setMapType(MapView.MapType.Hybrid); // 지도는 스카이뷰 모드로

		// 화면에 맵뷰를 추가한다.
		binding.mapViewParent.addView(mapView);
	}
}
