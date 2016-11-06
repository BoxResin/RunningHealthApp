package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Locale;

import app.boxresin.runninghealthapp.databinding.FragmentRecordBinding;
import app.boxresin.runninghealthapp.databinding.ItemRecordBinding;
import data.Record;

/**
 * 기록 프래그먼트
 */
public class RecordFragment extends Fragment
{
	private FragmentRecordBinding binding;
	private ArrayAdapter<Record> adapter;

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false);

		adapter = new ArrayAdapter<Record>(getContext(), R.layout.item_record)
		{
			@NonNull
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				ItemRecordBinding binding = null;

				if (convertView == null)
				{
					binding = DataBindingUtil.inflate(inflater, R.layout.item_record, parent, false);
					convertView = binding.getRoot();
				}
				else
					binding = DataBindingUtil.getBinding(convertView);

				Record data = getItem(position);
				binding.txtRecordName.setText(data.getName());
				binding.txtDate.setText(data.getDate() + " 종료");
				binding.txtElasped.setText(String.format(Locale.KOREAN, "%.1f분 동안", data.getElapsed()));
				binding.txtMoved.setText(String.format(Locale.KOREAN, "총 %.2f ㎞ 이동", data.getMoved()));
				binding.txtConsumed.setText(String.format(Locale.KOREAN, "총 %.2f ㎉ 소모", data.getConsumed()));

				return convertView;
			}
		};
		binding.listRecord.setAdapter(adapter);

		// NOTE 더미 데이터 추가. 나중에 지울 것
		adapter.add(new Record("한강 산책", 1.625, 310, 12.6));
		adapter.add(new Record("여의도 탐험", 3.25, 598.2, 28.1));
		adapter.add(new Record("어쩌구 산책", 0.43, 94.7, 7.3));

		return binding.getRoot();
	}
}