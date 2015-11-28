package uk.co.thinktag.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://regex101.com/
public class RegexUtil {

	public static List<String> parse(String data, String pattern) {
		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(data);

		int gc = m.groupCount();

		List<String> l = new ArrayList<String>();
		if (m.find()) {
			for (int i = 0; i < gc; i++) {
				///System.out.println(m.group(i));
				l.add(m.group(i));
			}
			return l;
		} else {
			return Collections.emptyList();
		}
	}

}
