package com.pushkarev.chartographer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import com.pushkarev.chartographer.service.ChartaService;
import com.pushkarev.chartographer.service.exceptions.ChartaNotFoundException;

@SpringBootTest
class ChartaServiceTest {

	@Autowired
	ChartaService chartaService;

	static String pathOfTestImageBMP = "testImageBMP.bmp";

	static File testImageBMP;

	private void getFileFromResource() throws URISyntaxException {

		ClassLoader classLoader = getClass().getClassLoader();

		URL resourceTestImageBMP = classLoader.getResource(pathOfTestImageBMP);

		if ((resourceTestImageBMP == null)) {
			throw new IllegalArgumentException("file not found! " + pathOfTestImageBMP);
		} else {
			testImageBMP = new File(resourceTestImageBMP.toURI());
		}
	}

	// test create method
	@Test
	void proccessCreateCharta_shouldReturnId() throws Exception {

		int width = 2000;

		int height = 2500;

		String id = chartaService.createNewCharta(width, height);

		assertEquals(String.class, id.getClass());

		chartaService.deleteChartaById(id);
	}

	@Test
	void proccessCreateCharta_shouldCheckLengthsOfCreatedCharta() throws Exception {

		int expectedWidth = 2000;

		int expectedHeight = 2500;

		String id = chartaService.createNewCharta(expectedWidth, expectedHeight);

		File pathOfCharta = new File("path\\to\\content\\folder\\" + id + "." + "bmp");

		BufferedImage charta = ImageIO.read(pathOfCharta);

		int actualWidth = charta.getWidth();

		int actualHeight = charta.getHeight();

		assertEquals(expectedWidth, actualWidth);

		assertEquals(expectedHeight, actualHeight);

		chartaService.deleteChartaById(id);
	}

	// test add method
	@Test
	void proccessAddPartOfCharta_shouldReturnException_byNull() throws Exception {

		int width = 500;

		int height = 500;

		int x = 0;

		int y = 0;

		assertThrows(IllegalArgumentException.class,
				() -> chartaService.addNewPartToChartaById(null, x, y, width, height, null));
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnException_byNonExistentCharta() throws Exception {

		getFileFromResource();

		int width = 500;

		int height = 500;

		int x = 0;

		int y = 0;

		String id = "12345";

		BufferedImage partOfCharta = ImageIO.read(testImageBMP);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(partOfCharta, "bmp", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());

		assertThrows(ChartaNotFoundException.class,
				() -> chartaService.addNewPartToChartaById(id, x, y, width, height, sampleFile));
	}

	// test get method
	@Test
	void proccessGetPartOfCharta_shouldCheckLengthsOfPartOfCharta() throws Exception {

		int expectedWidth = 500;

		int expectedHeight = 700;

		int x = 0;

		int y = 0;
		
		String id = chartaService.createNewCharta(expectedWidth, expectedHeight);
		
		InputStream is = new ByteArrayInputStream(chartaService.getPartOfChartaAsByteArray(id, x, y, expectedWidth, expectedHeight));
		
        BufferedImage charta = ImageIO.read(is);
        
        is.close();
        
        int actualWidth = charta.getWidth();
        
        int actualHeight = charta.getHeight();

		assertEquals(expectedWidth, actualWidth);

		assertEquals(expectedHeight, actualHeight);
		
		chartaService.deleteChartaById(id);
	}
	
	@Test
	void proccessGetPartOfCharta_shouldReturnException_byNull() throws Exception {

		int width = 500;

		int height = 500;

		int x = 0;

		int y = 0;

		assertThrows(IllegalArgumentException.class,
				() -> chartaService.getPartOfChartaAsByteArray(null, x, y, width, height));
	}

	@Test
	void proccessGetPartOfCharta_shouldReturnException_byNonExistentCharta() throws Exception {

		int width = 500;

		int height = 500;

		int x = 0;

		int y = 0;

		String id = "12345";

		assertThrows(ChartaNotFoundException.class,
				() -> chartaService.getPartOfChartaAsByteArray(id, x, y, width, height));
	}

	// test get method
	@Test
	void proccessDeleteCharta_shouldDeleteCharta() throws Exception {
		
        int width = 2000;

		int height = 2500;

		String id = chartaService.createNewCharta(width, height);

		chartaService.deleteChartaById(id);

		assertThrows(ChartaNotFoundException.class,
				() -> chartaService.deleteChartaById(id));
	}
	
	@Test
	void proccessDeleteCharta_shouldReturnException_byNull() throws Exception {

		assertThrows(IllegalArgumentException.class,
				() -> chartaService.deleteChartaById(null));
	}

	@Test
	void proccessDeleteCharta_shouldReturnException_byNonExistentCharta() throws Exception {

		String id = "12345";

		assertThrows(ChartaNotFoundException.class,
				() -> chartaService.deleteChartaById(id));
	}
}
