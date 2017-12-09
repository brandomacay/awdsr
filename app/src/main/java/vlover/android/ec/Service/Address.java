package vlover.android.ec.Service;

public class Address {
    // Server user login url
    public static String URL_LOGIN = "http://vlover.heliohost.org/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://vlover.heliohost.org/register.php";

    public static String URL_GET_USER_PROFILE = "http://vlover.heliohost.org/get_user_profile.php";
    public static String URL_UPDATE_USER_PROFILE = "http://vlover.heliohost.org/update_user.php";
    private static final String ROOT_URL = "http://vlover.heliohost.org/api.php?apicall=";
    public static final String UPLOAD_URL = ROOT_URL + "uploadpic";
    public static final String GET_PICS_URL = ROOT_URL + "getpics";

}
