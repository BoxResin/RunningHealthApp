package app.boxresin.runninghealthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.boxresin.runninghealthapp.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity
{
	ActivityWelcomeBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 뷰 페이저와 인디케이터를 초기화한다.
		binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
		binding.viewPager.setAdapter(new PagerAdapter()
		{
			@Override
			public int getCount()
			{
				return 3;
			}

			@Override
			public boolean isViewFromObject(View view, Object object)
			{
				return view == object;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position)
			{
				View root = null;
				LayoutInflater inflater = getLayoutInflater();

				switch (position)
				{
				case 0:
					root = inflater.inflate(R.layout.welcome_1, null);
					break;

				case 1:
					root = inflater.inflate(R.layout.welcome_2, null);
					break;

				case 2:
					root = inflater.inflate(R.layout.welcome_3, null);
					break;
				}
				container.addView(root);
				return root;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object)
			{
				container.removeView((View) object);
			}
		});

		// 마지막 페이지에 도달하면 시작하기 버튼을 나타낸다.
		binding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				// TODO 사용자가 모든 내용을 입력하면 시작하기 버튼이 나타나도록 한다.
				if (position == 2)
					binding.btnStart.setVisibility(View.VISIBLE);
				else
					binding.btnStart.setVisibility(View.GONE);
			}
		});
		binding.pagerIndicator.setFillColor(0XFF00B29C);
		binding.pagerIndicator.setStrokeColor(0XFF00B29C);
		binding.pagerIndicator.setViewPager(binding.viewPager);
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.btn_start:
			// 메인 액티비티로 이동한다.
			startActivity(new Intent(this, MainActivity.class));
			break;
		}
	}
}