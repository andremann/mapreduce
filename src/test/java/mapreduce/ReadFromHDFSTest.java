package mapreduce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import utils.GaussianParams;

public class ReadFromHDFSTest {
	
	private static GaussianParams[] readParamsFromHdfs(String filename, int k, int d) throws IOException {
		GaussianParams[] params = new GaussianParams[k];
		for (int i = 0; i < k; i++) {
			params[i] = new GaussianParams(d);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		try {
			String line;
			line = br.readLine();
			int counter = 0;
			while (line != null) {
				int index = counter / 3;
				switch (counter % 3) {
				case 0:
					params[index].setW(Double.parseDouble(line));
					break;
				case 1:
					params[index].setMu(line);
					break;
				case 2:
					params[index].setSigma(line);
					break;
				default:
					break;
				}
				counter++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return params;
	}
	
	public static void main(String[] args) throws IOException {
		String path = "/Users/andrea/git/mapreduce/src/main/resources/params.txt";
		
		GaussianParams[] params = readParamsFromHdfs(path, 2, 3);
		
		for (int i = 0; i < params.length; i++) {
			String output = String.format("%s \t %s \t %s", params[i].getWasString(), params[i].getMuAsString(), params[i].getSigmaAsString());
			System.out.println(output);
		}
	}

}
