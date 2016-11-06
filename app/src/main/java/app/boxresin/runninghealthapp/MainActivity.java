package app.boxresin.runninghealthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import app.boxresin.runninghealthapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;
	private Toolbar toolbar;
	private NavigationView navView;

	// 사용할 프래그먼트
	private MapFragment mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		// 툴바를 초기화한다.
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// 툴바에 햄버거 메뉴를 추가하고 내비게이션 드로어와 동기화한다.
		navView = (NavigationView) findViewById(R.id.nav_view);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, toolbar, R.string.app_name, R.string.app_name);
		binding.drawer.setDrawerListener(toggle);
		toggle.syncState();

		// 프래그먼트들을 초기화한다.
		mapFragment = new MapFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_parent, mapFragment).commit();

		// 내비게이션 목록을 누르면 해당 프래그먼트 또는 액티비티로 이동하게 한다.
		navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
		{
			@Override
			public boolean onNavigationItemSelected(MenuItem item)
			{
				// 드로어를 닫는다.
				binding.drawer.closeDrawers();

				// 설정을 눌렀으면 설정 액티비티로 이동한다.
				if (item.getItemId() == R.id.nav_setting)
				{
					startActivity(new Intent(MainActivity.this, SettingActivity.class));
					return true;
				}

				// 툴바 메뉴 초기화
				toolbar.getMenu().clear();

				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.hide(mapFragment);

				switch (item.getItemId())
				{
				case R.id.nav_main: // 지도 화면을 눌렀을 때
					transaction.show(mapFragment);
					setTitle(R.string.title_map_fragment);
					toolbar.inflateMenu(R.menu.fragment_map);
					toolbar.setOnMenuItemClickListener(mapFragment);
					mapFragment.syncMenuStatus(toolbar.getMenu());
					break;

				case R.id.nav_record: // 기록을 눌렀을 때
					setTitle(R.string.title_record_fragment);
					toolbar.inflateMenu(R.menu.fragment_record);
//					toolbar.setOnMenuItemClickListener();
					break;

				case R.id.nav_graph:
					setTitle("그래프");
					toolbar.inflateMenu(R.menu.fragment_graph);
					break;
				}
				transaction.commit();
				return true;
			}
		});

		// 내비게이션 드로어에서, 지도 화면에 체크한다.
		navView.setCheckedItem(R.id.nav_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.fragment_map, menu);
		toolbar.setOnMenuItemClickListener(mapFragment);
		return true;
	}
}