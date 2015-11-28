package uk.co.thinktag.monitor;

import java.util.List;

import org.junit.Test;

import junit.framework.Assert;
import uk.co.thinktag.monitor.RegexUtil;

public class RegexUtilTest {

	@Test
	public void testParse() {
		String regex = "(.*tuple=)(.*)(:.*\\->)(.*)(:.*)";
		
		String data = "3373736 10:26:55.669700837 3 nginx (16900) < accept fd=4(<4t>217.33.150.21:13303->178.62.213.197:https) tuple=217.33.150.21:13303->178.62.213.197:https queuepct=0 queuelen=0 queuemax=128";
				
		List<String> tokens = RegexUtil.parse(data, regex);
		
		
		Assert.assertEquals("217.33.150.21", tokens.get(2));
		Assert.assertEquals("178.62.213.197", tokens.get(4));
	}

}
