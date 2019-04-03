package app.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import app.entities.ExampleDate;
import app.entities.GMRowEntry;
import app.entities.MyCellStyles;
import app.entities.MyColumn;
import app.entities.Utils;

public class ExcelController {
	
	private static ExcelController instance;
    
	public ExcelController(){}
    
    public static ExcelController getInstance(){
        if(instance == null){
            instance = new ExcelController();
        }
        return instance;
    }
	
	public void writeInExcel(Workbook wb, GMRowEntry rowEntry) throws IOException {
		
		MyCellStyles myCellStylesRegular = new MyCellStyles(wb);
		Map<String, CellStyle> myCellStyles = new HashMap<String, CellStyle>();
		myCellStyles = myCellStylesRegular.getMyCellStyles();
			
		Sheet worksheet = wb.getSheet("Monero");


		int newRow = 0;
		for (Row row : worksheet) {
			for (Cell cell : row) {
				if (cell.getCellType() != Cell.CELL_TYPE_BLANK
						&& (cell.getCellType() != Cell.CELL_TYPE_STRING
						|| cell.getStringCellValue().length() > 0)) {
					newRow++;
					break;
				}
			}
		}

		// Create classes for columns
		MyColumn numR 			= new MyColumn("A", 0);
		MyColumn dateCol 		= new MyColumn("B", 1);
		MyColumn timeCol 		= new MyColumn("C", 2);
		MyColumn dowCol 		= new MyColumn("D", 3);
		MyColumn XMR_DayCol		= new MyColumn("E", 4);
		MyColumn hash_SecCol	= new MyColumn("F", 5);
		MyColumn XMR_1HSCol		= new MyColumn("G", 6);
		MyColumn XMR_USDCol		= new MyColumn("H", 7);
		MyColumn diffPrivCol	= new MyColumn("I", 8);
		MyColumn USD_DayCol		= new MyColumn("J", 9);
		MyColumn emptyCol	 	= new MyColumn("K", 10);

		MyColumn marketCap	 	= new MyColumn("L", 11);
		MyColumn volume	 		= new MyColumn("M", 12);
		MyColumn circSupply	 	= new MyColumn("N", 13);

		MyColumn blocksCol 		= new MyColumn("O", 14);
		MyColumn netwHashRateCol= new MyColumn("P", 15);
		MyColumn difficultyCol	= new MyColumn("Q", 16);

		// declare a Cell object
		String[] colNames = {
				"numR ", "dateCol", "timeCol", "dowCol", "XMR_DayCol",
				"hash_SecCol", "XMR_1HSCol", "XMR_USDCol",
				"diffPrivCol", "USD_DayCol", "emptyCol",
				"marketCap", "volume", "circSupply",
				"blocksCol", "netwHashRateCol", "difficultyCol"
				};
		List<Cell> cellList = new ArrayList<Cell>(colNames.length);
		Row lRow = worksheet.createRow(newRow);
		for (int i = 0; i < colNames.length; i++) {
			cellList.add(lRow.createCell(i));
		}
		
		// Column B Date(1)
		cellList.get(numR.getColNum()).setCellStyle(myCellStyles.get("csDef"));
		cellList.get(numR.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
		cellList.get(numR.getColNum()).setCellFormula(numR.getColChar() + (newRow) + "+ 1");
	
		
		
		// Column B Date(1)
		Calendar calendar = Calendar.getInstance();
		Date curDate = new Date();
		cellList.get(dateCol.getColNum()).setCellStyle(myCellStyles.get("csDateRight"));
		cellList.get(dateCol.getColNum()).setCellValue(curDate);

		// Column C Time(2)
		SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm");
		cellList.get(timeCol.getColNum()).setCellStyle(myCellStyles.get("csHour"));
		cellList.get(timeCol.getColNum()).setCellValue(myTimeFormat.format(calendar.getTime()));

		// Column D DoW(3)
		int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
		cellList.get(dowCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
		cellList.get(dowCol.getColNum()).setCellValue(ExampleDate.myDayOfWeek(dayOfWeek));

		// Column E XMR_Day(4)
		// fill manually 2 days behind

		// Column F H/s(5)
		cellList.get(hash_SecCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
		cellList.get(hash_SecCol.getColNum()).setCellValue(1300);

		// Column G hash_Sec(6)
		cellList.get(XMR_1HSCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
		cellList.get(XMR_1HSCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
		cellList.get(XMR_1HSCol.getColNum()).setCellFormula(XMR_DayCol.getColChar() + (newRow + 1) + "/" + hash_SecCol.getColChar() + (newRow + 1));
	

		// Column H XMR_USD(7)
		cellList.get(XMR_USDCol.getColNum()).setCellStyle(myCellStyles.get("csUSD"));
		cellList.get(XMR_USDCol.getColNum()).setCellValue(Double.parseDouble(rowEntry.getXMR_USD()));

		// Column I diffPriv(8)
			cellList.get(diffPrivCol.getColNum()).setCellStyle(myCellStyles.get("csPerc"));
			cellList.get(diffPrivCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
			cellList.get(diffPrivCol.getColNum()).setCellFormula(XMR_USDCol.getColChar() + (newRow + 1) + "/" + XMR_USDCol.getColChar() + (newRow) + "-1");

		// Column J USD_Day(9)
			cellList.get(USD_DayCol.getColNum()).setCellStyle(myCellStyles.get("csUSD"));
			cellList.get(USD_DayCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
			cellList.get(USD_DayCol.getColNum()).setCellFormula(XMR_DayCol.getColChar() + (newRow + 1) + "*" + XMR_USDCol.getColChar() + (newRow + 1));


		// Column K empty(10)
			
		// Column L Market Cap(11)
		cellList.get(marketCap.getColNum()).setCellStyle(myCellStyles.get("csUSDSep"));
		cellList.get(marketCap.getColNum()).setCellValue(rowEntry.getMarketCap());
		
		// Column M Volume(12)
		cellList.get(volume.getColNum()).setCellStyle(myCellStyles.get("csUSDSep"));
		cellList.get(volume.getColNum()).setCellValue(rowEntry.getVolume());
		
		// Column N Circulating Supply(13)
		cellList.get(circSupply.getColNum()).setCellStyle(myCellStyles.get("csUSDSep"));
		cellList.get(circSupply.getColNum()).setCellValue(rowEntry.getCirculatingSupply());
		
		// Column O block(14)
		cellList.get(blocksCol.getColNum()).setCellStyle(myCellStyles.get("csUSDSep"));
		cellList.get(blocksCol.getColNum()).setCellValue(rowEntry.getBlocks());

		// Column P netwHashRate(15)
		cellList.get(netwHashRateCol.getColNum()).setCellStyle(myCellStyles.get("csUSD"));
		Double temp = Double.parseDouble(rowEntry.getNetwHashRate());
		Double nhr = (temp > 10.0) ? temp : (temp * 1000);
		cellList.get(netwHashRateCol.getColNum()).setCellValue(nhr);
		
		// Column Q difficulty(16)
		cellList.get(difficultyCol.getColNum()).setCellStyle(myCellStyles.get("csUSDSep"));
		cellList.get(difficultyCol.getColNum()).setCellValue(rowEntry.getDifficulty());

	}

	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Start Program");
		long startTime = System.currentTimeMillis();
		
		String path = "D:\\GenesisMinig_temp.xlsx";
		
		File myFile = new File(path);
		FileInputStream fsIP = new FileInputStream(myFile);
				
		//Access the workbook                  
		Workbook wb = new XSSFWorkbook(fsIP);
		
		/*GMRowEntry rowEntry = new GMRowEntry(
				"379.42",
				"1469916",
				"451.30",
				"54155462791");*/
		WebSitesParser parser = new WebSitesParser();
		parser.getMoneroInfo();
		
		ExcelController contoller = new ExcelController();
		contoller.writeInExcel(wb, parser.getRowEntry());
		
		fsIP.close();
		
		//Open FileOutputStream to write updates
		FileOutputStream output_file =new FileOutputStream(path);  
		
		//write changes
		wb.write(output_file);
		
		//close the stream
		output_file.close();
		
		long endTime   = System.currentTimeMillis();
		System.err.println(Utils.duration(startTime, endTime));
		System.out.println("Done!!!");
	}

}
