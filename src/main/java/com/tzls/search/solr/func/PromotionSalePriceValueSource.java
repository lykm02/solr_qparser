package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
//import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.solr.common.SolrException;
/**
 * Promotion Sale Price if date in range(effectDate, expireDate)
 * 
 * */
public class PromotionSalePriceValueSource extends ValueSource {
	private ValueSource promotionSalePriceVs;
	private ValueSource effectDateLong;
	private ValueSource expireDateLong;
	private long time;
	
	public PromotionSalePriceValueSource(ValueSource salePriceVs,ValueSource effectDateLong,ValueSource expireDateLong,long now){
		if(salePriceVs==null || expireDateLong == null|| effectDateLong ==null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for promotionPrice function"
					);
		}
		this.promotionSalePriceVs = salePriceVs;
		this.effectDateLong = effectDateLong;
		this.expireDateLong = expireDateLong;
		this.time = now;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues salePriceFvs = promotionSalePriceVs.getValues(context, readerContext);
		final FunctionValues effectDateFvs = effectDateLong.getValues(context, readerContext);
		final FunctionValues expireDateFvs = expireDateLong.getValues(context, readerContext);
		
		return new FunctionValues(){
			
			@Override 
			public boolean boolVal(int doc){
				 long effectDateTimes = effectDateFvs.longVal(doc);
				 long expireDateTimes = expireDateFvs.longVal(doc);
				 if(time > expireDateTimes || time < effectDateTimes){
					 return false;
				 }
				 return true;
			}
			
			@Override
			public int intVal(int doc){
				return (int)Math.ceil(doubleVal(doc));
			}
			
			@Override
			public double doubleVal(int doc){
				return salePriceFvs.doubleVal(doc);
			}

			@Override
			public String toString(int doc) {
				return "promotion sale price  = " + intVal(doc);
			}
			
		};
		
		/*
		 return new DoubleDocValues(this) {
			 @Override
			  public double doubleVal(int doc){
				 int mod = modVs.intVal(doc);
				 if(mod<1 || mod >2){
					 return -1d;
				 }
				 long effectDateTimes = effectDateFvs.longVal(doc);
				 long expireDateTimes = expireDateFvs.longVal(doc);
				 if(time < effectDateTimes || time > expireDateTimes){
					 return -1d;
				 }
				 if(mod == 1){
					 return salePriceFvs.doubleVal(doc) - valueVs.doubleVal(doc);
				 }
				 return salePriceFvs.doubleVal(doc)*valueVs.doubleVal(doc)/100;
			 }
	
		};
		*/
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() != o.getClass()) return false;
		PromotionSalePriceValueSource other = (PromotionSalePriceValueSource) o;
		return this.time==other.time && this.promotionSalePriceVs.equals(other.promotionSalePriceVs)				
				&&this.expireDateLong.equals(other.expireDateLong)
				&&this.effectDateLong.equals(other.effectDateLong);
	}

	@Override
	public int hashCode() {
		long combinedHashes;
		combinedHashes = this.promotionSalePriceVs.hashCode()
		+ this.time;
		return (int) (combinedHashes ^ (combinedHashes >>> 32));
	}

	@Override
	public String description() {
		return "compute real time price according to SalePrice and promotation info";
	}

	public static boolean range(long val, long start, long end){
		return false;
	}
}
