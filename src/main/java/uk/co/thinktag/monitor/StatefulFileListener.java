package uk.co.thinktag.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 
 * This only works with continuously incrementing file
 *
 */
public class StatefulFileListener implements FileListener {

	private Map<String, AtomicLong> lastLineRead = new HashMap<String, AtomicLong>();

	private Map<String, File> files = new HashMap<String, File>();

	MailUtil mailUtil;
	
	String workDir;

	String ipAddress = InetAddress.getLocalHost().getHostAddress();
	
	Properties config;
	
	public StatefulFileListener(String workDir, MailUtil mu, Properties props) throws Exception {
		//load all the existing files
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(workDir))) {
			for (Path entry : stream) {
				files.put(entry.getFileName().toString(), entry.toFile());
				
				lastLineRead.put(entry.getFileName().toString(), 
						new AtomicLong(Files.lines(entry).count()));
				
			}
		}
		mailUtil = mu;
		this.workDir = workDir;
		this.config = props;
	}

	@Override
	public void listen(Path path) {

		String fileName = path.getFileName().toString();
		// System.out.println("last line read start "+lastLineRead);

		List<String> newLines = new ArrayList<String>();

		if(!lastLineRead.containsKey(fileName)){
			lastLineRead.put(fileName, new AtomicLong(0));
		}
		
		final int lastLine = lastLineRead.get(fileName).intValue();
		
		AtomicInteger counter = new AtomicInteger(1);
		try (Stream<String> stream = Files.lines(path)) {
			stream.forEachOrdered(new Consumer<String>() {
				@Override
				public void accept(String arg0) {
					if(arg0 ==null){
						return;
					}
					if (lastLine < counter.intValue()) {
						newLines.add(arg0);
					}
					counter.incrementAndGet();
				}
			});

			notifyClients( new File(workDir, fileName), newLines);

			// take care of a bug, where the counter is zero
			int nextLine = counter.intValue() - 1;
			if (nextLine > lastLineRead.get(fileName).intValue()) {
				lastLineRead.get(fileName).set(nextLine);
			}
			else{
				lastLineRead.get(fileName).set(Files.lines(path).count());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void notifyClients(File workFile, List<String> newLines) {

		if (newLines.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		int count = 0;
		try (FileOutputStream fos = new FileOutputStream(workFile, true)) {

			for (String line : newLines) {
				// sample top n
				if (++count < 10) {
					sb.append(line).append("\n".getBytes());
				}
				fos.write(line.getBytes());
				fos.write("\n".getBytes());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		mailUtil.sendMail(String.format(config.getProperty("mail.subject"), ipAddress, workFile.getName()) , sb.toString());
	}

}
