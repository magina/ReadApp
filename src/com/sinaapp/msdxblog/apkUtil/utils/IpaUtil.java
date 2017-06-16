package com.sinaapp.msdxblog.apkUtil.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;
/**
 * @author zsl
 */
public class IpaUtil {
	public static Map<String,Object> readIPA(String ipaURL){
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			File file = new File(ipaURL);
            InputStream is = new FileInputStream(file);
            InputStream is2 = new FileInputStream(file);
            
            ZipInputStream zipIns = new ZipInputStream(is);
            ZipInputStream zipIns2 = new ZipInputStream(is2);
            ZipEntry ze;
            ZipEntry ze2;
            InputStream infoIs = null;
            NSDictionary rootDict = null;
            String icon = null;
            while ((ze = zipIns.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    String name = ze.getName();
                    if (null != name && name.toLowerCase().contains("info.plist")) {
                        ByteArrayOutputStream _copy = new ByteArrayOutputStream();
                        int chunk = 0;
                        byte[] data = new byte[1024];
                        while(-1!=(chunk=zipIns.read(data))){
                            _copy.write(data, 0, chunk);
                        }
                        infoIs = new ByteArrayInputStream(_copy.toByteArray());
                        rootDict = (NSDictionary) PropertyListParser.parse(infoIs);
                       
                        NSDictionary iconDict = (NSDictionary) rootDict.get("CFBundleIcons");
                        
                      //获取图标名称
                        while (null != iconDict) {
            				if(iconDict.containsKey("CFBundlePrimaryIcon")){
            					NSDictionary CFBundlePrimaryIcon = (NSDictionary) iconDict.get("CFBundlePrimaryIcon"); 
            					if(CFBundlePrimaryIcon.containsKey("CFBundleIconFiles")){
            						NSArray CFBundleIconFiles = (NSArray) CFBundlePrimaryIcon.get("CFBundleIconFiles"); 
            						icon = CFBundleIconFiles.getArray()[0].toString();
            						if(icon.contains(".png")){
            							icon = icon.replace(".png", "");
            						}
            						System.out.println("获取icon名称:" + icon);
            						break;
            					}
            				}
            			}
                        break;
                    }
                }
            }
            
            //根据图标名称下载图标文件到指定位置
            while ((ze2 = zipIns2.getNextEntry()) != null) {
                if (!ze2.isDirectory()) {
                    String name = ze2.getName();
                    if(name.contains(icon.trim())){
                    	//图片下载到python目录下，并且名称为ipa.png
                    	FileOutputStream fos = new FileOutputStream(new File("/usr/local/python/ipa.png"));
	                       int chunk = 0;
	                       byte[] data = new byte[1024];
	                       while(-1!=(chunk=zipIns2.read(data))){
	                    	   fos.write(data, 0, chunk);
	                       }
	                       fos.close();
                    	break;
                    }
                }
            }
            
           
           
            ////////////////////////////////////////////////////////////////
            //如果想要查看有哪些key ，可以把下面注释放开
//			for (String string : dictionary.allKeys()) {
//				System.out.println(string + ":" + dictionary.get(string).toString()); 
//			}
           
            // 应用包名
    		NSString parameters = (NSString) rootDict.get("CFBundleIdentifier");
    		map.put("package", parameters.toString());
    		// 应用版本名
    		parameters = (NSString) rootDict.objectForKey("CFBundleShortVersionString");
    		map.put("versionName", parameters.toString());
    		//应用版本号
    		parameters = (NSString) rootDict.get("CFBundleVersion");
    		map.put("versionCode", parameters.toString());
//    		parameters = (NSString) rootDict.get("CFBundlePrimaryIcon");
//    		map.put("icon", parameters.toString());
            /////////////////////////////////////////////////
			infoIs.close();
	        is.close();
            zipIns.close();
            
        } catch (Exception e) {
        	e.printStackTrace();
        	map.put("code", "fail");
            map.put("error","读取ipa文件失败");
        }
        return map;
	}
}
