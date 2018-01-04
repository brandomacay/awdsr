package vlover.android.ec.Adapters;

import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by JUNED on 6/16/2016.
 */
public class getPostAdapter {

    Button deletep;
    int Id;
    String unique_id;
    String user_image;
    String imagen;
    String content;
    String date;


    public Button getDeletep() {
        return deletep;
    }

    public void setDeletep(Button deletep1) {
        this.deletep = deletep1;
    }
    public String getUnique_id() {

        return unique_id;
    }

    public void setUnique_id(String unique_id1) {

        this.unique_id = unique_id1;
    }

    public int getId() {

        return Id;
    }

    public void setId(int Id1) {

        this.Id = Id1;
    }


    public String getUserImage() {

        return user_image;
    }

    public void setUserImage(String uimagen1) {

        this.user_image = uimagen1;
    }


    public String getImage() {

        return imagen;
    }

    public void setImage(String imagen1) {

        this.imagen = imagen1;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content1) {

        this.content = content1;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date1) {

        this.date = date1;
    }

}
