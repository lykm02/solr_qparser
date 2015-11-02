package com.tzls.search.solr.func;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.util.DateMathParser;

public class PromotionPriceValueSourceParser extends ValueSourceParser{
	private  String effectDateString = "EffectDate";
	private  String expireDateString = "ExpireDate";
	private  String promotionModeString = "PromotionMode";
	private  String promotionValueString = "PromotionValue";
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList list){
		 String effectFieldName  = (String)list.get("EffectDateFieldName");
		 if(effectFieldName!=null && !"".equals(effectFieldName.trim())){
			 effectDateString = effectFieldName;
		 }
		 String expireFieldName  = (String)list.get("ExpireDateFieldName");
		 if(expireFieldName!=null && !"".equals(expireFieldName.trim())){
			 expireDateString = expireFieldName;
		 }
		 String promotionModeFieldName  = (String)list.get("PromotionModeFieldName");
		 if(promotionModeFieldName!=null && !"".equals(promotionModeFieldName.trim())){
			 promotionModeString = promotionModeFieldName;
		 }
		 String promotionValueFieldName  = (String)list.get("PromotionValueFieldName");
		 if(promotionValueFieldName!=null && !"".equals(promotionValueFieldName.trim())){
			 promotionValueString = promotionValueFieldName;
		 }
	}
	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		ValueSource salePriceVs = fqp.parseValueSource();
		ValueSource promotionModeVs = RealPriceValueSourceParser.getFieldValueSource(fqp,promotionModeString);
		ValueSource promotionValueVs = RealPriceValueSourceParser.getFieldValueSource(fqp,promotionValueString);
		ValueSource effectDateValueSource = RealPriceValueSourceParser.getValueSource(fqp,effectDateString);
		ValueSource expireDateValueSource = RealPriceValueSourceParser.getValueSource(fqp,expireDateString);
		DateMathParser p = new DateMathParser();
		long time = p.getNow().getTime();
		return new PromotionPriceValueSource(salePriceVs, promotionModeVs, promotionValueVs, effectDateValueSource, expireDateValueSource, time);
	}

}
