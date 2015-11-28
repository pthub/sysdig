package uk.co.thinktag.monitor;

import java.nio.file.Path;

/**
 * 
 * @author pt
 *
 */
public interface FileListener {

	/**
	 * Listen for events on path
	 * @param path
	 */
	void listen(Path path);
	
}
