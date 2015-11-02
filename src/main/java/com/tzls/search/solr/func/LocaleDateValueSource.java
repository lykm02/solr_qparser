package com.tzls.search.solr.func;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.StrDocValues;
import org.apache.solr.common.SolrException;

public class LocaleDateValueSource extends ValueSource {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ValueSource dateText;
	private ValueSource timeZone;
	
	public LocaleDateValueSource(ValueSource timeZone,ValueSource dateText){
		if(timeZone==null || dateText == null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for localedate function"
					);
		}
		this.timeZone = timeZone;
		this.dateText = dateText;
	}
	
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues timeZoneField = timeZone.getValues(context, readerContext);
		final FunctionValues dateTextField = dateText.getValues(context, readerContext);
		
		return new StrDocValues(this) {
			
			@Override
			public String strVal(int doc) {
				return convert(dateTextField.strVal(doc),timeZoneField.strVal(doc));
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() != o.getClass()) return false;
		LocaleDateValueSource other = (LocaleDateValueSource) o;
		return this.dateText.equals(other.dateText)
				&& this.timeZone.equals(other.timeZone);
	}

	@Override
	public int hashCode() {
		long combinedHashes;
		combinedHashes = this.dateText.hashCode()
		+ this.timeZone.hashCode();
		return (int) (combinedHashes ^ (combinedHashes >>> 32));
	}

	@Override
	public String description() {
		return "convert China/Beijing date (yyyy-MM-dd HHmmss) to Locale date";
	}

	public static String convert(String date, String timeZoneText){
		Date current = Calendar.getInstance().getTime();
		try {
			current = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(timeZoneText == null || timeZoneText=="" ||"+0800".equals(timeZoneText)){
			return dateFormat.format(current);
		}else{
			//
			TimeZone timeZone3 = TimeZone.getTimeZone("GMT"+timeZoneText);
			Calendar cal = Calendar.getInstance(timeZone3);
			cal.setTime(current);
			return cal.get(Calendar.YEAR)+"-"+String.format("%02d", (cal.get(Calendar.MONTH))+1)+"-"+String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		}
	}
}
