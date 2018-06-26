package cn.xyz.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class FileUtil {

	public static String readAll(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String ln = null;

		while (null != (ln = reader.readLine()))
			sb.append(ln);

		return sb.toString();
	}

	public static String readAll(InputStream in, String charsetName) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetName));
		StringBuffer sb = new StringBuffer();
		String ln = null;

		while (null != (ln = reader.readLine()))
			sb.append(ln);

		return sb.toString();
	}

	public static String readAll(BufferedReader reader) {
		try {
			StringBuffer sb = new StringBuffer();
			String ln = null;

			while (null != (ln = reader.readLine()))
				sb.append(ln);

			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 删除文件(图片、视频、语音)的方法
	 * @param paths  文件路径（支持多个）
	 * @return
	 */
	public static String deleteFileToUploadDomain(String domain,String ... paths) throws Exception{
		
		try{
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					Map<String, Object> params = null;
					String url = "/upload/deleteFileServlet";
					String path = null;
					for (int i = 0; i < paths.length; i++) {
						System.out.println("删除文件  ===> "+paths[i]);
						path = paths[i];
						//-1 表示空地址，不执行删除操作
						if(path.equals("-1"))   return;
						
						params = new HashMap<String, Object>();
						url = domain+url; //拼接URl
						System.out.println(" domain ===> "+domain+" deleteDomain ===>"+url);
						params.put("paths", paths[i]);
						HttpUtil.URLPost(url, params);
					}	
					
				}
			}).start();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
}
