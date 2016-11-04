package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.boxresin.runninghealthapp.databinding.ActivitySettingBinding;

/**
 * Created by eomin on 2016-11-04.
 */

public class SettingActivity extends AppCompatActivity
{
	private ActivitySettingBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
	}
}