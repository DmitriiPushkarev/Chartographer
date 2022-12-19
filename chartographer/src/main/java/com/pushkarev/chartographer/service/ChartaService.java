package com.pushkarev.chartographer.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.pushkarev.chartographer.service.exceptions.ChartaInvalidCoordinateException;
import com.pushkarev.chartographer.service.exceptions.ChartaNotFoundException;
import com.pushkarev.chartographer.service.exceptions.InvalidContentTypeException;

public interface ChartaService {

	public String createNewCharta(int width, int height) throws IOException;

	public void addNewPartToChartaById(String id, int x, int y, int width, int height, MultipartFile file)
			throws IOException, ChartaNotFoundException, InvalidContentTypeException;

	public byte[] getPartOfChartaAsByteArray(String id, int x, int y, int width, int height)
			throws IOException, ChartaNotFoundException, ChartaInvalidCoordinateException;
	
	public List<byte[]> getAllChartas() throws IOException, ChartaNotFoundException;

	public void deleteChartaById(String id) throws ChartaNotFoundException, IOException;
}
