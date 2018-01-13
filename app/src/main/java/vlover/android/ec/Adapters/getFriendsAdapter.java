package vlover.android.ec.Adapters;

import android.widget.Button;

/**
 * Created by Vlover on 12/01/2018.
 */

public class getFriendsAdapter {
    String unique_id;
    String user_image;
    String name;


    public void setUnique_id(String unique_id1) {

        this.unique_id = unique_id1;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setName(String name1) {
        this.name = name1;
    }

    public String getName() {
        return name;
    }


    public String getUserImage() {

        return user_image;
    }

    public void setUserImage(String uimagen1) {

        this.user_image = uimagen1;
    }

}
