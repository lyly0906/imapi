package cn.xyz.commons.utils;

public class WebappUtil {

	public static String getWebAppPath() {
		String webAppPath = System.getProperty("webAppPath");
		if (null == webAppPath || "".equals(webAppPath.trim())) {
			String path = WebappUtil.class.getResource("").getPath();
			webAppPath = path.substring(0, path.indexOf("/WEB-INF/"));
		}
		return webAppPath;
	}

	public static void main(String... strings) {
		String path = "D:/ProgramData/apache/apache-tomcat-7.0.52/wtpwebapps/mianshi-admin/WEB-INF/classes/com/shiku/mianshi/";
		int index = path.indexOf("/WEB-INF/");
		System.out.println(path.substring(0, index));
	}
}
