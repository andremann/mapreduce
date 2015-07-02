package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DimensionsReader {
	
	public static String[] ReadDimensionsFromHdfs(String filename, Configuration conf) throws IOException {
		Path pt = new Path(filename);
		FileSystem fs = FileSystem.get(conf);
		return parse(new InputStreamReader(fs.open(pt)));
	}
	
	public static String[] parse(InputStreamReader reader) throws IOException {
		String[] dimensions = new String[2];
		
		BufferedReader br = new BufferedReader(reader);
		try {
			String line;
			// first line = k
			line = br.readLine();
			dimensions[0] = line.trim();
			// second line = d
			line = br.readLine();
			dimensions[1] = line.trim();
		} finally {
			br.close();
		}
		return dimensions;
	}
	

}
