package app.boxresin.runninghealthapp;

import android.database.DataSetObserver;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dacer.androidcharts.LineView;

import java.util.ArrayList;

import app.boxresin.runninghealthapp.databinding.FragmentGraphBinding;
import data.Record;
import global.Settings;

/**
 * 그래프 프래그먼트
 */
public class GraphFragment extends Fragment
{
	private FragmentGraphBinding binding;
	private DataSetObserver recordObserver;

	private ArrayList<ArrayList<Integer>> allData = new ArrayList<>();
	private ArrayList<String> bottomCaptions = new ArrayList<>();
	private ArrayList<Integer> dataMoved = new ArrayList<>();
	private ArrayList<Integer> dataConsumed = new ArrayList<>();
	{
		allData.add(dataMoved);
		allData.add(dataConsumed);
	}

	private LineView lineView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_graph, container, false);
		lineView = (LineView) binding.getRoot().findViewById(R.id.line_graph_view);
		lineView.setDrawDotLine(true);
		lineView.setShowPopup(LineView.SHOW_POPUPS_All);

		syncRecordData();

		// 기록 데이터 어댑터에 옵저버를 설치하여 기록 데이터의 변화를 감시한다.
		recordObserver = new DataSetObserver()
		{
			@Override
			public void onChanged()
			{
				syncRecordData();
			}

			@Override
			public void onInvalidated()
			{
			}
		};
		Settings.get().getRecordAdapter().registerDataSetObserver(recordObserver);
		return binding.getRoot();
	}

	/**
	 * 기록 데이터와 꺾은 선 그래프를 동기화한다.
	 */
	private void syncRecordData()
	{
		ArrayAdapter<Record> adapter = Settings.get().getRecordAdapter();

		if (adapter == null)
			return;

		bottomCaptions.clear();
		dataMoved.clear();
		dataConsumed.clear();

		for (int i = 0; i < adapter.getCount(); i++)
		{
			Record record = adapter.getItem(i);
			bottomCaptions.add("_  " + record.getDate().replace("  ", "\n") + "  _");
			dataMoved.add((int) (record.getMoved() * 1000));
			dataConsumed.add((int) record.getConsumed());
		}

		lineView.setBottomTextList(bottomCaptions);
		lineView.setDataList(allData);
	}

	@Override
	public void onDestroyView()
	{
		Settings.get().getRecordAdapter().unregisterDataSetObserver(recordObserver);
		super.onDestroyView();
	}
}