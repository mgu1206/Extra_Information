package yu_cse.graduation_project_edit.list_views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.FTPClientClass;
import yu_cse.graduation_project_edit.util.PhpExcute;

/**
 * Created by gyeunguckmin on 11/2/15.
 */
public class MapListView_Adapter extends BaseAdapter implements View.OnClickListener{

    private MapList_View mMap;
    private Context mContext;

    private PhpExcute phpExcute;

    // ListView 내부 View들을 가르킬 변수들

    private boolean isExeist;

    private TextView tvMapName;
    //private TextView message;
    //public Button btnDown, btnDel;

    private Intent intent;


    // 리스트 아이템 데이터를 저장할 배열
    private ArrayList<MapList_View> mMapData;

    public MapListView_Adapter(Context context) {
        super();
        mContext = context;
        //myContext = ShowMyFriend.this;
        mMapData = new ArrayList<MapList_View>();

    }

    @Override
    /**
     * @return 아이템의 총 개수를 반환
     */
    public int getCount() {
        // TODO Auto-generated method stub
        return mMapData.size();
    }

    @Override
    /**
     * @return 선택된 아이템을 반환
     */
    public MapList_View getItem(int position) {
        // TODO Auto-generated method stub
        return mMapData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    /**
     * getView
     *
     * @param position - 현재 몇 번째로 아이템이 추가되고 있는지 정보를 갖고 있다.
     * @param convertView - 현재 사용되고 있는 어떤 레이아웃을 가지고 있는지 정보를 갖고 있다.
     * @param parent - 현재 뷰의 부모를 지칭하지만 특별히 사용되지는 않는다.
     * @return 리스트 아이템이 저장된 convertView
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int pos = position;

        View v = null;

        // 리스트 아이템이 새로 추가될 경우에는 v가 null값이다.
        // view는 어느 정도 생성된 뒤에는 재사용이 일어나기 때문에 효율을 위해서 해준다.
        if (v == null) {
            // inflater를 이용하여 사용할 레이아웃을 가져옵니다.
            v = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.map_list_view, null);
            v.setTag(position);

            // 레이아웃이 메모리에 올라왔기 때문에 이를 이용하여 포함된 뷰들을 참조할 수 있습니다.
            //imgUserIcon = (ImageView) v.findViewById(R.id.user_icon);
            tvMapName = (TextView) v.findViewById(R.id.mapName);
            //message = (TextView) v.findViewById(R.id.textViewMessage);

            //btnDown = (Button) v.findViewById(R.id.btnDownload);
            //btnDown.setFocusable(false);
            //btnDel = (Button) v.findViewById(R.id.btnDelete);
            //btnDel.setFocusable(false);
        }

        // 받아온 position 값을 이용하여 배열에서 아이템을 가져온다.
        mMap = getItem(position);
        mMap.message = (TextView)v.findViewById(R.id.textViewMessage);
        mMap.downBtn = (Button) v.findViewById(R.id.btnDownload);
        mMap.downBtn.setFocusable(false);
        mMap.downBtn.setTag(mMap);
        mMap.delBtn = (Button) v.findViewById(R.id.btnDelete);
        mMap.delBtn.setFocusable(false);
        mMap.delBtn.setTag(mMap);
        // Tag를 이용하여 데이터와 뷰를 묶습니다.
        //btnDown.setTag(mMap);
        //btnDel.setTag(mMap);

        // 데이터의 실존 여부를 판별합니다.
        if (mMap != null) {
            // 데이터가 있다면 갖고 있는 정보를 뷰에 알맞게 배치시킵니다.

            tvMapName.setText(mMap.getMapName());

            if(mMap.getIsExist())
            {
                mMap.downBtn.setVisibility(View.INVISIBLE);
                mMap.message.setVisibility(View.VISIBLE);
                mMap.delBtn.setVisibility(View.VISIBLE);
                mMap.delBtn.setOnClickListener(this);
            }
            else
            {
                mMap.downBtn.setVisibility(View.VISIBLE);
                mMap.downBtn.setOnClickListener(this);
                mMap.message.setVisibility(View.INVISIBLE);
                mMap.delBtn.setVisibility(View.INVISIBLE);
            }


        }
        // 완성된 아이템 뷰를 반환합니다.
        return v;
    }



    // 데이터를 추가하는 것을 위해서 만들어 준다.
    public void add(MapList_View map) {
        mMapData.add(map);
        notifyDataSetChanged();
    }

    class MapDownLoad extends Thread
    {
        String mapName="";
        public MapDownLoad(String map)
        {
            mapName = map;
        }
        public void run(){
            FTPClientClass ftpClientClass = new FTPClientClass();
            ftpClientClass.DownloadContents(mapName);

            this.interrupt();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // Tag를 이용하여 Data를 가져옵니다.
        MapList_View clickItem = (MapList_View) v.getTag();

        switch (v.getId()) {
            case R.id.btnDownload:

                MapDownLoad mapDownLoad = new MapDownLoad(clickItem.getMapName());
                mapDownLoad.start();



                clickItem.downBtn.setVisibility(View.INVISIBLE);
                clickItem.message.setVisibility(View.VISIBLE);
                clickItem.delBtn.setVisibility(View.VISIBLE);
                clickItem.delBtn.setOnClickListener(this);

                Toast.makeText(mContext, "지도 다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                //ftp에서 다운로드 받는 함수 수행
                break;

            case R.id.btnDelete:

                File file = new File("/storage/sdcard0/indoorMapData/"+clickItem.getMapName());
                if(file.exists()) {
                    file.delete();

                    clickItem.downBtn.setVisibility(View.VISIBLE);
                    clickItem.downBtn.setOnClickListener(this);
                    clickItem.message.setVisibility(View.INVISIBLE);
                    clickItem.delBtn.setVisibility(View.INVISIBLE);

                    Toast.makeText(mContext, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
                //기기에서 맵 삭제 함수 수행
                break;
        }

       // mMapData.in

        //mContext.
        //this.getView();
        //changeListState(clickItem.);
    }

    @Override
    public boolean isEnabled(int position)
    {

        return true;
    }

    public String getSessionInfo()
    {
        String session;
        SharedPreferences sessionInfo = mContext.getSharedPreferences("sessinInfo", 0);

        session = sessionInfo.getString("session","");

        return session;
    }



}
