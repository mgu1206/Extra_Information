package yu_cse.graduation_project_edit.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.SVGMapViewListener;
import com.jiahuan.svgmapview.core.data.SVGPicture;
import com.jiahuan.svgmapview.core.helper.ImageHelper;
import com.jiahuan.svgmapview.core.helper.map.SVGBuilder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.activities.ShowMyFriend;
import yu_cse.graduation_project_edit.util.AssetsHelper;
import yu_cse.graduation_project_edit.util.PhpExcute;

/**
 * Created by gyeunguckmin on 11/9/15.
 */
public class MapViewDialog extends Dialog {

    SVGMapView mapView;
    Context context;
    static Context mContext;
    String mapName="";

    private String friendId, session;


    private GetFriendLocation getFriendLocation;

    private void setLayout()
    {
        mapView = (SVGMapView) findViewById(R.id.map_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_view_layout);

        getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;

        setLayout();

        getFriendLocation = new GetFriendLocation();

        getFriendLocation.setFriendLocationState(true);

        getFriendLocation.start();


        PhpExcute phpExcute = new PhpExcute();
        try {
            String temp = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/pushService/send_friend_req_push.php?user_id=" + friendId + "&mem_id=" + session+"&mode="+1).get();
            phpExcute.cancel(true);
        }catch(ExecutionException e)
        {
            e.printStackTrace();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public MapViewDialog(Context context, String session, String friendId) {

        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.context = context;
        this.session = session;
        this.friendId = friendId;
    }



    class GetFriendLocation extends Thread{

        private boolean friendLocationRef;
        private boolean isExistmap;


        public void setFriendLocationState(boolean state)
        {
            friendLocationRef = state;
        }


        public void run() {
            Looper.prepare();

            String temp="";
            String[] location;
            String currentMap="";


            while(friendLocationRef) {
                PhpExcute phpExcute = new PhpExcute();

                try {
                    temp = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/locationService/getfriendlocation.php?mem_id=" + friendId).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Log.d("ddd","Null");
                    e.printStackTrace();
                }
                location = temp.split(",");

                if (!location[0].equals("off") || !location[1].equals("nulll"))
                {
                    if(!currentMap.equals(location[1])) {
                        try {
                            isExistmap = loadMap(location[1]);
                        }catch(NullPointerException e)
                        {
                            e.printStackTrace();
                        }

                        currentMap = location[1];
                    }

                    if(isExistmap) {
                        mapView.getController().sparkAtPoint(new PointF(Integer.parseInt(location[2]), Integer.parseInt(location[3])), 50, Color.RED, 1);
                    }
                    else
                    {
                        setFriendLocationState(false);
                        Toast.makeText(context, "친구가 위치 찾기를 중단했어요.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(location[0].equals("off"))
                {
                    setFriendLocationState(false);
                    Toast.makeText(context, "친구가 오프라인이 되었어요.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else if(location[1].equals("nulll"))
                {
                    setFriendLocationState(false);
                    Toast.makeText(context, "친구가 위치 찾기를 중단했어요.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else
                {
                    setFriendLocationState(false);

                    dismiss();
                }

                try {
                    Thread.sleep(1000);
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }





    private boolean loadMap(String mapName)
    {
        String mapPath = "/storage/sdcard0/indoorMapData/"+mapName+".svg";
        File mapFile = new File(mapPath);

        if(mapFile.isFile()) {

            mapView.registerMapViewListener(new SVGMapViewListener() {
                @Override
                public void onMapLoadComplete() {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }

                @Override
                public void onMapLoadError() {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }

                @Override
                public void onGetCurrentMap(Bitmap bitmap) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            });
            //mapView.setBrandBitmap(ImageHelper.drawableToBitmap(new SVGBuilder().readFromString(SVGPicture.ICON_TOILET).build().getDrawable(), 1.0f));
            mapView.loadMap(AssetsHelper.getContent(context, mapPath));
            return true;
        }
        else
        {
            //Toast.makeText(context, "맵 파일을 다운받아 주세요!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(!getFriendLocation.isInterrupted()) getFriendLocation.interrupt();
        //refreshFriendLocation.interrupt();

    }
}
