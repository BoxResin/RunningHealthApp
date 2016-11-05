package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.boxresin.runninghealthapp.databinding.FragmentSettingBinding;

/**
 * Created by eomin on 2016-11-05.
 */

public class SettingFragment extends Fragment
{
	private FragmentSettingBinding binding;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
		return binding.getRoot();
	}
}