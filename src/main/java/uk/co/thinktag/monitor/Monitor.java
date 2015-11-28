package uk.co.thinktag.monitor;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Monitor {

	
	public static Properties loadProperties(String fileName) throws Exception{
		
		Properties props = new Properties();
		
		props.load(new FileInputStream(fileName));
		
		return props;
	}
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception{
		
		if(args == null || args.length <=0){
			System.out.println("Please pass the name of the properties file");
			System.exit(1);
		}
		
		String fileName = args[0];
		Properties props = loadProperties(fileName);
		
		String mirrorDir = props.getProperty("mirrorDir");
		String dirToMonitor = props.getProperty("monitorDir");
		String filePattern = props.getProperty("filePattern");
		
		List<FileListener> listeners = new ArrayList<FileListener>();
		
		listeners.add(new StatefulFileListener(mirrorDir, new MailUtil(props), props));
		
		final FileWatcher fw = new FileWatcher(dirToMonitor, filePattern, listeners);
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					fw.listen();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		t.start();
		
		try{
		
			t.join();
			
		}catch(InterruptedException ie){
			
		}
		fw.stop();
	}
}
