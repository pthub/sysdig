package uk.co.thinktag.monitor;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileWatcher {

	private volatile boolean stop;
	
	private final String directory;
	
	private final String fileRegex;
	
	List<FileListener> listeners = new ArrayList<FileListener>();
	
	public FileWatcher(String directory, String fileRegex,
			List<FileListener> listeners){
		this.directory = directory;
		this.fileRegex = fileRegex;
		this.listeners.addAll(listeners);
	}
	
	public void stop(){
		stop = true;
		
	}
	/**
	 * 
	 * @param directory
	 * @param fileRegex
	 * @param listeners
	 * @throws Exception
	 */
	public void listen() throws Exception {

		System.out.println("Monitoring directory "+ directory);
		/**
		 * Get a watcher
		 */
		WatchService watcher = FileSystems.getDefault().newWatchService();

		Path dir = Paths.get(directory);

		// wait for key to be signaled
		WatchKey key = dir.register(watcher,
				StandardWatchEventKinds.ENTRY_MODIFY);

		for (;;) {

			if(stop){
				return;
			}
			
			key = watcher.take();

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				
				//System.out.println(kind);

				// This key is registered only for ENTRY_CREATE events,
				// but an OVERFLOW event can occur regardless if events are
				// lost or discarded.
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				// The filename is the context of the event.
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();

				// If the filename is "test" and the directory is "foo",
				// the resolved name is "test/foo".
				Path child = dir.resolve(filename);

				String file = child.toFile().getAbsolutePath();
				if (Pattern.matches(fileRegex, file)) {

					for (FileListener listener : listeners) {
						listener.listen(child);
						
					}
				}

			}
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}	
	
}
