package app.boxresin.runninghealthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import app.boxresin.runninghealthapp.databinding.FragmentMapBinding;
import data.Pref;
import data.Record;
import global.DaumMapView;
import global.Settings;
import view.CameraSurface;

/**
 * 지도 화면 프래그먼트
 */
public class MapFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener, LocationListener
{
	FragmentMapBinding binding;

	private boolean bStarted; // 위치 기록 시작 여부
	private boolean bChase; // 현재 위치 추적 여부
	private boolean bCamera; // 카메라 띄우기 여부
	private boolean isPaused; // 프래그먼트가 현재 onPause된 상태인지

	Record loadedRecord;
	private Record record = new Record();

	private MapView lastMapView; // 마지막으로 사용된 맵뷰

	private MapPolyline traceLine = new MapPolyline(); // 이동 궤적
	private MapPolyline loadedLine = new MapPolyline(); // 이전 기록에서 불러온 궤적
	private MapPolyline shadowLine = new MapPolyline(); // 이전 기록 궤적 중, 어둡게 차오른 궤적

	private Location lastLocation; // 마지막으로 있었던 위치

	private LocationManager locationManager;

	private class ShadowCounter
	{
		private int index = 0;
		private int maxIndex = 0;
		private boolean isStart = false;
		private Handler handler = new Handler();
		private Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				long gap = next();
				if (gap != 0 && isStart && index <= loadedLine.getPointCount())
				{
					shadowLine.addPoint(loadedLine.getPoint(index - 1));
					DaumMapView.get(getContext()).removePolyline(shadowLine);
					DaumMapView.get(getContext()).addPolyline(shadowLine);
					binding.progressShadow.setProgress(getTotalTime());
					handler.postDelayed(task, gap);
//					handler.postDelayed(task, 100);
				}
				else if (gap == 0 && isStart)
				{
					shadowLine.addPoint(loadedLine.getPoint(index));
					DaumMapView.get(getContext()).removePolyline(shadowLine);
					DaumMapView.get(getContext()).addPolyline(shadowLine);
				}
			}
		};

		int getTotalTime()
		{
			long result = 0;
			for (int i = 0; i < index; i++)
			{
				result += Math.abs(loadedRecord.getTimes().get(i) - loadedRecord.getTimes().get(i + 1));
			}
			return (int) result / 60000;
		}

		void reset()
		{
			index = 0;
			maxIndex = 0;
			shadowLine = new MapPolyline();
			shadowLine.setLineColor(Color.argb(220, 50, 50, 50));
		}

		/**
		 * 다음 좌표까지 가는데 걸린 시간을 가져오는 메서드
		 */
		long next()
		{
			if (index < maxIndex)
			{
				long gap = Math.abs(loadedRecord.getTimes().get(index) - loadedRecord.getTimes().get(index + 1));
				index++;
				return gap;
			}
			return 0;
		}

		void start()
		{
			if (loadedLine == null || loadedRecord == null)
				return;

			maxIndex = loadedRecord.getPointCount() - 1;
			isStart = true;
			handler.post(task);
		}

		void pause()
		{
			isStart = false;
			if (index > 0)
				index--;
		}
	}
	private ShadowCounter shadowCounter = new ShadowCounter();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		isPaused = false;

		// 맵뷰가 삭제되었는지 확인한다.
		if (lastMapView != DaumMapView.get(getContext()))
		{
			// 맵뷰가 삭제되었으면 새로 만들어 화면에 추가한다.
			initMapView();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		isPaused = true;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
		initMapView();

		binding.forTouchevent.setOnTouchListener(new View.OnTouchListener()
		{
			private int _xDelta;
			private int _yDelta;

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				final int X = (int) event.getRawX();
				final int Y = (int) event.getRawY();
				switch (event.getAction() & MotionEvent.ACTION_MASK)
				{
				case MotionEvent.ACTION_DOWN:
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) binding.forTouchevent.getLayoutParams();
					_xDelta = X - lParams.leftMargin;
					_yDelta = Y - lParams.topMargin;
					return true;

				case MotionEvent.ACTION_MOVE:
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.forTouchevent.getLayoutParams();
					layoutParams.leftMargin = X - _xDelta;
					layoutParams.topMargin = Y - _yDelta;
					binding.forTouchevent.setLayoutParams(layoutParams);
					((MainActivity) getActivity()).moveCamera(layoutParams.leftMargin, layoutParams.topMargin);
					return true;
				}
				return false;
			}
		});

		// 버튼을 초기화한다.
		binding.btnLocationChase.setOnClickListener(this);
		binding.btnZoomIn.setOnClickListener(this);
		binding.btnZoomOut.setOnClickListener(this);
		binding.btnTakePhoto.setOnClickListener(this);

		return binding.getRoot();
	}

	/**
	 * 맵뷰를 초기화하는 메서드
	 */
	private void initMapView()
	{
		// 화면에 맵뷰를 추가한다.
		lastMapView = DaumMapView.get(getContext());
		DaumMapView.changeParent(getContext(), binding.mapViewParent);
		traceLine.setLineColor(Color.argb(128, 255, 51, 0));
		loadedLine.setLineColor(Color.argb(128, 120, 120, 120));
		shadowLine.setLineColor(Color.argb(220, 50, 50, 50));
		lastMapView.addPolyline(traceLine);
		lastMapView.addPolyline(loadedLine);
		lastMapView.addPolyline(shadowLine);

		// 마지막 위치가 있으면 그 위치로 이동한다.
		if (lastLocation != null)
		{
			lastMapView.addCircle(new MapCircle(MapPoint.mapPointWithGeoCoord(
					lastLocation.getLatitude(), lastLocation.getLongitude()), 2, 0xFFFF0000, 0xFFFF8000));
			lastMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lastLocation.getLatitude(), lastLocation.getLongitude()), true);
		}
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
			DaumMapView.get(getContext()).setMapType(Pref.mapType);

			// 카메라를 띄울지 말지 결정한다.
			((MainActivity) getActivity()).showCamera(bCamera);
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
		DaumMapView.get(getContext()).setMapType(Pref.mapType);
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

		menu.findItem(R.id.action_camera).setChecked(bCamera);
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
				shadowCounter.start();

				// 현재 속도, 소모 칼로리 패널을 나타낸다.
				binding.panelSpeedKcal.setVisibility(View.VISIBLE);

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
				shadowCounter.pause();

				// 현재 속도, 소모 칼로리 패널을 숨긴다.
				binding.panelSpeedKcal.setVisibility(View.GONE);

				// 위치 기록을 중단한다.
				//noinspection ResourceType
				locationManager.removeUpdates(this);

				item.setIcon(R.drawable.action_start_white);
				bStarted = false;
			}
		}

		// 기록 저장 메뉴
		else if (item.getItemId() == R.id.action_save_record)
		{
			final EditText input = new EditText(getContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			input.setSingleLine(true);
			input.setHint("기록 이름 입력");
			input.setLayoutParams(layoutParams);

			switch (item.getItemId())
			{
			case R.id.action_save_record: // 기록 저장 메뉴
				if (record.getPointCount() == 0)
				{
					Toast.makeText(getContext(), "저장할 이동궤적이 없습니다.", Toast.LENGTH_SHORT).show();
					return true;
				}

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
								record.save(input.getText().toString());

								Settings.get().getRecordAdapter().add(record);
								Settings.get().getRecordAdapter().notifyDataSetChanged();

								// 화면의 이동궤적을 지운다.
								record = new Record();
								DaumMapView.get(getContext()).removeAllPolylines();
								DaumMapView.get(getContext()).removeAllCircles();
								lastLocation = null;
								traceLine = new MapPolyline();
								loadedLine = new MapPolyline();

								// 불러온 쉐도우 궤적을 초기화한다.
								shadowCounter.reset();
								binding.progressShadow.setVisibility(View.GONE);
								loadedRecord = null;

								// 기록 프래그먼트로 이동한다.
								((MainActivity) getActivity()).showRecordFragment();
							}
						})
						.setCancelable(false)
						.setNegativeButton("취소", null)
						.show();
				return true;
			}
		}

		// 기록 초기화 메뉴
		else if (item.getItemId() == R.id.action_clear_record)
		{
			record.discard();
			record = new Record();
			DaumMapView.get(getContext()).removeAllPolylines();
			DaumMapView.get(getContext()).removeAllCircles();
			lastLocation = null;
			traceLine = new MapPolyline();
			loadedLine = new MapPolyline();
			shadowCounter.reset();
			binding.progressShadow.setVisibility(View.GONE);
		}

		// 불러온 이동궤적 삭제 메뉴
		else if (item.getItemId() == R.id.action_remove_loadrd_lines)
		{
			shadowCounter.reset();
			binding.progressShadow.setVisibility(View.GONE);
			loadedRecord = null;
			DaumMapView.get(getContext()).removePolyline(loadedLine);
			loadedLine = new MapPolyline();
			loadedLine.setLineColor(Color.argb(128, 120, 120, 120));
			DaumMapView.get(getContext()).addPolyline(loadedLine);
			DaumMapView.get(getContext()).addPolyline(shadowLine);
		}

		// 카메라 메뉴
		else if (item.getItemId() == R.id.action_camera)
		{
			// 카메라 팝업 숨기기
			if (item.isChecked())
			{
				bCamera = false;
				item.setChecked(false);
				((MainActivity) getActivity()).showCamera(false);
			}

			// 카메라 팝업 나타내기
			else
			{
				bCamera = true;
				item.setChecked(true);
				((MainActivity) getActivity()).showCamera(true);
			}
		}

		return false;
	}

	@Override
	public void onDestroyView()
	{
		DaumMapView.resetParent(getContext(), binding.mapViewParent);
		super.onDestroyView();
	}

	@Override
	public void onDestroy()
	{
		// 위치 기록을 중단한다.
		//noinspection ResourceType
		locationManager.removeUpdates(this);

		// 저장하지 않은 기록 데이터를 삭제한다.
		record.discard();

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
				bChase = false;
				binding.btnLocationChase.setImageDrawable(getResources().getDrawable(R.drawable.action_my_location));
				binding.btnLocationChase.setBackgroundResource(R.drawable.btn_square_flat_normal);
			}

			// 현재 위치 추적 켜기
			else
			{
				// 마지막 위치가 있으면 그 위치로 이동한다.
				if (lastLocation != null)
					lastMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lastLocation.getLatitude(), lastLocation.getLongitude()), true);

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

		case R.id.btn_take_photo: // 사진찍기 버튼
			if (record.getPointCount() == 0)
			{
				Toast.makeText(getContext(), "사진을 띄울 좌표가 없습니다. ▶를 눌러 이동궤적을 지도에 남긴 뒤 다시 시도하세요.", Toast.LENGTH_LONG).show();
				break;
			}

			if (lastLocation != null)
			{
				((MainActivity) getActivity()).getCameraView().takePreview(new CameraSurface.BitmapTakenListener()
				{
					@Override
					public void onBitmapCaptured(Bitmap bitmap)
					{
						MapPOIItem marker = new MapPOIItem();
						marker.setItemName("사진");
						marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
						marker.setCustomCalloutBalloonBitmap(bitmap);
						marker.setCustomImageResourceId(R.drawable.custom_marker_red);
						marker.setCustomImageAutoscale(false);
						marker.setCustomImageAnchor(0.5f, 1.0f);
						marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lastLocation.getLatitude(), lastLocation.getLongitude()));
						DaumMapView.get(getContext()).addPOIItem(marker);

						// 이미지를 저장한다.
						FileOutputStream out = null;
						try
						{
							// 랜덤 파일명 생성
							UUID id = UUID.randomUUID();

							String imgPath = Environment.getExternalStorageDirectory().getPath() + "/RunningHealth/img/" + id.toString() + ".jpg";

							// 해당 경로에 빈 파일 생성
							File imagefile = new File(imgPath);
							imagefile.getParentFile().mkdirs();
							imagefile.createNewFile();

							// 이미지를 해당 경로에 저장한다.
							out = new FileOutputStream(imgPath);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
							record.addPicture(lastLocation.getLatitude(), lastLocation.getLongitude(), imgPath);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						finally
						{
							try
							{
								if (out != null)
								{
									out.close();
								}
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				});
			}
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		lastLocation = location;

		MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
		traceLine.addPoint(mapPoint);
		DaumMapView.get(getContext()).removePolyline(traceLine);
		DaumMapView.get(getContext()).addPolyline(traceLine);

		DaumMapView.get(getContext()).removeAllCircles();
		DaumMapView.get(getContext()).addCircle(new MapCircle(mapPoint, 2, 0xFFFF0000, 0xFFFF8000));

		record.addPoint(mapPoint);
		binding.txtSpeed.setText(String.format(Locale.KOREAN, "%.2f ㎞/h", record.getCurrentSpeed()));
		binding.txtKcal.setText(String.format(Locale.KOREAN, "%.2f ㎉", record.getConsumed()));
		binding.txtTime.setText(String.format(Locale.KOREAN, "%.1f 분 경과", record.getElapsed()));

		// 현재 위치 추적 상태일 때만 지도를 현재 위치로 이동시킨다.
		if (bChase)
			DaumMapView.get(getContext()).setMapCenterPoint(mapPoint, true);
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

	/**
	 * 기록의 궤적을 지도로 불러오는 메서드
	 */
	public void setLoadedLines(MapPolyline line)
	{
		DaumMapView.get(getContext()).removeAllPolylines();
		loadedLine = line;
		loadedLine.setLineColor(Color.argb(128, 120, 120, 120));
		DaumMapView.get(getContext()).addPolyline(loadedLine);
		DaumMapView.get(getContext()).addPolyline(traceLine);

		shadowCounter.reset();

		// 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
		DaumMapView.get(getContext()).moveCamera(CameraUpdateFactory.newMapPointBounds(new MapPointBounds(loadedLine.getMapPoints()), 100));
	}
}