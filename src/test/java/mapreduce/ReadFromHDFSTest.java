package mapreduce;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import utils.GaussianParams;

public class ReadFromHDFSTest {
	
	@Test
	public void testRead() throws IOException {
		System.out.println("\n TESTING: " + this.getClass().getCanonicalName());
		String path = "/params.txt";
		GaussianParams[] params = GaussianParams.parse(new InputStreamReader(getClass().getResourceAsStream(path)), 2, 2);

		for (int i = 0; i < params.length; i++) {
			String output = String.format("%s \t %s \t %s", params[i].getWasString(), params[i].getMuAsString(), params[i].getSigmaSqrAsString());
			System.out.println(output);
		}
	}

}
