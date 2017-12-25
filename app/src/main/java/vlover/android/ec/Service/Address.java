package vlover.android.ec.Service;

public class Address {
    // Server user login url
    public static String URL_LOGIN = "https://vlover.000webhostapp.com/login.php";

    // Server user register url
    public static String URL_REGISTER = "https://vlover.000webhostapp.com/register.php";

    public static String URL_GET_USER_PROFILE = "https://vlover.000webhostapp.com/get_user_profile.php";
    public static String URL_UPDATE_USER_PROFILE = "https://vlover.000webhostapp.com/update_user.php";
    public static String URL_POST_USER = "https://vlover.000webhostapp.com/post_user.php";
    public static String URL_RESEND_EMAIL = "https://vlover.000webhostapp.com/resendemail.php";
    private static final String ROOT_URL = "https://vlover.000webhostapp.com/api.php?apicall=";
    public static final String UPLOAD_URL = ROOT_URL + "uploadpic";
    public static final String GET_PICS_URL = ROOT_URL + "getpics";

}
