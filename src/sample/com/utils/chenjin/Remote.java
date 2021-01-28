package sample.com.utils.chenjin;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import cn.hutool.http.HttpUtil;
import sample.com.main.baidu.utils.GsonUtils;
import sample.com.utils.ThreadConinfguration.ThreadConfiguration;


/**
 * 用来进行远程授权，远程获取信息
 */
public class Remote {


    public static String URL;
    private static String USER;
    private static String USER_NAME;
    private static String PASSWORD;

    /**
     * 获取配置信息
     */
    static {
//        try {
            URL = ThreadConfiguration.URL;
            USER = ThreadConfiguration.USER;
            PASSWORD = ThreadConfiguration.PASSWORD;
//             String sn = SlmRuntimeUtil.findSn();
//            if(StringUtils.isNotBlank(sn)){
//                USER_NAME =sn;
//            }else{
                USER_NAME =USER;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 获取API访问token
     */
//    public static String getAuth() {
//        return getAuth(URL);
//    }

    /**
     * 获取API访问token
     */
  /*  public static String getAuth(String url) {
        // 获取token地址
        String authHost = url + "/admin/login";
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("username", USER);
            paramMap.put("password", PASSWORD);
            paramMap.put("rememberMe", true);
            HttpRequest request = HttpUtil.createPost(authHost);
            HttpResponse response = request.form(paramMap).execute();
            String result = response.body();
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String re = jsonObject.getStr("code");
            if (re.equals("200")) {
                return jsonObject.getStr("authToken");
            }
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static String get(String url, String access_token, Map params) {
        String authHost = URL + url;
        HttpRequest request = HttpUtil.createGet(authHost);
        request.header("authToken", access_token);
        HttpResponse execute = request.form(params).execute();
        System.out.println(execute.header("authToken"));
        return execute.body();
    }

    public static JSONObject post(String url, String access_token, Map params) {
        String authHost = URL + url;
        HttpRequest request = HttpUtil.createPost(authHost);
        request.header("authToken", access_token);
        HttpResponse execute = request.form(params).execute();
        JSONObject jsonObject = JSONUtil.parseObj(execute.body());
        return jsonObject;
    }
*/
    public static boolean addOperLog(UserOperLog userOperLog) {

        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            userOperLog.setOperIp("IP地址：" + addr.getHostAddress() + "，主机名：" + addr.getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            userOperLog.setOperIp("IP地址：获取失败");
        }
        userOperLog.setCreateDate(new Date());
        String log = GsonUtils.toJson(userOperLog);
        return submit(log);
    }

    private static boolean submit(String log) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("record", log);
//        String params = "&record=" + log;

        String post = HttpUtil.post(URL + "/api/log/addLog", params);
//        JSONObject jsonObject = JSONUtil.parseObj(post);
        Map result = GsonUtils.fromJson(post, Map.class);
        if ("200".equals(result.get("code"))) {
            System.out.println(result.get("code"));
            return true;
        }
        return false;
    }

   /* public static boolean removeLog() {
        try {
            List<SignatureVo> signatureInfo = LogHelper.getSignatureInfo("");
            for (SignatureVo s : signatureInfo) {
                submit(s.getFileName());
                LogHelper.delSignatureById(s.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }*/

    public static boolean addOperLog(String operModule, String descr, String errMsg) {
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setOperModule(operModule);
        userOperLog.setDescr(descr);
        userOperLog.setUkeyCode(USER_NAME);
        userOperLog.setErrMsg(errMsg);
        return addOperLog(userOperLog);
    }

    public static boolean addOperLogSucces(String operModule, String descr) {
        return addOperLog(operModule, descr, "操作成功");
    }
/*

    */
/**
     * 从远程端获取授权文件的信息
     * @param authToken     登录
     * @param userName      授权用户
     * @param password      授权密码
     * @param UKey          是否是用UKey
     * @param info          返回的错误信息
     * @return      是否操作成功
     *//*

    public static boolean getLicenseByServer(String authToken, String userName,
                                             String password,boolean UKey,StringBuffer info) {
        PropertiesUtil p = null;
        try {
            p = new PropertiesUtil();
            String creationDate = p.getValue("creationDate");
            String encryption = p.getValue("encryption");
            Map<String, String> map = new HashMap<>(8);
            map.put("username", userName);
            map.put("password", password);
            if(UKey){
                map.put("licenseType", "UKey");
            }
            Map<String, String> maps = new HashMap<>(8);
            maps.put("record", JSONUtil.toJsonPrettyStr(map));
            Map<String, Integer> check = p.checkSlsoDays();
            JSONObject json = post("/LicenseController/getLicense", authToken, maps);
            if ("200".equals(json.getStr("code"))) {
                String str = AEC.Decrypt(json.getStr("data"));
                JSONObject obj = JSONUtil.parseObj(str);
                String id = obj.get("id") + "";
                if(UKey){
                    if (StringUtils.isNotBlank(obj.getStr("d2cFile"))) {
                        if(SlmRuntimeUtil.updateLicense(obj.getStr("d2cFile"),info)){
                            info.append("授权成功");
                        }
                    }else{
                        info.append("没有找到授权码");
                    }
                }else {
                    String date = obj.get("date") + "";
                    int day = Integer.parseInt(obj.get("day") + "");
                    String countNum = obj.get("countNum") + "";
                    int daysBetween = check.get("daysBetween");
                    if (daysBetween >= 0) {
                        p.setValue("encryption", AEC.Encrypt((daysBetween + day) + ""));
                    } else {
                        p.setValue("creationDate", AEC.Encrypt(date + ""));
                        p.setValue("encryption", AEC.Encrypt(day + ""));
                    }
                }
                map.clear();
                map.put("id", id);
                map.put("code", "200");
                JSONObject post = post("/LicenseController/licenseSave", authToken, map);
                if (!"200".equals(post.getStr("code"))) {
                    p.setValue("creationDate", creationDate);
                    p.setValue("encryption", encryption);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    */
/**
     * 修改远程服务器的授权文件
     *
     * @param authToken
     * @return
     *//*

    public static boolean setLicenseToServer(String authToken) {
        PropertiesUtil p = null;
        try {
            p = new PropertiesUtil();
            String isUpdate = p.getValue("isUpdate");
            String creationDate = p.getValue("creationDate");
            String encryption = p.getValue("encryption");
            Map<String, String> map = new HashMap<>(8);
            map.put("username", "耒阳");
            map.put("password", "123456");
            map.put("date", AEC.Decrypt(creationDate));
            map.put("day", AEC.Decrypt(encryption));
            map.put("countNum", "0");
            map.put("id", isUpdate);
            Map<String, String> maps = new HashMap<>(8);
            maps.put("record", JSONUtil.toJsonPrettyStr(map));
            JSONObject json = post("/LicenseController/addByRemote", authToken, maps);
            if ("200".equals(json.getStr("code"))) {
                p.setValue("isUpdate", AEC.Encrypt("tI/rnp8ScpZeFicTPFyHgg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
*/

    /**
     * 更新服务器上的授权文件
     *
     * @param args
     */
    public static void main(String[] args) {
//        String auth = getAuth();
//        System.out.println(auth);
        ///admin/getUserMenu
//        System.out.println(get("/admin/getUserMenu", "35fb806e-b571-4164-a792-377afe9aaff7", new HashMap()));
//        System.out.println(get("/admin/getUserMenu", auth, new HashMap()));
//        System.out.println(get("/admin/getUserMenu",auth,new HashMap()));
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setOperModule("test");
        userOperLog.setDescr("测试传输功能11111111111111");
        userOperLog.setUkeyCode(UUID.randomUUID().toString());
        userOperLog.setErrMsg("操作成功");
        Remote.addOperLog(userOperLog);
    }

}
