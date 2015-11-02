package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.BoolDocValues;
import org.apache.solr.common.SolrException;
/**
 *  Range function.
 * 
 * */
public class RangeValueSource extends ValueSource {
	private ValueSource variableVs;
	private ValueSource leftVs;
	private ValueSource rightVs;
	private int leftInc;
	private int rightInc;
	
	
	public RangeValueSource(ValueSource variableVs,ValueSource leftVs, ValueSource rightVs, int leftInc, int rightInc){
		if(variableVs==null || leftVs == null || rightVs == null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for range function"
					);
		}
		this.variableVs = variableVs;
		this.leftVs = leftVs;
		this.rightVs = rightVs;
		this.leftInc = leftInc;
		this.rightInc = rightInc;
	}
	
	public RangeValueSource(ValueSource variableVs,ValueSource leftVs, ValueSource rightVs){
		this(variableVs,leftVs,rightVs,1,1);
	}
	
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues variableFvs = variableVs.getValues(context, readerContext);
		final FunctionValues leftFvs = leftVs.getValues(context, readerContext);
		final FunctionValues rightFvs = rightVs.getValues(context, readerContext);
		
		return new BoolDocValues(this) {

			@Override
			public boolean boolVal(int doc) {
				double vDoc = variableFvs.doubleVal(doc);
				double lDoc = leftFvs.doubleVal(doc);
				double rDoc = rightFvs.doubleVal(doc);
				if(leftInc == 0){
					if(vDoc <= lDoc){
						return false;
					}
				}
				if(leftInc == 1 && vDoc <lDoc){
					return false;
				}
				if(rightInc == 0 && vDoc >= rDoc){
					return false;
				}
				if(rightInc == 1 && vDoc > rDoc ){
					return false;
				}
				return true;
			}
			

		};
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() != o.getClass()) return false;
		RangeValueSource other = (RangeValueSource) o;
		return this.variableVs.equals(other.variableVs)
				&& this.leftVs.equals(other.leftVs)
				&& this.rightVs.equals(other.rightVs)
				&& this.leftInc == other.leftInc
				&& this.rightInc == other.rightInc;
	}

	@Override
	public int hashCode() {
		long combinedHashes;
		combinedHashes = this.variableVs.hashCode()
		+ this.leftVs.hashCode();
		return (int) (combinedHashes ^ (combinedHashes >>> 32));
	}

	@Override
	public String description() {
		return "compute range ";
	}


}
