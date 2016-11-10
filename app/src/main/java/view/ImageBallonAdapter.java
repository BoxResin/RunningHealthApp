package view;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;

import app.boxresin.runninghealthapp.R;
import app.boxresin.runninghealthapp.databinding.ItemBalloonBinding;

/**
 * Created by eomin on 2016-11-11.
 */

public class ImageBallonAdapter implements CalloutBalloonAdapter
{
	private ItemBalloonBinding binding;

	public ImageBallonAdapter(LayoutInflater inflater)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.item_balloon, null, false);
	}

	@Override
	public View getCalloutBalloon(MapPOIItem mapPOIItem)
	{
		if (!mapPOIItem.getItemName().equals(""))
		{
			binding.txtTitle.setText(mapPOIItem.getItemName());
			binding.txtTitle.setVisibility(View.VISIBLE);
		}

		if (mapPOIItem.getCustomCalloutBalloonBitmap() != null)
			binding.img.setImageBitmap(mapPOIItem.getCustomCalloutBalloonBitmap());
		return binding.getRoot();
	}

	@Override
	public View getPressedCalloutBalloon(MapPOIItem mapPOIItem)
	{
		return null;
	}
}
