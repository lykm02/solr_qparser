package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.solr.common.SolrException;

/**
 * 
 * 
 * */
public class PrepaySalePriceValueSource extends ValueSource {
	private ValueSource effectDateLong;
	private ValueSource expireDateLong;
	public long time;
	private ValueSource prepaySalePriceVs;
	private ValueSource isPrePayVs;
	
	public PrepaySalePriceValueSource(ValueSource salePriceVs,ValueSource effectDateLong,ValueSource expireDateLong,long now,ValueSource isPrePayVs){
		if(salePriceVs==null || expireDateLong == null|| effectDateLong ==null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for prepay sale price function"
					);
		}
		this.isPrePayVs = isPrePayVs;
		this.prepaySalePriceVs = salePriceVs;
		this.effectDateLong = effectDateLong;
		this.expireDateLong = expireDateLong;
		this.time = now;
	}
	
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues effectDateLongFvs = effectDateLong.getValues(context, readerContext);
		final FunctionValues expireDateLongFvs = expireDateLong.getValues(context, readerContext);
		final FunctionValues prepaySalePriceFvs = prepaySalePriceVs.getValues(context, readerContext);
		final FunctionValues isPrePayFvs = isPrePayVs.getValues(context, readerContext);
		
		return new IntDocValues(this) {
			
			@Override
			public int intVal(int doc) {
				if(isPrePayFvs.intVal(doc) < 1){
					return -1;
				}
				if(PrepaySalePriceValueSource.this.time < effectDateLongFvs.longVal(doc) ){
					return -1;
				}
				if(PrepaySalePriceValueSource.this.time > expireDateLongFvs.longVal(doc) ){
					return -1;
				}
				return (int)Math.ceil(prepaySalePriceFvs.doubleVal(doc));
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() != o.getClass()) return false;
		PrepaySalePriceValueSource other = (PrepaySalePriceValueSource) o;
		return this.time==other.time && this.prepaySalePriceVs.equals(other.prepaySalePriceVs)				
				&&this.expireDateLong.equals(other.expireDateLong)
				&&this.effectDateLong.equals(other.effectDateLong);
	}

	@Override
	public int hashCode() {
		long combinedHashes;
		combinedHashes = this.prepaySalePriceVs.hashCode()
		+ this.time;
		return (int) (combinedHashes ^ (combinedHashes >>> 32));
	}

	@Override
	public String description() {
		return "prepay sale price compute.";
	}

}
