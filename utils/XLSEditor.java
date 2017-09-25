package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import law.ChitietVanban;
import law.LawNode;
import medical.Disease;

public class XLSEditor {
	private String filePath = System.getProperty("user.dir") + "/exported/data.xlsx";

	public XLSEditor() {
	}

	public XLSEditor(String filePath) {
		this.filePath = filePath;
	}

	public void readXLSXFile(String sheetName, ArrayList<Disease> diseases) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));
		XSSFSheet sheet = wb.getSheet(sheetName);
		XSSFRow row;
		XSSFCell cell;

		Iterator<?> rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			Iterator<?> cells = row.cellIterator();
			int index = 0;
			Disease dis = new Disease();
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				String value = "";
				if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					value = cell.getStringCellValue() + "";
				} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					value = cell.getNumericCellValue() + "";
				} else {
					// U Can Handel Boolean, Formula, Errors
				}
				fillData(dis, index, value);
				index++;
			}
			diseases.add(dis);
		}

	}

	public void readXLSXFile(String sheetName, String vanbanID) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));
		XSSFSheet sheet = wb.getSheet(sheetName);
		XSSFRow row;
		XSSFCell cell;

		Iterator<?> rows = sheet.rowIterator();

		String[] parentTrack = { "", "", "", "", "", "" };

		Queue<ChitietVanban> queue = new LinkedList<>();
		int rownumber = 0;
		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			rownumber++;
			if (rownumber != 1) {
				Iterator<?> cells = row.cellIterator();
				int index = 0;
				int lastFound = 0;
				String data = "";
				String lastMinhhoa = "";
				ChitietVanban dieukhoan = new ChitietVanban();
				queue.add(dieukhoan);
				while (cells.hasNext()) {
					cell = (XSSFCell) cells.next();
					String value = "";
					if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						value = cell.getStringCellValue() + "";
					} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						value = cell.getNumericCellValue() + "";
					} else {
						// U Can Handel Boolean, Formula, Errors
					}
					if (value.length() > 0) {
						switch (index) {
						case 0:
							parentTrack[0] = value;
							parentTrack[1] = "";
							parentTrack[2] = "";
							parentTrack[3] = "";
							parentTrack[4] = "";
							parentTrack[5] = "";
							dieukhoan.setSo(value);
							lastFound = 0;
							break;
						case 3:
							parentTrack[1] = value;
							parentTrack[2] = "";
							parentTrack[3] = "";
							parentTrack[4] = "";
							parentTrack[5] = "";
							dieukhoan.setSo(value);
							lastFound = 1;
							break;
						case 6:
							parentTrack[2] = value;
							parentTrack[3] = "";
							parentTrack[4] = "";
							parentTrack[5] = "";
							dieukhoan.setSo(value);
							lastFound = 2;
							break;
						case 9:
							parentTrack[3] = value;
							parentTrack[4] = "";
							parentTrack[5] = "";
							dieukhoan.setSo(value);
							lastFound = 3;
							break;
						case 12:
							parentTrack[4] = value;
							parentTrack[5] = "";
							dieukhoan.setSo(value);
							lastFound = 4;
							break;
						case 15:
							parentTrack[5] = value;
							dieukhoan.setSo(value);
							lastFound = 5;
							break;
						case 18:
							dieukhoan.setMinhhoa(value);
							break;
						default:
							if ((index % 3) == 1) {
								dieukhoan.setTieude(value);
							} else {
								dieukhoan.setNoidung(value);
							}
							break;
						}
					}
					index++;
				}
				for (int i = (lastFound); i >= 0; i--) {
					if (i > 0) {
						if (parentTrack[i - 1].length() > 0) {
							dieukhoan.setCha(parentTrack[i - 1]);
							if (i - 1 > 0) {
								for (int j = (i - 1); j >= 0; j--) {

									if (parentTrack[j - 1].length() > 0) {
										dieukhoan.setOng(parentTrack[j - 1]);
										if (j - 1 > 0) {
											for (int k = (j - 1); k >= 0; k--) {

												if (parentTrack[k - 1].length() > 0) {
													dieukhoan.setCu(parentTrack[k - 1]);

												}
												break;
											}
										}
									}
									break;
								}
							}
							break;
						}
					}
				}
			}
		}

		for (ChitietVanban chitiet : queue) {
			String cha = "null";
			String minhhoa = "";
			String noidung = "";
			String tieude = "";
			if (chitiet.getCha() != null && chitiet.getOng() != null && chitiet.getCu() != null) {
				cha = "(select id from \"tblChitietvanban\" where So = '" + chitiet.getCha().replace("'", "\'").trim()
						+ "' and cha in (Select id from \"tblChitietvanban\" where So = '" + chitiet.getOng().replace("'", "\'").trim()
						+ "' and cha in (Select id from \"tblChitietvanban\" where So = '" + chitiet.getCu().replace("'", "\'").trim()
						+ "' and vanbanid = " + vanbanID + ")))";
			}else if (chitiet.getCha() != null && chitiet.getOng() != null) {
				cha = "(select id from \"tblChitietvanban\" where So = '" + chitiet.getCha().replace("'", "\'").trim()
						+ "' and cha in (Select id from \"tblChitietvanban\" where So = '" + chitiet.getOng().replace("'", "\'").trim()
						+ "' and vanbanid = " + vanbanID + "))";
			}else if(chitiet.getCha() != null){
				cha ="(Select id from \"tblChitietvanban\" where So = '" + chitiet.getCha().replace("'", "\'").trim()
						+ "' and vanbanid = " + vanbanID + ")";
			}
			
			
			if (chitiet.getMinhhoa() != null) {
				minhhoa = chitiet.getMinhhoa().replace("'", "\'");
			}
			if (chitiet.getNoidung() != null) {
				noidung = chitiet.getNoidung().replace("'", "\'");
			}
			if (chitiet.getTieude() != null) {
				tieude = chitiet.getTieude().replace("'", "\'");
			}

			String query = "INSERT INTO \"tblChitietvanban\" (\"So\",\"tieude\",\"noidung\",\"minhhoa\",\"cha\",\"vanbanid\",\"forSearch\") VALUES ('"
					+ chitiet.getSo().replace("'", "\'").trim() + "','" + tieude.trim() + "','" + noidung.trim() + "','"
					+ minhhoa.trim() + "'," + cha.trim() + "," + vanbanID.trim() + ",'"
					+ chitiet.getSo().replace("'", "\'").toLowerCase().trim() + " " + tieude.toLowerCase().trim() + " "
					+ noidung.toLowerCase().trim() + " " + minhhoa.toLowerCase().trim() + "');";

			Path filePath = Paths.get(System.getProperty("user.dir") + "/exported/" + sheetName + "_query.txt");
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
			Files.write(filePath, ("\n\n" + query).getBytes(), StandardOpenOption.APPEND);
		}

	}

	private void fillData(Disease disease, int index, String text) {
		if (index == 0) {
			disease.setName(text);
		} else if (index == 1) {
			disease.setGeneral(text);
		} else if (index == 2) {
			disease.setSymptom(text);
		} else if (index == 3) {
			disease.setDiagnose(text);
		} else if (index == 4) {
			disease.setCause(text);
		} else if (index == 5) {
			disease.setTreatment(text);
		} else if (index == 6) {
			disease.setObviate(text);
		} else if (index == 7) {
			disease.setTraditional(text);
		} else if (index == 8) {
			disease.setRisk(text);
		} else if (index == 9) {
			disease.setComplication(text);
		} else if (index == 10) {
			disease.setCare(text);
		}
	}

	public void deleteAllSheets() {
		XSSFWorkbook wb;
		try {
			wb = new XSSFWorkbook(new FileInputStream(filePath));
			while (wb.getNumberOfSheets() > 0) {
				wb.removeSheetAt(0);
			}
			FileOutputStream fileOut = new FileOutputStream(filePath);

			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addRowToXLSXFile(String filePath, String sheetName, LawNode node) throws IOException {

		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));
		XSSFSheet sheet;
		sheet = wb.getSheet(sheetName);

		if (sheet == null) {
			sheet = wb.createSheet(sheetName);
			XSSFRow row = sheet.createRow(0);

			// headers for QC41
			// String[] headers = { "Phan-So", "Phan-Tieude", "Phan-Noidung",
			// "Chuong-So", "Chuong-Tieude",
			// "Chuong-Noidung", "Muc-So", "Muc-Tieude", "Muc-Noidung",
			// "Dieu-So", "Dieu-Tieude", "Dieu-Noidung",
			// "Khoan-So", "Khoan-Tieude", "Khoan-Noidung", "Diem-So",
			// "Diem-Tieude", "Diem-Noidung" };

			// headers for ND46
			String[] headers = { "Phan-So", "Phan-Tieude", "Phan-Noidung", "Chuong-So", "Chuong-Tieude",
					"Chuong-Noidung", "Muc-So", "Muc-Tieude", "Muc-Noidung", "Dieu-So", "Dieu-Tieude", "Dieu-Noidung",
					"Khoan-So", "Khoan-Tieude", "Khoan-Noidung", "Diem-So", "Diem-Tieude", "Diem-Noidung", "Minhhoa" };
			for (String str : headers) {
				addCell(row, str);
			}
		}
		XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);

		// iterating c number of columns
		int baseCol = 0;
		switch (node.getType().toLowerCase()) {
		case "chương":
			baseCol = 3;
			break;
		case "mục":
			baseCol = 6;
			break;
		case "điều":
			baseCol = 9;
			break;
		case "khoản":
			baseCol = 12;
			break;
		case "điểm":
			baseCol = 15;
			break;
		default:
			break;
		}

		addCell(row, baseCol, node.getNumber());
		addCell(row, baseCol + 1, node.getTitle());
		addCell(row, baseCol + 2, node.getDetails());

		FileOutputStream fileOut = new FileOutputStream(filePath);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	public void addRowToXLSXFile(String sheetName, Disease disease) throws IOException {

		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));
		XSSFSheet sheet;
		sheet = wb.getSheet(sheetName);

		if (sheet == null) {
			sheet = wb.createSheet(sheetName);
			XSSFRow row = sheet.createRow(0);
			String[] headers = { "Name", "General", "Symptoms", "Diagnose", "Cause", "Treatment", "Obviate",
					"Traditional", "Risk", "Complication", "Care" };
			for (String str : headers) {
				addCell(row, str);
			}
		}
		XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);

		// iterating c number of columns
		for (String str : disease.getInfo()) {
			addCell(row, str);
		}

		FileOutputStream fileOut = new FileOutputStream(filePath);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	private void addCell(XSSFRow row, int col, String str) {
		XSSFCell cell = row.createCell(col);

		cell.setCellValue(str);
	}

	private void addCell(XSSFRow row, String str) {
		int cellNum = row.getLastCellNum();
		if (cellNum < 0) {
			cellNum = 0;
		}
		XSSFCell cell = row.createCell(cellNum);

		cell.setCellValue(str);
	}
}
