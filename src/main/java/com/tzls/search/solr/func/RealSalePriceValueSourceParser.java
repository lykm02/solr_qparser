package com.tzls.search.solr.func;


import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.util.DateMathParser;

public class RealSalePriceValueSourceParser extends ValueSourceParser{
	private  String effectDateString = "EffectDate";
	private  String expireDateString = "ExpireDate";
	private  String nextEffectDateString = "NextEffectDate";
	private  String nextExpireDateString = "NextEffectDate";
	
	private  String promotionSalePriceFieldName = "PromotionSalePrice";
	private  String nextPromotionSalePriceFieldName = "NextPromotionSalePrice";
	private  boolean enableNext = true ;
	
	private  String prepayEffectDateFn = "EarnestStartTime";
	private  String prepayExpireDateFn = "EarnestEndTime";
	private  String prepaySalePriceFieldName  = "EarnestAmount"; 
	private  String isPrepayFieldName = "IsEarnest";
	
	
	/**
	  * Initialize the plugin.
	  */
	@Override
	public void init(@SuppressWarnings("rawtypes") NamedList args) {
		 String effectFieldName  = (String)args.get("EffectDateFieldName");
		 if(effectFieldName!=null && !"".equals(effectFieldName.trim())){
			 effectDateString = effectFieldName;
		 }
		 String expireFieldName  = (String)args.get("ExpireDateFieldName");
		 if(expireFieldName!=null && !"".equals(expireFieldName.trim())){
			 expireDateString = expireFieldName;
		 }
		 String nextEffectFieldName  = (String)args.get("NextEffectDateFieldName");
		 if(nextEffectFieldName!=null && !"".equals(nextEffectFieldName.trim())){
			 nextEffectDateString = nextEffectFieldName;
		 }
		 String nextExpireFieldName  = (String)args.get("NextExpireDateFieldName");
		 if(nextExpireFieldName!=null && !"".equals(nextExpireFieldName.trim())){
			 nextExpireDateString = nextExpireFieldName;
		 }
		 String promotionSalePriceFieldName  = (String)args.get("PromotionSalePriceFieldName");
		 if(promotionSalePriceFieldName!=null && !"".equals(promotionSalePriceFieldName.trim())){
			 this.promotionSalePriceFieldName = promotionSalePriceFieldName;
		 }
		 
		 String nextPromotionSalePriceFieldName  = (String)args.get("NextPromotionSalePriceFieldName");
		 if(nextPromotionSalePriceFieldName != null && !"".equals(nextPromotionSalePriceFieldName.trim())){
			 this.nextPromotionSalePriceFieldName = nextPromotionSalePriceFieldName;
		 }
		
		 String enableNextOpts  = (String)args.get("EnableNext");
		 if(enableNextOpts != null && "false".equals(enableNextOpts.trim())){
			 enableNext = false;
		 }
	}
	
	
	
	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		ValueSource salePriceValueSource = fqp.parseValueSource();
		DateMathParser p = new DateMathParser();
		long time = p.getNow().getTime();
		ValueSource promotionSalePriceVs = getFieldValueSource(fqp,promotionSalePriceFieldName);
		ValueSource nextPromotionSalePriceVs = getFieldValueSource(fqp,nextPromotionSalePriceFieldName);
		//effectDateValueSource
		ValueSource effectDateValueSource = getValueSource(fqp,effectDateString);
		//ExpireDateValueSource
		ValueSource expireDateValueSource = getValueSource(fqp,expireDateString);
		//value
		PromotionSalePriceValueSource promotionPriceValueSource = new PromotionSalePriceValueSource(promotionSalePriceVs,effectDateValueSource, expireDateValueSource, time);
		
		ValueSource nextPromotionPriceValueSource = null;
		if(enableNext){

			//next
			ValueSource nextEffectDateValueSource = getValueSource(fqp,nextEffectDateString);
			//next
			ValueSource nextExpireDateValueSource = getValueSource(fqp,nextExpireDateString);
			nextPromotionPriceValueSource = new PromotionSalePriceValueSource(nextPromotionSalePriceVs, nextEffectDateValueSource, nextExpireDateValueSource, time);
		}else{
			nextPromotionPriceValueSource = new FalsePriceValueSource();
		}
		ValueSource prepaySalePriceFvs = getFieldValueSource(fqp, prepaySalePriceFieldName);
		ValueSource prepayEffectDateValueSource = getValueSource(fqp,prepayEffectDateFn);
		ValueSource prepayExpireDateValueSource = getValueSource(fqp,prepayExpireDateFn);
		
		ValueSource isPrepayValueSource = getFieldValueSource(fqp, isPrepayFieldName);
		PrepaySalePriceValueSource prepaySalePriceVs = new PrepaySalePriceValueSource(prepaySalePriceFvs, prepayEffectDateValueSource, prepayExpireDateValueSource, time,isPrepayValueSource);
		return new RealSalePriceValueSource(salePriceValueSource,promotionPriceValueSource,nextPromotionPriceValueSource,prepaySalePriceVs);
	}
	
	
	//cannot use datavaluesourceparser for package invisible
//	private static ValueSource genDateTypeValueSource(String fieldName,FunctionQParser fqp){
//		ValueSourceParser vsp = ValueSourceParser.standardValueSourceParsers.get("ms");
//		ValueSourceParser.DateValueSourceParser dvsp = (ValueSourceParser.DateValueSourceParser)vsp;
//	}
	
	  public static ValueSource getValueSource(FunctionQParser fp, String arg) {
		    if (arg == null) return null;
		    SchemaField f = fp.getReq().getSchema().getField(arg);
		    if (f.getType().getClass() == DateField.class) {
		      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Can't use realPrice() function on non-numeric legacy date field " + arg);
		    }
		    return f.getType().getValueSource(f, fp);
	  }
	  
	  public static ValueSource getFieldValueSource(FunctionQParser fp, String fieldName){
		  if(fieldName == null) {
			  return null;
		  }
		  SchemaField f = fp.getReq().getSchema().getField(fieldName);
		  return f.getType().getValueSource(f, fp);
	  }
}
