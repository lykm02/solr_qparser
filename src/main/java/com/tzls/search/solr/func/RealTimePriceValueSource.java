package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.solr.common.SolrException;

/**
 * STOP IT.
 * 
 * */
@Deprecated()
public class RealTimePriceValueSource extends ValueSource {

	private ValueSource salePriceVs;
	private ValueSource promotionVs;
	private ValueSource nextPromotionVs;
	
	
	public RealTimePriceValueSource(ValueSource salePriceVs,ValueSource promotionVs,ValueSource nextPromotionVs){
		if(salePriceVs==null || promotionVs == null || nextPromotionVs == null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for realtimeprice function"
					);
		}
		this.salePriceVs = salePriceVs;
		this.promotionVs = promotionVs;
		this.nextPromotionVs = nextPromotionVs;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues salePriceFvs = salePriceVs.getValues(context, readerContext);
		final FunctionValues promotionFvs = promotionVs.getValues(context, readerContext);
		final FunctionValues nextPromotionFvs = nextPromotionVs.getValues(context, readerContext);
		
		return new DoubleDocValues(this) {
			 @Override
			  public double doubleVal(int doc){
				 double value = promotionFvs.doubleVal(doc);
				 if(value > 0){
					 return value;
				 }
				 value = nextPromotionFvs.doubleVal(doc);
				 if(value > 0){
					 return value;
				 }
				 return salePriceFvs.doubleVal(doc);
			 }
	
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() != o.getClass()) return false;
		RealTimePriceValueSource other = (RealTimePriceValueSource) o;
		return this.salePriceVs.equals(other.salePriceVs)
				&& this.promotionVs.equals(other.promotionVs);
	}

	@Override
	public int hashCode() {
		long combinedHashes;
		combinedHashes = this.salePriceVs.hashCode()
		+ this.promotionVs.hashCode();
		return (int) (combinedHashes ^ (combinedHashes >>> 32));
	}

	@Override
	public String description() {
		return "compute real time price according to SalePrice and promotation info";
	}


}
