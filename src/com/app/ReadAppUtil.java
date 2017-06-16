package com.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sinaapp.msdxblog.apkUtil.entity.ApkInfo;
import com.sinaapp.msdxblog.apkUtil.utils.ApkUtil;
import com.sinaapp.msdxblog.apkUtil.utils.IconUtil;
import com.sinaapp.msdxblog.apkUtil.utils.IpaUtil;

/**
 * Servlet implementation class ReadAppUtil
 */
public class ReadAppUtil extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public ReadAppUtil() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		ApkInfo apkInfo = null;
		String serverPath = request.getRealPath(File.separator);
		System.out.println("============================serverPath:" + serverPath);
		
		try {
//			String apkpath = "E:/tomcat/apache-tomcat-7.0.55-windows-x64/apache-tomcat-7.0.55/webapps/ReadApp/WEB-INF/classes/shenmiaotaowang_966.apk";
			//放一个apk文件到tomcat/webapps
			String apkpath = "/usr/local/apache-tomcat-7.0.29/webapps/shenmiaotaowang_966.apk";
			apkInfo = new ApkUtil().getApkInfo(apkpath);
			
			System.out.println(apkInfo);
			//把图片解析到路径/usr/local/python/img/下面，且命名图片名称为apk.png
			IconUtil.extractFileFromApk(apkpath, apkInfo.getApplicationIcon(),"/usr/local/python/img/apk.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
//			Map<String,Object> mapIpa = IpaUtil.readIPA("E:/tomcat/apache-tomcat-7.0.55-windows-x64/apache-tomcat-7.0.55/webapps/ReadApp/WEB-INF/classes/拳皇97风云再起OL.ipa");
			//放一个ipa文件到tomcat/webapps
			Map<String,Object> mapIpa = IpaUtil.readIPA("/usr/local/apache-tomcat-7.0.29/webapps/quanhuang.ipa");
			System.out.println("======ipa==========");
			pw.println("================================ipa begin==================================");
			for (String key : mapIpa.keySet()) {
				System.out.println(key + ":" + mapIpa.get(key));
				pw.println(key + ":" + mapIpa.get(key));
			}
			
			
//			Process process= Runtime.getRuntime().exec("cmd.exe /c start E:\\python\\img\\serilizeImg.bat");
			//执行脚本文件，解析ipa文件图标 ，ipin.sh文件和ipin.py文件都放在python目录下
			Process process= Runtime.getRuntime().exec("sh /usr/local/python/ipin.sh");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
			int count = process.waitFor();
			System.out.println(count);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		pw.println("================================apk begin==================================");
		pw.println(apkInfo);
		pw.flush();
		pw.close();
	}
}
