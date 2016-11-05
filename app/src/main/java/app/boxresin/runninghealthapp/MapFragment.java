package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.boxresin.runninghealthapp.databinding.FragmentMapBinding;
import util.DaumMapView;


/**
 * 지도 화면 프래그먼트
 */
public class MapFragment extends Fragment
{
	private FragmentMapBinding binding;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

		// 화면에 맵뷰를 추가한다.
		binding.mapViewParent.addView(DaumMapView.get(getActivity()));

		return binding.getRoot();
	}

	@Override
	public void onDestroyView()
	{
		binding.mapViewParent.removeView(DaumMapView.get(getActivity()));
		super.onDestroyView();
	}
}