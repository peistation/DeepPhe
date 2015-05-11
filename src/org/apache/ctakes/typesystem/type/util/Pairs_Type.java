
/* First created by JCasGen Mon May 11 11:00:52 EDT 2015 */
package org.apache.ctakes.typesystem.type.util;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** A brute force "hash" that stores multiple Pairs in a list. 
Equivalent to cTAKES: edu.mayo.bmi.uima.core.type.Properties
 * Updated by JCasGen Mon May 11 11:00:52 EDT 2015
 * @generated */
public class Pairs_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Pairs_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Pairs_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Pairs(addr, Pairs_Type.this);
  			   Pairs_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Pairs(addr, Pairs_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Pairs.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.util.Pairs");
 
  /** @generated */
  final Feature casFeat_pairs;
  /** @generated */
  final int     casFeatCode_pairs;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getPairs(int addr) {
        if (featOkTst && casFeat_pairs == null)
      jcas.throwFeatMissing("pairs", "org.apache.ctakes.typesystem.type.util.Pairs");
    return ll_cas.ll_getRefValue(addr, casFeatCode_pairs);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPairs(int addr, int v) {
        if (featOkTst && casFeat_pairs == null)
      jcas.throwFeatMissing("pairs", "org.apache.ctakes.typesystem.type.util.Pairs");
    ll_cas.ll_setRefValue(addr, casFeatCode_pairs, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getPairs(int addr, int i) {
        if (featOkTst && casFeat_pairs == null)
      jcas.throwFeatMissing("pairs", "org.apache.ctakes.typesystem.type.util.Pairs");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pairs), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_pairs), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pairs), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setPairs(int addr, int i, int v) {
        if (featOkTst && casFeat_pairs == null)
      jcas.throwFeatMissing("pairs", "org.apache.ctakes.typesystem.type.util.Pairs");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pairs), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_pairs), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_pairs), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Pairs_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pairs = jcas.getRequiredFeatureDE(casType, "pairs", "uima.cas.FSArray", featOkTst);
    casFeatCode_pairs  = (null == casFeat_pairs) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pairs).getCode();

  }
}



    