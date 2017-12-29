package vlover.android.ec.Service;

public class Address {
    // Server user login url
    //https://vlover.ruvnot.com

    public static String URL_LOGIN = "http://vlover.ruvnot.com/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://vlover.ruvnot.com/register.php";
    public static String URL_GET_USER_PROFILE = "http://vlover.ruvnot.com/get_user_profile.php";
    public static String URL_UPDATE_USER_PROFILE = "http://vlover.ruvnot.com/update_user.php";
    public static String URL_POST_USER = "http://vlover.ruvnot.com/post_user.php";
    public static String URL_RESEND_EMAIL = "http://vlover.ruvnot.com/resendemail.php";
    private static final String ROOT_URL = "http://vlover.ruvnot.com/api.php?apicall=";
    private static final String ROOT_URLPOST = "http://vlover.ruvnot.com/api_post.php?apicall=";
    public static final String UPLOAD_URLPOST = ROOT_URLPOST + "uploadpic";
    public static final String UPLOAD_URL = ROOT_URL + "uploadpic";
    public static final String GET_PICS_URL = ROOT_URL + "getpics";



}
