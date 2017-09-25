package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import law.LawNode;
import utils.FFDriver;
import utils.XLSEditor;

public class LawCrawler {
	private ArrayList<LawNode> nodes;
	private WebDriver driver;

	// ========================
	// QC41 settings
	private String qc41XlsxFile = "QC412016.xlsx";
	private String qc41VanbanID = "1";

	// ND46 settings
	private String nd46XlsxFile = "ND462016.xlsx";
	private String nd46VanbanID = "2";

	// TT01 settings
	private String tt01XlsxFile = "TT012016.xlsx";
	private String tt01VanbanID = "3";

	// LuatGTDB settings
	private String LuatGTDBXlsxFile = "LuatGTDB.xlsx";
	private String LuatGTDBVanbanID = "4";

	// LuatGTDB settings
	private String LuatXLVPHCXlsxFile = "LuatXLVPHC.xlsx";
	private String LuatXLVPHCVanbanID = "5";
	// ==========================

	@BeforeSuite
	public void init() {
		System.setProperty("webdriver.gecko.driver", "/Users/Shared/Jenkins/Desktop/Test/geckodriver/geckodriver");
		driver = new FFDriver().getDriver();
	}

	@AfterSuite
	public void cleanup() {
		driver.quit();
	}

	// @Test
	public void linhtinh() {
		System.out.println(4 % 3);
	}

	// @Test
	public void getQC41() {
		getContentQC41();
		getPhuLuc1();
		getPhuLuc2();
		String xlsxFile = qc41XlsxFile;
		String vanbanID = qc41VanbanID;
		String[] tabs = { "QC41", "PL1", "PL2" };
		generateQuery(xlsxFile, tabs, vanbanID);
	}

	public void getContentQC41() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/QC41.html");
		ArrayList<WebElement> allRows = getAllRows();
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentPart = null;
		LawNode currentChapter = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = null;
		for (WebElement row : allRows) {
			List<WebElement> e = null;
			if (row.getAttribute("style").contains("center")) {
				e = row.findElements(By.xpath("./b/span"));
				if (!e.isEmpty()) {
					if (e.get(0).getText().trim().length() > 0) {
						String text = e.get(0).getText().trim();
						// System.out.println("- "+text);
						if (text.toLowerCase().startsWith("phần")) {
							LawNode node = new LawNode();
							currentPart = node;
							currentNode = node;
							node.setNumber(text);
							node.setType("Phần");
							nodes.add(node);
							currentChapter = null;
							currentDieu = null;
							currentKhoan = null;
							currentDiem = null;
						} else if (text.toLowerCase().startsWith("chương")) {
							LawNode node = new LawNode();
							currentChapter = node;
							currentNode = node;
							node.setNumber(text);
							node.setType("Chương");
							currentPart.addChild(node);
							currentDieu = null;
							currentKhoan = null;
							currentDiem = null;
						} else if (text.toLowerCase().startsWith("phụ lục")) {
							LawNode node = new LawNode();
							currentPart = node;
							currentNode = node;
							node.setNumber(text);
							node.setType("Phụ lục");
							nodes.add(node);
							currentChapter = null;
							currentDieu = null;
							currentKhoan = null;
							currentDiem = null;
						} else if (text.toLowerCase().startsWith("hình")) {
							currentNode.setDetails(text);
						} else if (currentNode != null) {
							currentNode.setTitle(text);
						} else {
							currentNode.setTitle("T1: SOMETHING WRONG HERE!!");
						}
					}
				}
			} else {
				if (!row.findElements(By.xpath("./b/span")).isEmpty()
						|| !row.findElements(By.xpath("./span/b")).isEmpty()) {
					String text = "";
					if (!row.findElements(By.xpath("./b/span")).isEmpty()) {
						text = row.findElements(By.xpath("./b/span")).get(0).getText().trim();
					} else {
						text = row.findElements(By.xpath("./span/b")).get(0).getText().trim();
					}
					// System.out.println(text);
					if (!row.findElements(By.xpath("./span")).isEmpty()) {
						LawNode node = new LawNode();
						currentNode = node;
						node.setNumber(text);
						node.setTitle(row.findElements(By.xpath("./span")).get(0).getText());
						if (text.split("\\.").length > 2) {
							node.setType("Điểm");
							currentDiem = node;
							currentKhoan.addChild(node);
						} else {
							node.setType("Khoản");
							currentKhoan = node;
							currentDieu.addChild(node);
						}
					} else if (text.toLowerCase().startsWith("hình") || text.toLowerCase().startsWith("bảng")
							|| text.toLowerCase().startsWith("kích thước")) {
						currentNode.setDetails(text);
					} else {
						if (currentNode != null && text.length() > 0) {
							LawNode node = new LawNode();
							currentDieu = node;
							currentNode = node;
							node.setNumber(text.split("\\.")[0].trim());
							node.setType("Điều");
							node.setTitle(text.split("\\.")[1].trim());
							if (currentChapter != null) {
								currentChapter.addChild(node);
							} else {
								currentPart.addChild(node);
							}
						}
					}
				} else {
					if (row.findElements(By.xpath("./span")).size() > 0 && currentNode != null) {
						if (row.findElements(By.xpath("./span")).get(0).getText().trim().length() > 0) {
							currentNode.setDetails(row.findElements(By.xpath("./span")).get(0).getText().trim());
						}
					} else {
						currentNode.setDetails("T2: SOMETHING WRONG HERE!!");
					}
				}
			}

			// System.out.println(" - "+currentNode.getType()+":
			// "+currentNode.getNumber()+"-"+currentNode.getTitle()+"-"+currentNode.getDetails());
		}
		// exportXML(nodes, "QC41.xml");
		try {
			String xlsxFileName = qc41XlsxFile;
			exportXlS(xlsxFileName, "QC41", nodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getPhuLuc1() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/PL-1.html");
		ArrayList<WebElement> allRows = getAllRows();
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentPart = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = null;
		for (WebElement row : allRows) {
			List<WebElement> e = null;
			if (row.getAttribute("style").contains("center")) {
				e = row.findElements(By.xpath("./b/span"));
				if (!e.isEmpty()) {
					if (e.get(0).getText().trim().length() > 0) {
						String text = e.get(0).getText().trim();
						// System.out.println("- " + text);
						if (text.toLowerCase().startsWith("phụ lục")) {
							LawNode node = new LawNode();
							currentPart = node;
							currentNode = node;
							node.setNumber(text);
							node.setType("Phụ lục");
							nodes.add(node);
							currentDieu = null;
						} else if (text.toLowerCase().startsWith("hình") || text.toLowerCase().startsWith("bảng")
								|| text.toLowerCase().startsWith("kích thước")
								|| text.toLowerCase().startsWith("thông số kỹ thuật")) {
							currentNode.setDetails(text);
						} else if (currentNode != null) {
							currentNode.setTitle(text);
						} else {
							currentNode.setTitle("T1: SOMETHING WRONG HERE!!");
						}
					}
				}
			} else {
				if (!row.findElements(By.xpath("./b/span")).isEmpty()
						|| !row.findElements(By.xpath("./span/b")).isEmpty()) {
					String text = "";
					if (!row.findElements(By.xpath("./b/span")).isEmpty()) {
						text = row.findElements(By.xpath("./b/span")).get(0).getText().trim();
					} else {
						text = row.findElements(By.xpath("./span/b")).get(0).getText().trim();
					}
					// System.out.println(text);
					if (text.toLowerCase().startsWith("hình") || text.toLowerCase().startsWith("bảng")
							|| text.toLowerCase().startsWith("kích thước")
							|| text.toLowerCase().startsWith("thông số kỹ thuật")) {
						currentNode.setDetails(text);
					} else {
						if (currentNode != null && text.length() > 0) {
							LawNode node = new LawNode();
							String number = text.split(" ")[0].trim();
							node.setNumber(number);
							if (number.split("\\.").length > 2) {
								currentDieu.addChild(node);
								currentKhoan = node;
								node.setType("Khoản");
							} else if (number.split("\\.").length > 1) {
								currentDieu = node;
								node.setType("Điều");
								currentPart.addChild(node);
							} else if (number.split("\\.").length > 0) {
								node.setType("Điểm");
								currentDiem = node;
								currentKhoan.addChild(node);
							}
							node.setTitle(text.replace(number, ""));
							currentNode = node;
						}
					}
				} else {
					if (row.findElements(By.xpath("./span")).size() > 0 && currentNode != null) {
						if (row.findElements(By.xpath("./span")).get(0).getText().trim().length() > 0) {
							currentNode.setDetails(row.findElements(By.xpath("./span")).get(0).getText().trim());
						}
					} else {
						currentNode.setDetails("T2: SOMETHING WRONG HERE!!");
					}
				}
			}

			// System.out.println(" - "+currentNode.getType()+":
			// "+currentNode.getNumber()+"-"+currentNode.getTitle()+"-"+currentNode.getDetails());
		}
		// exportXML(nodes, "QC41-PL1.xml");
		try {
			String xlsxFileName = qc41XlsxFile;
			exportXlS(xlsxFileName, "PL1", nodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getPhuLuc2() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/PL-2.html");
		ArrayList<WebElement> allRows = getAllRows();
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentPart = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = null;
		for (WebElement row : allRows) {
			List<WebElement> e = null;
			if (row.getAttribute("style").contains("center")) {
				e = row.findElements(By.xpath("./b/span"));
				if (!e.isEmpty()) {
					if (e.get(0).getText().trim().length() > 0) {
						String text = e.get(0).getText().trim();
						// System.out.println("- " + text);
						if (text.toLowerCase().startsWith("phụ lục")) {
							LawNode node = new LawNode();
							currentPart = node;
							currentNode = node;
							node.setNumber(text);
							node.setType("Phụ lục");
							nodes.add(node);
							currentDieu = null;
							currentKhoan = null;
							currentDiem = null;
						} else if (text.toLowerCase().startsWith("hình") || text.toLowerCase().startsWith("bảng")
								|| text.toLowerCase().startsWith("kích thước")
								|| text.toLowerCase().startsWith("thông số kỹ thuật")) {
							currentNode.setDetails(text);
						} else if (currentNode != null) {
							currentNode.setTitle(text);
						} else {
							System.out.println("T1: SOMETHING WRONG HERE!!");
						}
					}
				}
			} else {
				if (!row.findElements(By.xpath("./b/span")).isEmpty()
						|| !row.findElements(By.xpath("./span/b")).isEmpty()) {
					String text = "";
					if (!row.findElements(By.xpath("./b/span")).isEmpty()) {
						text = row.findElements(By.xpath("./b/span")).get(0).getText().trim();
					} else {
						text = row.findElements(By.xpath("./span/b")).get(0).getText().trim();
					}
					// System.out.println(text);
					if (text.toLowerCase().startsWith("hình") || text.toLowerCase().startsWith("bảng")
							|| text.toLowerCase().startsWith("kích thước")
							|| text.toLowerCase().startsWith("thông số kỹ thuật")) {
						currentNode.setDetails(text);
					} else {
						if (currentNode != null && text.length() > 0) {
							LawNode node = new LawNode();
							String number = text.split(" ")[0].trim();
							node.setNumber(number);
							if (number.split("\\.").length > 2) {
								currentDieu.addChild(node);
								currentKhoan = node;
								node.setType("Khoản");
							} else if (number.split("\\.").length > 1) {
								currentDieu = node;
								node.setType("Điều");
								currentPart.addChild(node);
								currentKhoan = null;
								currentDiem = null;
							} else if (number.split("\\.").length > 0) {
								node.setType("Điểm");
								currentDiem = node;
								if (currentDieu != null && currentKhoan == null) {
									currentDieu.addChild(node);
								} else {
									currentKhoan.addChild(node);
								}
							}
							node.setTitle(text.replace(number, ""));
							currentNode = node;
						}
					}
				} else {
					if (row.findElements(By.xpath("./span")).size() > 0 && currentNode != null) {
						if (row.findElements(By.xpath("./span")).get(0).getText().trim().length() > 0) {
							currentNode.setDetails(row.findElements(By.xpath("./span")).get(0).getText().trim());
						}
					} else {
						System.out.println("T2: SOMETHING WRONG HERE!!");
					}
				}
			}

			// System.out.println(" - "+currentNode.getType()+":
			// "+currentNode.getNumber()+"-"+currentNode.getTitle()+"-"+currentNode.getDetails());
		}
		// exportXML(nodes, "QC41-PL2.xml");
		try {
			String xlsxFileName = qc41XlsxFile;
			exportXlS(xlsxFileName, "PL2", nodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void getND46() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/ND462016.html");
		ArrayList<WebElement> allRows = getAllRows("//p");
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentChuong = null;
		LawNode currentMuc = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = new LawNode();
		for (WebElement row : allRows) {
			String rawText = row.getText();
			String text = rawText.trim().split("\\. ")[0];
			LawNode node = new LawNode();
			boolean isValid = true;
			switch (text.toLowerCase().split(" ")[0]) {
			case "chương":
				node.setType("Chương");
				currentChuong = node;
				currentMuc = null;
				nodes.add(node);
				break;
			case "mục":
				node.setType("Mục");
				currentMuc = node;
				currentChuong.addChild(node);
				break;
			case "điều":
				node.setType("Điều");
				currentDieu = node;
				if (currentMuc != null) {
					currentMuc.addChild(node);
				} else {
					currentChuong.addChild(node);
				}
				break;
			default:
				try {
					int n = Integer.parseInt(text);
					node.setType("Khoản");
					currentKhoan = node;
					currentDieu.addChild(node);
				} catch (Exception e) {
					text = text.split("\\) ")[0];
					if (text.length() < 2) {
						node.setType("Điểm");
						currentDiem = node;
						currentKhoan.addChild(node);
					} else {
						isValid = false;
						currentNode.setDetails(rawText);
					}
				}

				break;
			}
			if (isValid) {
				if (text.length() != rawText.length()) {
					node.setTitle(rawText.subSequence(text.length() + 1, rawText.length()).toString());
				}
				node.setNumber(text);
				currentNode = node;
			}
		}

		try {
			String xlsxFileName = nd46XlsxFile;
			String vanbanID = nd46VanbanID;
			exportXlS(xlsxFileName, xlsxFileName, nodes);
			String[] tabs = { xlsxFileName };
			generateQuery(xlsxFileName, tabs, vanbanID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void getTT01() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/TT012016.html");
		ArrayList<WebElement> allRows = getAllRows("//p");
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentChuong = null;
		LawNode currentMuc = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = new LawNode();
		for (WebElement row : allRows) {
			String rawText = row.getText();
			String text = rawText.trim().split("\\. ")[0];
			LawNode node = new LawNode();
			boolean isValid = true;
			switch (text.toLowerCase().split(" ")[0]) {
			case "chương":
				node.setType("Chương");
				currentChuong = node;
				currentMuc = null;
				nodes.add(node);
				break;
			case "mục":
				node.setType("Mục");
				currentMuc = node;
				currentChuong.addChild(node);
				break;
			case "điều":
				node.setType("Điều");
				currentDieu = node;
				if (currentMuc != null) {
					currentMuc.addChild(node);
				} else {
					currentChuong.addChild(node);
				}
				break;
			default:
				try {
					int n = Integer.parseInt(text);
					node.setType("Khoản");
					currentKhoan = node;
					currentDieu.addChild(node);
				} catch (Exception e) {
					text = text.split("\\) ")[0];
					if (text.length() < 2) {
						node.setType("Điểm");
						currentDiem = node;
						currentKhoan.addChild(node);
					} else {
						isValid = false;
						currentNode.setDetails(rawText);
					}
				}

				break;
			}
			if (isValid) {
				if (text.length() != rawText.length()) {
					node.setTitle(rawText.subSequence(text.length() + 1, rawText.length()).toString());
				}
				node.setNumber(text);
				currentNode = node;
			}
		}

		try {
			String xlsxFileName = tt01XlsxFile;
			String vanbanID = tt01VanbanID;
			exportXlS(xlsxFileName, xlsxFileName, nodes);
			String[] tabs = { xlsxFileName };

			// File that auto-generated has some problems with null cells then
			// it causes NullPointerException when generating queries.
			// To fix this temporarily, upload the xlsx file to Google Drive
			// then download it again. Queries will be generated
			// successfully

			// generateQuery(xlsxFileName, tabs, vanbanID);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void getLGTDB() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/LuatGTDB.html");
		ArrayList<WebElement> allRows = getAllRows("//p");
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentChuong = null;
		LawNode currentMuc = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = new LawNode();
		for (WebElement row : allRows) {
			String rawText = row.getText();
			String text = rawText.trim().split("\\. ")[0];
			LawNode node = new LawNode();
			boolean isValid = true;
			switch (text.toLowerCase().split(" ")[0]) {
			case "chương":
				node.setType("Chương");
				currentChuong = node;
				currentMuc = null;
				nodes.add(node);
				break;
			case "mục":
				node.setType("Mục");
				currentMuc = node;
				currentChuong.addChild(node);
				break;
			case "điều":
				node.setType("Điều");
				currentDieu = node;
				if (currentMuc != null) {
					currentMuc.addChild(node);
				} else {
					currentChuong.addChild(node);
				}
				break;
			default:
				try {
					int n = Integer.parseInt(text);
					node.setType("Khoản");
					currentKhoan = node;
					currentDieu.addChild(node);
				} catch (Exception e) {
					text = text.split("\\) ")[0];
					if (text.length() < 2) {
						node.setType("Điểm");
						currentDiem = node;
						currentKhoan.addChild(node);
					} else {
						isValid = false;
						currentNode.setDetails(rawText);
					}
				}

				break;
			}
			if (isValid) {
				if (text.length() != rawText.length()) {
					node.setTitle(rawText.subSequence(text.length() + 1, rawText.length()).toString());
				}
				node.setNumber(text);
				currentNode = node;
			}
		}

		try {
			String xlsxFileName = LuatGTDBXlsxFile;
			String vanbanID = LuatGTDBVanbanID;
			exportXlS(xlsxFileName, xlsxFileName, nodes);
			String[] tabs = { xlsxFileName };

			// File that auto-generated has some problems with null cells then
			// it causes NullPointerException when generating queries.
			// To fix this temporarily, upload the xlsx file to Google Drive
			// then download it again. Queries will be generated
			// successfully

			// generateQuery(xlsxFileName, tabs, vanbanID);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void getLXLVPHC() {
		driver.get("file:///Users/Shared/Jenkins/Desktop/MyStazep/hieuluat/LuatXLVPHC.html");
		ArrayList<WebElement> allRows = getAllRows("//p");
		ArrayList<LawNode> nodes = new ArrayList<>();
		LawNode currentPhan = null;
		LawNode currentChuong = null;
		LawNode currentMuc = null;
		LawNode currentDieu = null;
		LawNode currentKhoan = null;
		LawNode currentDiem = null;
		LawNode currentNode = new LawNode();
		for (WebElement row : allRows) {
			String rawText = row.getText();
			String text = rawText.trim().split("\\. ")[0];
			LawNode node = new LawNode();
			boolean isValid = true;
			switch (text.toLowerCase().split(" ")[0]) {
			case "phần":
				node.setType("Phần");
				currentPhan = node;
				currentChuong = null;
				currentMuc = null;
				nodes.add(node);
				break;
			case "chương":
				node.setType("Chương");
				currentPhan.addChild(node);
				currentChuong = node;
				currentMuc = null;
				break;
			case "mục":
				node.setType("Mục");
				currentMuc = node;
				if (currentChuong != null) {
					currentChuong.addChild(node);
				} else {
					currentPhan.addChild(node);
				}
				break;
			case "điều":
				node.setType("Điều");
				currentDieu = node;
				if (currentMuc != null) {
					currentMuc.addChild(node);
				} else if (currentChuong != null) {
					currentChuong.addChild(node);
				} else {
					currentPhan.addChild(node);
				}
				break;
			default:
				try {
					int n = Integer.parseInt(text);
					node.setType("Khoản");
					currentKhoan = node;
					currentDieu.addChild(node);
				} catch (Exception e) {
					text = text.split("\\) ")[0];
					if (text.length() < 2) {
						node.setType("Điểm");
						currentDiem = node;
						currentKhoan.addChild(node);
					} else {
						isValid = false;
						currentNode.setDetails(rawText);
					}
				}

				break;
			}
			if (isValid) {
				if (text.length() != rawText.length()) {
					node.setTitle(rawText.subSequence(text.length() + 1, rawText.length()).toString());
				}
				node.setNumber(text);
				currentNode = node;
			}
		}

		try {
			String xlsxFileName = LuatXLVPHCXlsxFile;
			String vanbanID = LuatXLVPHCVanbanID;
			exportXlS(xlsxFileName, xlsxFileName, nodes);
			String[] tabs = { xlsxFileName };

			// File that auto-generated has some problems with null cells then
			// it causes NullPointerException when generating queries.
			// To fix this temporarily, upload the xlsx file to Google Drive
			// then download it again. Queries will be generated
			// successfully

			// generateQuery(xlsxFileName, tabs, vanbanID);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void generateQueryFromFiles() {
		// QC41 - 2016
//		String xlsxFileName = qc41XlsxFile;
//		String vanbanID = qc41VanbanID;
		
		//ND46 - 2016
//		String xlsxFileName = nd46XlsxFile;
//		String vanbanID = nd46VanbanID;
		
		//TT01 - 2016
//		String xlsxFileName = tt01XlsxFile;
//		String vanbanID = tt01VanbanID;
		
		//Luat GTDB - 2008
//		String xlsxFileName = LuatGTDBXlsxFile;
//		String vanbanID = LuatGTDBVanbanIDÏ;
		
		//Luat XLVPHC - 2012
		String xlsxFileName = LuatXLVPHCXlsxFile;
		String vanbanID = LuatXLVPHCVanbanID;
		
		String[] tabs = { xlsxFileName };
		generateQuery(xlsxFileName, tabs, vanbanID);
	}

	public void generateQuery(String fileName, String[] tabs, String vanbanID) {
		XLSEditor editor = new XLSEditor(
				Paths.get(System.getProperty("user.dir") + "/exported/" + fileName).toString());
		try {
			for (String tab : tabs) {
				editor.readXLSXFile(tab, vanbanID);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<WebElement> getAllRows() {
		return (ArrayList<WebElement>) (driver.findElements(By.xpath("//div[@dir='ltr']/p")));
	}

	private ArrayList<WebElement> getAllRows(String xpath) {
		return (ArrayList<WebElement>) (driver.findElements(By.xpath(xpath)));
	}

	private void showDetails(ArrayList<LawNode> nodes, String fileName) {

		if (nodes != null) {
			for (LawNode node : nodes) {
				// System.out.println(node.getType());
				// String prefix="";
				// switch (node.getType().toLowerCase()) {
				// case "chương":
				// prefix = " ";
				// break;
				// case "điều":
				// prefix = " ";
				// break;
				// case "khoản":
				// prefix = " ";
				// break;
				// case "điểm":
				// prefix = " ";
				// break;
				// default:
				// break;
				// }
				// System.out.println(prefix+node.getNumber()+":
				// "+node.getTitle());
				// System.out.println(prefix+node.getDetails());

				String text = "";
				text = "<" + node.getType().trim().replace(" ", "") + ">" + "<so><![CDATA[" + node.getNumber()
						+ "]]></so>" + "<tieude><![CDATA[" + node.getTitle() + "]]></tieude>" + "<noidung><![CDATA["
						+ node.getDetails() + "]]></noidung>";
				try {
					exportFile(fileName, text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				showDetails(node.getChildren(), fileName);

				text = "</" + node.getType().trim().replace(" ", "") + ">";
				try {
					exportFile(fileName, text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private void exportFile(String filename, String text) throws IOException {
		Path filePath = Paths.get(System.getProperty("user.dir") + "/exported/" + filename);
		if (!Files.exists(filePath)) {
			Files.createFile(filePath);
			exportFile(filename, "<QC>");
		}
		Files.write(filePath, ("\n" + text).getBytes(), StandardOpenOption.APPEND);
	}

	private void exportXML(ArrayList<LawNode> nodes, String fileName) {
		showDetails(nodes, fileName);
		try {
			exportFile(fileName, "</QC>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void exportXlS(String filename, String sheetName, ArrayList<LawNode> nodes) throws IOException {
		if (nodes != null) {
			Path filePath = Paths.get(System.getProperty("user.dir") + "/exported/" + filename);
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
			XLSEditor xlsFile = new XLSEditor(filePath.toString());
			for (LawNode node : nodes) {
				// System.out.println(filePath.toString());
				xlsFile.addRowToXLSXFile(filePath.toString(), sheetName, node);
				exportXlS(filename, sheetName, node.getChildren());
			}
		}
	}
}
