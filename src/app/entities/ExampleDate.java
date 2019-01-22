package app.entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExampleDate {
	
	public static String myDayOfWeek(int dayOfWeek) {
//		System.out.println(dayOfWeek);
		String dOW = "";
		switch (dayOfWeek) {
		case 2:
			return dOW = "пон";
		case 3:
			return dOW = "вт";
		case 4:
			return dOW = "ср";
		case 5:
			return dOW = "четв";
		case 6:
			return dOW = "пет";
		case 7:
			return dOW = "съб";
		case 1:
			return dOW = "нед";

		default:
			return null;
		}
		 
	}
	
	
	public static void main(String[] args) {
/*		
		Calendar calendar = Calendar.getInstance();
		int year       = calendar.get(Calendar.YEAR);
		int month      = calendar.get(Calendar.MONTH); // Jan = 0, dec = 11
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
		int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		int weekOfMonth= calendar.get(Calendar.WEEK_OF_MONTH);

		int hour       = calendar.get(Calendar.HOUR);        // 12 hour clock
		int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		int minute     = calendar.get(Calendar.MINUTE);
		int second     = calendar.get(Calendar.SECOND);
		int millisecond= calendar.get(Calendar.MILLISECOND);

		System.out.println(calendar.getTime());

		System.out.println("year \t\t: " + year);
		System.out.println("month \t\t: " + month);
		System.out.println("dayOfMonth \t: " + dayOfMonth);
		System.out.println("dayOfWeek \t: " + dayOfWeek);
		System.out.println("weekOfYear \t: " + weekOfYear);
		System.out.println("weekOfMonth \t: " + weekOfMonth);

		System.out.println("hour \t\t: " + hour);
		System.out.println("hourOfDay \t: " + hourOfDay);
		System.out.println("minute \t\t: " + minute);
		System.out.println("second \t\t: " + second);
		System.out.println("millisecond \t: " + millisecond);
		*/
		
		Calendar calendar = Calendar.getInstance();
		
		SimpleDateFormat myDate = new SimpleDateFormat("d.M.yyyy");
		System.out.println(myDate.format(calendar.getTime()));
		
		SimpleDateFormat myTime = new SimpleDateFormat("HH:mm");
		System.out.println(myTime.format(calendar.getTime()));
		
		int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println(myDayOfWeek(dayOfWeek));
	}
}
