package app.boxresin.runninghealthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import app.boxresin.runninghealthapp.databinding.ActivityWelcomeBinding;
import app.boxresin.runninghealthapp.databinding.Welcome3Binding;
import data.Pref;

public class WelcomeActivity extends AppCompatActivity
{
	private ActivityWelcomeBinding binding;
	private String gender;
	private int height, weight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 처음 실행이 아니면 이 액티비티를 건너뛴다.
		if (!Pref.isFirst)
		{
			startActivity(new Intent(this, MainActivity.class));
			finish();
			return;
		}

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
					final Welcome3Binding inputBinding = DataBindingUtil.inflate(inflater, R.layout.welcome_3, null, false);
					inputBinding.genderChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
					{
						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId)
						{
							checkInput(inputBinding);
						}
					});
					View.OnKeyListener onKeyListener = new View.OnKeyListener()
					{
						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event)
						{
							checkInput(inputBinding);
							return false;
						}
					};
					inputBinding.editHeight.setOnKeyListener(onKeyListener);
					inputBinding.editWeight.setOnKeyListener(onKeyListener);
					root = inputBinding.getRoot();
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

		binding.pagerIndicator.setFillColor(0XFF00B29C);
		binding.pagerIndicator.setStrokeColor(0XFF00B29C);
		binding.pagerIndicator.setViewPager(binding.viewPager);
	}

	/**
	 * 사용자가 다음 단계로 진행하기 위한 정보를 모두 입력했는지 확인하는 메서드
	 * 모든 정보를 입력했으면 시작하기 버튼을 나타내고, 그렇지 않으면 나타내지 않는다.
	 */
	private void checkInput(Welcome3Binding inputBinding)
	{
		// 모든 정보가 입력됐으면
		if (inputBinding.genderChoice.getCheckedRadioButtonId() != -1 &&
				!inputBinding.editHeight.getText().toString().equals("") &&
				!inputBinding.editWeight.getText().toString().equals(""))
		{
			if (inputBinding.genderChoice.getCheckedRadioButtonId() == R.id.btn_man)
				gender = "man";
			else
				gender = "woman";
			height = Integer.parseInt(inputBinding.editHeight.getText().toString());
			weight = Integer.parseInt(inputBinding.editWeight.getText().toString());

			binding.btnStart.setVisibility(View.VISIBLE);
		}
		else
			binding.btnStart.setVisibility(View.GONE);
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.btn_start: // 시작하기 버튼
			// 사용자가 입력한 정보를 저장한다.
			Pref.gender = gender;
			Pref.height = height;
			Pref.weight = weight;

			// 메인 액티비티로 이동한다.
			startActivity(new Intent(this, MainActivity.class));
			finish();
			Pref.isFirst = false;
			break;
		}
	}
}