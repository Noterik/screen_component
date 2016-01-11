package org.springfield.lou.screencomponent.util;

public class ImageUtils {
	public static String mapURL(String url, boolean useEdna){
		if(url != null){
			if (!useEdna) {
				url = url.replace("edna/", "");
			} else {
				int pos = url.indexOf("edna/");
				if 	(pos!=-1) {
					url = "http://images.euscreenxl.eu/" + url.substring(pos+5);
				}
			}
		}
		return url;
	}
}
