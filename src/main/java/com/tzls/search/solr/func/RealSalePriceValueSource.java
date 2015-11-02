package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.solr.common.SolrException;

/**
 * 
 * @author kmchu
 * 
 * 
 * */
public class RealSalePriceValueSource extends ValueSource {
	private ValueSource salePriceVs;
	private ValueSource promotionVs;
	private ValueSource nextPromotionVs;
	private ValueSource prepayVs;
	
	public RealSalePriceValueSource(ValueSource salePriceVs,ValueSource promotionVs,ValueSource nextPromotionVs,ValueSource prepayVs){
		if(salePriceVs==null || promotionVs == null || nextPromotionVs == null || prepayVs == null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for realtimesaleprice function"
					);
		}
		this.salePriceVs = salePriceVs;
		this.promotionVs = promotionVs;
		this.nextPromotionVs = nextPromotionVs;
		this.prepayVs = prepayVs;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues salePriceFvs = salePriceVs.getValues(context, readerContext);
		final FunctionValues promotionFvs = promotionVs.getValues(context, readerContext);
		final FunctionValues nextPromotionFvs = nextPromotionVs.getValues(context, readerContext);
		final FunctionValues prepayFvs = prepayVs.getValues(context, readerContext);
		return new DoubleDocValues(this) {
			 @Override
			  public double doubleVal(int doc){
				 if(promotionFvs.boolVal(doc)){
					 double value = promotionFvs.doubleVal(doc);
					 if(value > 0){
						 return value;
					 }
						 
				 }
				 if(nextPromotionFvs.boolVal(doc)){
					 double value = nextPromotionFvs.doubleVal(doc);
				 	if(value > 0){
				 		return value;
				 	}
				 }
				 
				 double value = prepayFvs.intVal(doc);
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
		RealSalePriceValueSource other = (RealSalePriceValueSource) o;
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
		return "compute real time sale price according to SalePrice and promotation info";
	}


}
