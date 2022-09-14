package com.pushkarev.chartographer.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pushkarev.chartographer.service.exceptions.ChartaInvalidCoordinateException;
import com.pushkarev.chartographer.service.exceptions.ChartaNotFoundException;
import com.pushkarev.chartographer.service.exceptions.InvalidContentTypeException;

@Service
public class ChartaServiceImpl implements ChartaService {

	private List<String> idsOfChartas = new ArrayList<>();

	private final static String DEFAULT_EXCEPTION_MESSAGE = "ChartaService exception ";

	private final static String NOT_FOUND_BY_ID_EXCEPTION_MESSAGE = "Not found charta by id ";

	private final static String CONTENT_TYPE_OF_CHARTA = "bmp";

	ChartaServiceImpl() {

		File targetFolder = new File(getPathOfTargetFolder());

		addFilesFromFolderToList(targetFolder);
	}

	@Override
	public String createNewCharta(int width, int height) throws IOException {

		String id = UUID.randomUUID().toString();

		File pathOfCharta = new File(getPathOfTargetFolder() + id + "." + CONTENT_TYPE_OF_CHARTA);

		BufferedImage charta = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			ImageIO.write(charta, CONTENT_TYPE_OF_CHARTA, pathOfCharta);
		} catch (IOException e) {
			throw new IOException(DEFAULT_EXCEPTION_MESSAGE + e.getMessage(), e);
		}

		idsOfChartas.add(id);

		return id;
	}

	@Override
	public void addNewPartToChartaById(String id, int x, int y, int width, int height, MultipartFile file)
			throws IOException, ChartaNotFoundException, InvalidContentTypeException {

		if (id == null || file == null) {
			throw new IllegalArgumentException("One of the arguments is null");
		}

		if (!idsOfChartas.contains(id)) {
			throw new ChartaNotFoundException(NOT_FOUND_BY_ID_EXCEPTION_MESSAGE + id);
		}

		try {

			BufferedImage newPartOfCharta = ImageIO.read(file.getInputStream());

			File pathOfCharta = new File(getPathOfTargetFolder() + id + "." + CONTENT_TYPE_OF_CHARTA);

			BufferedImage charta = ImageIO.read(pathOfCharta);

			Graphics g = charta.getGraphics();

			g.drawImage(newPartOfCharta, x, y, width, height, null);

			ImageIO.write(charta, CONTENT_TYPE_OF_CHARTA, pathOfCharta);

			g.dispose();

		} catch (IOException e) {
			throw new IOException(DEFAULT_EXCEPTION_MESSAGE + e.getMessage(), e);
		}
	}

	@Override
	public byte[] getPartOfChartaAsByteArray(String id, int x, int y, int width, int height)
			throws IOException, ChartaNotFoundException, ChartaInvalidCoordinateException {

		if (id == null) {
			throw new IllegalArgumentException("Argument is null");
		}

		if (!idsOfChartas.contains(id)) {
			throw new ChartaNotFoundException(NOT_FOUND_BY_ID_EXCEPTION_MESSAGE + id);
		}

		File pathOfCharta = new File(getPathOfTargetFolder() + id + "." + CONTENT_TYPE_OF_CHARTA);

		try {

			BufferedImage charta = ImageIO.read(pathOfCharta);

			if (charta.getWidth() - x <= 0 || charta.getHeight() - y <= 0) {
				throw new ChartaInvalidCoordinateException("Invalid coordinate");
			}

			int newWidth = getMaxPossibleLengthForSubimage(charta.getWidth(), width, x);

			int newHeight = getMaxPossibleLengthForSubimage(charta.getHeight(), height, y);

			BufferedImage partOfImage = charta.getSubimage(x, y, newWidth, newHeight);

			BufferedImage returnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			returnImage.getGraphics().drawImage(partOfImage, 0, 0, null);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageIO.write(returnImage, CONTENT_TYPE_OF_CHARTA, baos);
			
			return baos.toByteArray();

		} catch (IOException e) {
			throw new IOException(DEFAULT_EXCEPTION_MESSAGE + e.getMessage(), e);
		}
	}

	@Override
	public void deleteChartaById(String id) throws ChartaNotFoundException, IOException {

		if (id == null) {
			throw new IllegalArgumentException("Argument is null");
		}

		if (!idsOfChartas.contains(id)) {
			throw new ChartaNotFoundException(NOT_FOUND_BY_ID_EXCEPTION_MESSAGE + id);
		} else {

			Path path = Paths.get(getPathOfTargetFolder() + id + "." + CONTENT_TYPE_OF_CHARTA);

			try {
				Files.delete(path);
				idsOfChartas.remove(id);
			} catch (IOException e) {
				throw new IOException(DEFAULT_EXCEPTION_MESSAGE + e.getMessage(), e);
			}
		}
	}

	// use with pairs: width + width + x, height + height + y
	private int getMaxPossibleLengthForSubimage(int lengthOfCharta, int requiredLength, int coordinate) {

		int maxPossibleCoordinateOfCharta = lengthOfCharta - coordinate;

		int diffBetweenMaxCoordinateAndRequiredLength = requiredLength - maxPossibleCoordinateOfCharta;

		if (maxPossibleCoordinateOfCharta < requiredLength) {

			return requiredLength - diffBetweenMaxCoordinateAndRequiredLength;

		} else {

			return requiredLength;
		}
	}

	private String getPathOfTargetFolder() {

		File currentDirFile = new File("path\\to\\content\\folder");
				
		if (!currentDirFile.exists()){
			currentDirFile.mkdirs();
		}

		return currentDirFile.getAbsolutePath() + "\\";
	}

	private void addFilesFromFolderToList(final File folder) {

		if (folder.listFiles().length != 0) {

			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					addFilesFromFolderToList(fileEntry);
				} else {

					int index = fileEntry.getName().lastIndexOf(".");

					if (fileEntry.getName().substring(index + 1).equals(CONTENT_TYPE_OF_CHARTA)) {

						String id = fileEntry.getName().substring(0, index);

						idsOfChartas.add(id);
					}
				}
			}

		}
	}
}
