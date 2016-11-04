package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
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
		binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
		binding.viewPager.setAdapter(new PagerAdapter()
		{
			@Override
			public int getCount()
			{
				return 2;
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
		binding.test.setFillColor(0XFF00B29C);
		binding.test.setStrokeColor(0XFF00B29C);
		binding.test.setViewPager(binding.viewPager);
	}
}