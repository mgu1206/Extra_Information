package yu_cse.graduation_project_edit.list_views;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by gyeunguckmin on 11/6/15.
 */
public class MapList_View {

    private String mapName;
    public Button downBtn, delBtn;
    public TextView message;
    private boolean isExist;

    public MapList_View(String map, boolean exist)
    {

        mapName = map;
        isExist = exist;
    }

    public String getMapName()
    {
        return mapName;
    }

    public boolean getIsExist()
    {
        return isExist;
    }


}
