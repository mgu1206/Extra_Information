package yu_cse.graduation_project_edit.location;

/**
 * Created by gyeunguckmin on 11/8/15.
 */
public class BuildingListSet {

    public String buildingNumber;
    public String buildingName;

    public BuildingListSet(String bNumber, String bName)
    {
        this.buildingNumber = bNumber;
        this.buildingName = bName;
    }

    public String getBuildingNumber()
    {
        return this.buildingNumber;
    }

    public String getBuildingName()
    {
        return this.buildingName;
    }
}


