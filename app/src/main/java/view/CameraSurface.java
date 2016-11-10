package view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback
{
	SurfaceHolder holder;
	static Camera mCamera;

	public CameraSurface(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> arSize = params.getSupportedPreviewSizes();
		if (arSize == null)
		{
			params.setPreviewSize(width, height);
		}
		else
		{
			int diff = 10000;
			Camera.Size opti = null;
			for (Camera.Size s : arSize)
			{
				if (Math.abs(s.height - height) < diff)
				{
					diff = Math.abs(s.height - height);
					opti = s;

				}
			}
			params.setPreviewSize(opti.width, opti.height);
		}
		mCamera.setParameters(params);
		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		try
		{
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(90);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		mCamera.stopPreview();
		mCamera.release();
	}
}