package org.fit.xmltree;
import java.util.regex.*;;
public class Regex {
	public static String phone = "\\d{3}?-\\d{3}-\\d{4}|\\(\\d{3}\\)-\\d{3}-\\d{4}"; 
	public static String email = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
	
	public static void main(String args[]){
		String test ="Address Purdue UniversityDepartment of Computer Science305 N. University StreetWest Lafayette, Indiana, 47907-2107Office Phone: +1  765-494-1997FAX:          +1 (765)-494-0739&#9;";
		Pattern r = Pattern.compile(email);
		Matcher m = r.matcher(test);
		while(m.find()){
			System.out.println("Found Value: "+m.group(0)); 
		}
	}
}
