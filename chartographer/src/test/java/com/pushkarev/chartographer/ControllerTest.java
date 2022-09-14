package com.pushkarev.chartographer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class ControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	static String pathOfTestImageBMP = "testImageBMP.bmp";

	static String pathOfTestImageJPG = "testImageJPG.jpg";

	static File testImageBMP;

	static File testImageJPG;

	private void getFileFromResource() throws URISyntaxException {

		ClassLoader classLoader = getClass().getClassLoader();

		URL resourceTestImageBMP = classLoader.getResource(pathOfTestImageBMP);

		URL resourceTestImageJPG = classLoader.getResource(pathOfTestImageJPG);

		if ((resourceTestImageBMP == null) || (resourceTestImageJPG == null)) {
			throw new IllegalArgumentException("file not found! " + pathOfTestImageBMP + " or " + pathOfTestImageJPG);
		} else {
			testImageBMP = new File(resourceTestImageBMP.toURI());
			testImageJPG = new File(resourceTestImageJPG.toURI());
		}
	}

	// test POST create method
	@Test
	void proccessCreateCharta_shouldReturnStatus201Created() throws Exception {

		int width = 2000;

		int height = 2500;
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + width + "&height=" + height)
						.toString(),
				null, String.class);

		HttpStatus actual = responseEntity.getStatusCode();

		HttpStatus expected = HttpStatus.CREATED;

		assertEquals(expected, actual);
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + responseEntity.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	@Test
	void proccessCreateCharta_shouldReturnStatus201Created_byBigLengths() throws Exception {

		int width = 20000;

		int height = 40000;

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + width + "&height=" + height)
						.toString(),
				null, String.class);
		
		HttpStatus actual = responseEntity.getStatusCode();

		HttpStatus expected = HttpStatus.CREATED;

		assertEquals(expected, actual);
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + responseEntity.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessCreateCharta_shouldReturnStatus400BadRequest_byNotValidatedLengths() throws Exception {

		int width = 30000;

		int height = 60000;

		HttpStatus actual = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + width + "&height=" + height).toString(), null,
				String.class).getStatusCode();

		HttpStatus expected = HttpStatus.BAD_REQUEST;

		assertEquals(expected, actual);
	}
	
	@Test
	void proccessCreateCharta_shouldReturnStatus400BadRequest_byZeroLengths() throws Exception {

		int width = 0;

		int height = 0;

		HttpStatus actual = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + width + "&height=" + height).toString(), null,
				String.class).getStatusCode();

		HttpStatus expected = HttpStatus.BAD_REQUEST;

		assertEquals(expected, actual);
	}

	@Test
	void proccessCreateCharta_shouldReturnStatus400BadRequest_byInvalidLength() throws Exception {

		String width = "20@00";

		String height = "!000";

		HttpStatus actual = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + width + "&height=" + height).toString(), null,
				String.class).getStatusCode();

		HttpStatus expected = HttpStatus.BAD_REQUEST;

		assertEquals(expected, actual);
	}

	@Test
	void proccessCreateCharta_shouldReturnStatus400BadRequest_byEmptyArgs() throws Exception {

		String width = "";

		String height = "";

		HttpStatus actual = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + width + "&height=" + height).toString(), null,
				String.class).getStatusCode();

		HttpStatus expected = HttpStatus.BAD_REQUEST;

		assertEquals(expected, actual);
	}

	// test POST add method
	@Test
	void proccessAddPartOfCharta_shouldReturnStatus200OK() throws Exception {

		getFileFromResource();

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500;

		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		BufferedImage image = ImageIO.read(testImageBMP);
						
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
		ImageIO.write(image, "bmp", baos);
						
		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());
				
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isOk());

		baos.close();

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus400BadReuest_byInvalidLengths() throws Exception {

		getFileFromResource();

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		String widthPartOfCharta = "5@00";

		String heightPartOfCharta = "!500";

		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		BufferedImage image = ImageIO.read(testImageBMP);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "bmp", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isBadRequest());

		baos.close();

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus400BadReuest_byNegativeLengths() throws Exception {
		
		getFileFromResource();

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		int widthPartOfCharta = -500;

		int heightPartOfCharta = -500;

		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		BufferedImage image = ImageIO.read(testImageBMP);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "bmp", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isBadRequest());

		baos.close();

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus400BadReuest_byInvalidCoordinates() throws Exception {
		
		getFileFromResource();

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500;

		String x = "@100";

		String y = "25$0";

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		BufferedImage image = ImageIO.read(testImageBMP);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "bmp", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isBadRequest());

		baos.close();

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus400BadReuest_byEmptyArgs() throws Exception {
		
		getFileFromResource();

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		String widthPartOfCharta = "";

		String heightPartOfCharta = "";

		String x = "";

		String y = "";

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		BufferedImage image = ImageIO.read(testImageBMP);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "bmp", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isBadRequest());

		baos.close();

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus400BadReuest_withoutFile() throws Exception {
		
		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500;

		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)).andExpect(status().isBadRequest());

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus404NotFound_byNonExistentCharta() throws Exception {
		
		getFileFromResource();

		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500;

		int x = 0;

		int y = 0;

		String id = "12345";

		BufferedImage image = ImageIO.read(testImageBMP);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "bmp", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageBMP, "image/bmp",
				baos.toByteArray());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id + "/?x=" + x + "&y=" + y + "&width="
				+ widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isNotFound());

		baos.close();
	}

	@Test
	void proccessAddPartOfCharta_shouldReturnStatus400BadReuest_byNotBmpFileFormat() throws Exception {
		
		getFileFromResource();

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500;

		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta)
						.toString(),
				null, String.class);

		BufferedImage image = ImageIO.read(testImageJPG);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "jpg", baos);

		MockMultipartFile sampleFile = new MockMultipartFile("file", pathOfTestImageJPG, "image/jpg",
				baos.toByteArray());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(multipart("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y=" + y
				+ "&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).file(sampleFile))
				.andExpect(status().isBadRequest());

		baos.close();

		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	// test GET method
	@Test
	void proccessGetPartOfCharta_shouldReturnStatus200OK() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;
		
		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500; 
		
		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)
				.contentType("image/bmp")).andExpect(status().is(200));
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
					
	@Test
	void proccessGetPartOfCharta_shouldReturnBmpFile() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;
		
		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500; 
		
		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta).contentType("image/bmp")).andExpect(status().is(200)).andReturn();
				
		assertEquals("image/bmp", result.getResponse().getContentType());
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	@Test
	void proccessGetPartOfCharta_shouldReturnStatus400BadReuest_byInvalidLengths() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;
		
		String widthPartOfCharta = "5a00";

		String heightPartOfCharta = "500@"; 
		
		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)
				.contentType("image/bmp")).andExpect(status().is(400));
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	@Test
	void proccessGetPartOfCharta_shouldReturnStatus400BadReuest_byNegativeLengths() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;
		
		int widthPartOfCharta = -500;

		int heightPartOfCharta = -500; 
		
		int x = 0;

		int y = 0;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)
				.contentType("image/bmp")).andExpect(status().is(400));
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	@Test
	void proccessGetPartOfCharta_shouldReturnStatus400BadReuest_byInvalidCoordinates() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;
		
		int widthPartOfCharta = -500;

		int heightPartOfCharta = -500; 
		
		String x = "@100";

		String y = "25$0";

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)
				.contentType("image/bmp")).andExpect(status().is(400));
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	@Test
	void proccessGetPartOfCharta_shouldReturnStatus400BadReuest_byEmptyArgs() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;
		
		String widthPartOfCharta = "";

		String heightPartOfCharta = ""; 
		
		String x = "";

		String y = "";

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("http://localhost:" + port + "/chartas/" + id.getBody() + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)
				.contentType("image/bmp")).andExpect(status().is(400));
		
		restTemplate.exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null,
				String.class).getStatusCode();
	}
	
	@Test
	void proccessGetPartOfCharta_shouldReturnStatus404NotFound_byNonExistentCharta() throws Exception {
		
		int widthPartOfCharta = 500;

		int heightPartOfCharta = 500; 
		
		int x = 0;

		int y = 0;
		
		String id = "12345";
								
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("http://localhost:" + port + "/chartas/" + id + "/?x=" + x + "&y="+ y +"&width=" + widthPartOfCharta + "&height=" + heightPartOfCharta)
				.contentType("image/bmp")).andExpect(status().is(404));
	}
	
	//test DELETE method
	@Test
	void proccessDeleteCharta_shouldReturnStatus200OK() throws Exception {

		int widthOfCharta = 2000;

		int heightOfCharta = 2500;

		ResponseEntity<String> id = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/chartas/?width=" + widthOfCharta + "&height=" + heightOfCharta).toString(), null,
				String.class);
		
		HttpStatus actual = restTemplate
				  .exchange("http://localhost:" + port + "/chartas/" + id.getBody() + "/", HttpMethod.DELETE, null, String.class).getStatusCode();

		HttpStatus expected = HttpStatus.OK;

		assertEquals(expected, actual);
	}
	
	@Test
	void proccessDeleteCharta_shouldReturnStatus404NotFound_byNonExistentCharta() throws Exception {

		String id  = "12345";
		
		HttpStatus actual = restTemplate
				  .exchange("http://localhost:" + port + "/chartas/" + id + "/", HttpMethod.DELETE, null, String.class).getStatusCode();

		HttpStatus expected = HttpStatus.NOT_FOUND;

		assertEquals(expected, actual);
	}
}
