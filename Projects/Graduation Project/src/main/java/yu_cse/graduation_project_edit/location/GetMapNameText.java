package yu_cse.graduation_project_edit.location;

import java.util.ArrayList;

/**
 * Created by gyeunguckmin on 11/19/15.
 */
public class GetMapNameText {
    BuildingListSet buildingListSet;
    ArrayList<BuildingListSet> mapList;

    public GetMapNameText()
    {
        mapList = new ArrayList<>();
        buildingListSet = new BuildingListSet("E2101","IT관 1층");
        mapList.add(buildingListSet);
        buildingListSet = new BuildingListSet("E2102","IT관 2층");
        mapList.add(buildingListSet);
    }

    public String getMapNumber(String mapName)
    {
        String mapNumber="";

        for(int i=0; i<mapList.size(); i++)
        {
            if(mapList.get(i).getBuildingName().equals(mapName))
            {
                mapNumber = mapList.get(i).getBuildingNumber();
                break;
            }
        }
        return mapNumber;
    }


    public String getMapName(String mapNumber)
    {
        String mapName="";

        for(int i=0; i<mapList.size(); i++)
        {
            if(mapList.get(i).getBuildingNumber().equals(mapNumber))
            {
                mapName = mapList.get(i).getBuildingName();
                break;
            }
        }
        return mapName;
    }

}
