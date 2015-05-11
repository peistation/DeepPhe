

/* First created by JCasGen Mon May 11 11:00:52 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Value set specific to stage type
 * Updated by JCasGen Mon May 11 11:00:52 EDT 2015
 * XML source: /home/tseytlin/Work/DeepPhe/ctakes-cancer/src/main/resources/org/apache/ctakes/cancer/types/TypeSystem.xml
 * @generated */
public class TnmStage extends CodedDescribedType {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TnmStage.class);
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
  protected TnmStage() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TnmStage(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TnmStage(JCas jcas) {
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
  //* Feature: prefix

  /** getter for prefix - gets 
   * @generated
   * @return value of the feature 
   */
  public TnmStagePrefix getPrefix() {
    if (TnmStage_Type.featOkTst && ((TnmStage_Type)jcasType).casFeat_prefix == null)
      jcasType.jcas.throwFeatMissing("prefix", "org.apache.ctakes.cancer.type.textsem.TnmStage");
    return (TnmStagePrefix)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TnmStage_Type)jcasType).casFeatCode_prefix)));}
    
  /** setter for prefix - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPrefix(TnmStagePrefix v) {
    if (TnmStage_Type.featOkTst && ((TnmStage_Type)jcasType).casFeat_prefix == null)
      jcasType.jcas.throwFeatMissing("prefix", "org.apache.ctakes.cancer.type.textsem.TnmStage");
    jcasType.ll_cas.ll_setRefValue(addr, ((TnmStage_Type)jcasType).casFeatCode_prefix, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (TnmStage_Type.featOkTst && ((TnmStage_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.TnmStage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TnmStage_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (TnmStage_Type.featOkTst && ((TnmStage_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.TnmStage");
    jcasType.ll_cas.ll_setStringValue(addr, ((TnmStage_Type)jcasType).casFeatCode_value, v);}    
  }

    