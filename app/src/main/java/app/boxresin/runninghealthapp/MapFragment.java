package app.boxresin.runninghealthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;

import app.boxresin.runninghealthapp.databinding.FragmentMapBinding;
import data.Record;
import global.Settings;
import util.DaumMapView;


/**
 * 지도 화면 프래그먼트
 */
public class MapFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener, LocationListener
{
	private FragmentMapBinding binding;

	private boolean bStarted; // 위치 기록 시작 여부
	private boolean bChase; // 현재 위치 추적 여부

	private LocationManager locationManager;

	private Location lastLocation; // 마지막 위치

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

		// 화면에 맵뷰를 추가한다.
		DaumMapView.changeParent(getContext(), binding.mapViewParent);

		// 버튼을 초기화한다.
		binding.btnLocationChase.setOnClickListener(this);
		binding.btnZoomIn.setOnClickListener(this);
		binding.btnZoomOut.setOnClickListener(this);

		return binding.getRoot();
	}

	/**
	 * 지도 프래그먼트가 사라지거나 나타날 때 호출되는 메서드
	 */
	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (!hidden)
		{
			// 환경설정대로 지도 표시 형태를 갱신한다.
			DaumMapView.get(getContext()).setMapType(Settings.get().getMapType());
		}
	}

	/**
	 * 지도 프래그먼트가 재시작(다른 액티비티로 갔다 돌아올 때)될 때 호출되는 메서드
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		// 환경설정대로 지도 표시 형태를 갱신한다.
		DaumMapView.get(getContext()).setMapType(Settings.get().getMapType());
	}

	/**
	 * 메뉴의 상태(ex. 시작·정지 상태인지)를 동기화하는 메서드
	 */
	public void syncMenuStatus(Menu menu)
	{
		if (bStarted)
			menu.findItem(R.id.action_start).setIcon(R.drawable.action_pause_white);
		else
			menu.findItem(R.id.action_start).setIcon(R.drawable.action_start_white);
	}

	/**
	 * 지도 프래그먼트에서 메뉴 아이템을 클릭했을 때 호출되는 메서드
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		if (item.getItemId() == R.id.action_start)
		{
			// 위치 기록을 시작해야 할 때
			if (!bStarted)
			{
				// 네트워크 위치제공자가 사용가능한지 확인한다.
				if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				{
					Toast.makeText(getActivity(), "현재 위치제공자를 사용할 수 없습니다.\n설정에서 '위치'를 켜주세요.", Toast.LENGTH_SHORT).show();
					return true;
				}

				// 위치 기록을 시작한다.
				//noinspection ResourceType
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);

				item.setIcon(R.drawable.action_pause_white);
				bStarted = true;
			}

			// 위치 기록을 멈춰야 할 때
			else
			{
				// 위치 기록을 중단한다.
				//noinspection ResourceType
				locationManager.removeUpdates(this);

				item.setIcon(R.drawable.action_start_white);
				bStarted = false;
			}
		}
		else
		{
			final EditText input = new EditText(getContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			input.setHint("기록 이름 입력");
			input.setLayoutParams(layoutParams);

			switch (item.getItemId())
			{
			case R.id.action_save_record: // 기록 저장 메뉴
				// 기록 저장 대화상자를 띄운다.
				new AlertDialog.Builder(getContext())
						.setTitle("기록 저장")
						.setView(input)
						.setPositiveButton("저장", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// 여기서 실제로 기록을 저장한다.
								Settings.get().getRecordAdapter().add(
										new Record(input.getText().toString(), 0, 0, 0));
								Settings.get().getRecordAdapter().notifyDataSetChanged();

								// 기록 프래그먼트로 이동한다.
								((MainActivity) getActivity()).showRecordFragment();
							}
						})
						.setNegativeButton("취소", null)
						.show();
				break;
			}
		}

		return false;
	}

	@Override
	public void onDestroy()
	{
		// 위치 기록을 중단한다.
		//noinspection ResourceType
		locationManager.removeUpdates(this);

		super.onDestroy();
	}

	/**
	 * 지도 위의 버튼을 눌렀을 때 호출되는 메서드
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_location_chase: // 위치 추적 버튼
			int imgPadding = binding.btnLocationChase.getPaddingLeft();

			// 현재 위치 추적 끄기
			if (bChase)
			{
				lastLocation = null;
				bChase = false;
				binding.btnLocationChase.setImageDrawable(getResources().getDrawable(R.drawable.action_my_location));
				binding.btnLocationChase.setBackgroundResource(R.drawable.btn_square_flat_normal);
			}

			// 현재 위치 추적 켜기
			else
			{
				bChase = true;
				binding.btnLocationChase.setImageDrawable(getResources().getDrawable(R.drawable.action_my_location_white));
				binding.btnLocationChase.setBackgroundResource(R.drawable.btn_square_theme_flat_normal);
			}

			binding.btnLocationChase.setPadding(imgPadding, imgPadding, imgPadding, imgPadding);
			break;

		case R.id.btn_zoom_in: // 지도 확대 버튼
			DaumMapView.get(getContext()).zoomIn(true);
			break;

		case R.id.btn_zoom_out: // 지도 축소 버튼
			DaumMapView.get(getContext()).zoomOut(true);
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		if (lastLocation == null)
		{
			lastLocation = location;
			return;
		}
		MapPolyline line = new MapPolyline();
		line.addPoint(MapPoint.mapPointWithGeoCoord(lastLocation.getLatitude(), lastLocation.getLongitude()));
		line.addPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()));
		line.setLineColor(Color.argb(128, 255, 51, 0));

		DaumMapView.get(getContext()).addPolyline(line);
		DaumMapView.get(getContext()).removeAllCircles();
		DaumMapView.get(getContext()).addCircle(new MapCircle(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), 2, 0xFFFF0000, 0xFFFF8000));
		DaumMapView.get(getContext()).setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);

		lastLocation = location;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	@Override
	public void onProviderEnabled(String provider)
	{

	}

	@Override
	public void onProviderDisabled(String provider)
	{

	}
}