package com.pushkarev.chartographer.controller;

import java.io.IOException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pushkarev.chartographer.service.ChartaServiceImpl;
import com.pushkarev.chartographer.service.exceptions.ChartaInvalidCoordinateException;
import com.pushkarev.chartographer.service.exceptions.ChartaNotFoundException;
import com.pushkarev.chartographer.service.exceptions.InvalidContentTypeException;

@RestController
@RequestMapping("/chartas")
@Validated
public class Controller {

	@Autowired
	private ChartaServiceImpl chartaService;

	private final static int MAX_WITDH_OF_CHARTA = 20_000;

	private final static int MAX_HEIGHT_OF_CHARTA = 50_000;

	private final static int MAX_WITDH_OF_PART_OF_CHARTA = 5_000;

	private final static int MAX_HEIGHT_OF_PART_OF_CHARTA = 5_000;

	private final static int MIN_WITDH_OF_PART_OF_CHARTA = 1;

	private final static int MIN_HEIGHT_OF_PART_OF_CHARTA = 1;

	@PostMapping("/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public String createNewCharta(@RequestParam @Min(1) @Max(MAX_WITDH_OF_CHARTA) int width,
			@RequestParam @Min(1) @Max(MAX_HEIGHT_OF_CHARTA) int height) throws IOException {

		return chartaService.createNewCharta(width, height);
	}

	@PostMapping(value = "/{id}/")
	@ResponseStatus(code = HttpStatus.OK)
	public void addPartToCharta(
			@PathVariable String id, 
			@RequestParam int x, 
			@RequestParam int y,
			@RequestParam @Min(MIN_WITDH_OF_PART_OF_CHARTA) int width, 
			@RequestParam @Min(MIN_HEIGHT_OF_PART_OF_CHARTA) int height,
			@RequestParam MultipartFile file) 
			throws IOException, ChartaNotFoundException, InvalidContentTypeException {
				
		if (file.isEmpty() || !file.getContentType().contentEquals("image/bmp")) {
			throw new InvalidContentTypeException("Invalid content type of file");
		}
		
		chartaService.addNewPartToChartaById(id, x, y, width, height, file);
	}

	@GetMapping(value = "/{id}/", produces = "image/bmp")
	@ResponseStatus(code = HttpStatus.OK)
	@ResponseBody
	public byte[] getPartOfCharta(
			@PathVariable String id, 
			@RequestParam int x, 
			@RequestParam int y,
			@RequestParam @Min(MIN_WITDH_OF_PART_OF_CHARTA) @Max(MAX_WITDH_OF_PART_OF_CHARTA) int width,
			@RequestParam @Min(MIN_HEIGHT_OF_PART_OF_CHARTA) @Max(MAX_HEIGHT_OF_PART_OF_CHARTA) int height)
			throws IOException, ChartaNotFoundException, ChartaInvalidCoordinateException {

		return chartaService.getPartOfChartaAsByteArray(id, x, y, width, height);
	}

	@DeleteMapping("/{id}/")
	@ResponseStatus(code = HttpStatus.OK)
	public void deleteCharta(@PathVariable String id) throws ChartaNotFoundException, IOException {

		chartaService.deleteChartaById(id);
	}
}
