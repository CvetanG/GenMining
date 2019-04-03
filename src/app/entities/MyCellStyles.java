package app.entities;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;

public class MyCellStyles {
	
	private Map<String, CellStyle> cellStyles;
	
	private Workbook wb;
	
	public MyCellStyles(Workbook wb) {
		cellStyles = new HashMap<>();
		
		CreationHelper createHelper = wb.getCreationHelper();
		
		CellStyle csDateRight = wb.createCellStyle();
		csDateRight.setAlignment(CellStyle.ALIGN_RIGHT);
		csDateRight.setDataFormat(
				createHelper.createDataFormat().getFormat("d.m.yyyy"));
		
		CellStyle csHour = wb.createCellStyle();
		csHour.setDataFormat((short)14);
		csHour.setAlignment(CellStyle.ALIGN_RIGHT);
		csHour.setDataFormat(
				createHelper.createDataFormat().getFormat("HH:MM"));
		
		CellStyle csPerc = wb.createCellStyle();
		csPerc.setDataFormat((short)10);
		
		CellStyle csUSD = wb.createCellStyle();
		csUSD.setAlignment(CellStyle.ALIGN_CENTER);
		csUSD.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
		
		CellStyle csUSDSep = wb.createCellStyle();
		csUSDSep.setAlignment(CellStyle.ALIGN_CENTER);
		csUSDSep.setDataFormat(createHelper.createDataFormat().getFormat("# ### ### ###"));

		CellStyle csDef = wb.createCellStyle();
		
		cellStyles.put("csDateRight", csDateRight);
		cellStyles.put("csHour", csHour);
		cellStyles.put("csPerc", csPerc);
		cellStyles.put("csUSD", csUSD);
		cellStyles.put("csUSDSep", csUSDSep);
		cellStyles.put("csDef", csDef);
		
	}

	public Map<String, CellStyle> getMyCellStyles() {
		return cellStyles;
	}

	public void setMyCellStyles(Map<String, CellStyle> myCellStyles) {
		this.cellStyles = myCellStyles;
	}

	public Workbook getWb() {
		return wb;
	}

	public void setWb(Workbook wb) {
		this.wb = wb;
	}

}
