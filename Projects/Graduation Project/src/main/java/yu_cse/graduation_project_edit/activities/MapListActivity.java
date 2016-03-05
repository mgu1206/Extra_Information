package yu_cse.graduation_project_edit.activities;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.list_views.MapListView_Adapter;
import yu_cse.graduation_project_edit.list_views.MapList_View;
import yu_cse.graduation_project_edit.util.FTPClientClass;

public class MapListActivity extends Activity {


    private ListView mapListView;
    private FTPClientClass ftpClientClass;
    private FTPFile[] ftpMapList;

    private MapListView_Adapter adapter;

    private String[] localMapList;

    private GetMapList getMapList;

    Handler handler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        adapter = new MapListView_Adapter(MapListActivity.this);

        mapListView = (ListView) findViewById(R.id.map_List_View);
        localMapList = getTitleList();

        setMapList();

    }


    private static class MyHandler extends Handler {
        /**
         * 외부 쓰레드에서 액티비티UI변경을 위한 핸들러
         */
        private final WeakReference<MapListActivity> mActivity;

        public MyHandler(MapListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MapListActivity activity = mActivity.get();

            if (activity != null) {
                activity.adapter.add((MapList_View)msg.obj);
            }
        }
    }

    class GetMapList extends Thread
    {
        boolean isExist=false;


        public void run()
        {
            ftpClientClass = new FTPClientClass();
            int mapSize = 0;

            mapSize = ftpClientClass.list().length;
            ftpMapList = new FTPFile[mapSize];

            for(int i=0; i<mapSize; i++)
            {
                ftpMapList[i] = new FTPFile();
            }


            ftpMapList = ftpClientClass.list();

            if(ftpMapList.length!=0) {
                try {
                    for (int i = 0; i < ftpMapList.length; i++) {

                        for (int j = 0; j < localMapList.length; j++) {
                            if (ftpMapList[i].getName().equals(localMapList[j])) {
                                isExist = true;
                                break;
                            } else isExist = false;
                        }
                        Message msg = Message.obtain();
                        MapList_View mv = new MapList_View(ftpMapList[i].getName(), isExist);
                        msg.obj = mv;
                        handler.sendMessage(msg);
                        // adapter.add(mv);
                    }
                }catch(NullPointerException e)
                {
                    e.printStackTrace();
                }
            }


            this.interrupt();


        }
    }


    private String[] getTitleList()
    {
        try
        {
            String path = "/storage/sdcard0/indoorMapData/";
            File file = new File(path); //경로를 SD카드로 잡은거고 그 안에 있는 A폴더 입니다. 입맛에 따라 바꾸세요.
            if(!file.exists())
            {
                file.mkdirs();
            }
            File[] files = file.listFiles();//위에 만들어 두신 필터를 넣으세요. 만약 필요치 않으시면 fileFilter를 지우세요.
            String [] titleList = new String [files.length]; //파일이 있는 만큼 어레이 생성했구요
            for(int i = 0;i < files.length;i++)
            {
                titleList[i] = files[i].getName();	//루프로 돌면서 어레이에 하나씩 집어 넣습니다.
            }//end for
            return titleList;
        } catch( Exception e )
        {
            return null;
        }//end catch()
    }//end getTitleList


    public void setMapList()
    {

        getMapList = new GetMapList();
        getMapList.start();
        mapListView.setAdapter(adapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //setMapList();

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
