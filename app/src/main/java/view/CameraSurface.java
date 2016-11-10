package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder holder;
	private static Camera mCamera;
	private BitmapTakenListener listener;

	public interface BitmapTakenListener
	{
		void onBitmapCaptured(Bitmap bitmap);
	}

	public void takePreview(BitmapTakenListener listener)
	{
		this.listener = listener;
	}

	public CameraSurface(Context context)
	{
		super(context);

		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

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
			mCamera.setPreviewCallback(new Camera.PreviewCallback()
			{
				@Override
				public void onPreviewFrame(byte[] data, Camera camera)
				{
					if (listener != null)
					{
						Camera.Parameters params = mCamera.getParameters();

						int w = params.getPreviewSize().width;

						int h = params.getPreviewSize().height;

						int format = params.getPreviewFormat();

						YuvImage image = new YuvImage(data, format, w, h, null);



						ByteArrayOutputStream out = new ByteArrayOutputStream();

						Rect area = new Rect(0, 0, w, h);

						image.compressToJpeg(area, 75, out);

						Bitmap captureImg = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
						Matrix matrix = new Matrix();
						matrix.postRotate(90);
						listener.onBitmapCaptured(Bitmap.createBitmap(captureImg, 0, 0, captureImg.getWidth(), captureImg.getHeight(), matrix, true));
						listener = null;
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
	}
}