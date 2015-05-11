

/* First created by JCasGen Mon May 11 11:00:51 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Value set
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * XML source: /home/tseytlin/Work/DeepPhe/ctakes-cancer/src/main/resources/org/apache/ctakes/cancer/types/TypeSystem.xml
 * @generated */
public class TnmOption extends CodedDescribedType {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TnmOption.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TnmOption() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TnmOption(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TnmOption(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public int getValue() {
    if (TnmOption_Type.featOkTst && ((TnmOption_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    return jcasType.ll_cas.ll_getIntValue(addr, ((TnmOption_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(int v) {
    if (TnmOption_Type.featOkTst && ((TnmOption_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    jcasType.ll_cas.ll_setIntValue(addr, ((TnmOption_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: certainty

  /** getter for certainty - gets 
   * @generated
   * @return value of the feature 
   */
  public int getCertainty() {
    if (TnmOption_Type.featOkTst && ((TnmOption_Type)jcasType).casFeat_certainty == null)
      jcasType.jcas.throwFeatMissing("certainty", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    return jcasType.ll_cas.ll_getIntValue(addr, ((TnmOption_Type)jcasType).casFeatCode_certainty);}
    
  /** setter for certainty - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCertainty(int v) {
    if (TnmOption_Type.featOkTst && ((TnmOption_Type)jcasType).casFeat_certainty == null)
      jcasType.jcas.throwFeatMissing("certainty", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    jcasType.ll_cas.ll_setIntValue(addr, ((TnmOption_Type)jcasType).casFeatCode_certainty, v);}    
  }

    