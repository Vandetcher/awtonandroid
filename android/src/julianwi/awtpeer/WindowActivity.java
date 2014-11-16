package julianwi.awtpeer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.SurfaceView;

public class WindowActivity extends Activity {
	
	public DataOutputStream pipeout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!new File("/data/data/julianwi.awtpeer/pipe").exists()){
			try {
				String bbpath = createPackageContext("julianwi.javainstaller", 0).getSharedPreferences("settings", 1).getString("path1", "/data/data/jackpal.androidterm/bin")+"/busybox";
				System.out.println(bbpath);
				Runtime.getRuntime().exec(bbpath+" mkfifo /data/data/julianwi.awtpeer/pipe");
				Runtime.getRuntime().exec(bbpath+" mkfifo /data/data/julianwi.awtpeer/returnpipe");
				Runtime.getRuntime().exec(bbpath+" chmod 0666 /data/data/julianwi.awtpeer/pipe");
				Runtime.getRuntime().exec(bbpath+" chmod 0666 /data/data/julianwi.awtpeer/returnpipe");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			Window.class.getMethod("takeSurface", Class.forName("android.view.SurfaceHolder$Callback2")).invoke(getWindow(), Class.forName("julianwi.awtpeer.ListenerNewApi").getConstructor(WindowActivity.class).newInstance(this));
			System.out.println("succes!");
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
			//view = new GraphicsView(this);
			SurfaceView view = new SurfaceView(this);
			view.getHolder().addCallback(new ListenerOldApi(this));
			setContentView(view);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(-1<ev.getAction()&&ev.getAction()<3){
			try {
				pipeout.write(0x02+ev.getAction());
				pipeout.writeInt((int) ev.getX());
				pipeout.writeInt((int) ev.getY());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			pipeout.write(0x01);
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			pipeout.writeInt(metrics.widthPixels);
			pipeout.writeInt(metrics.heightPixels);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}