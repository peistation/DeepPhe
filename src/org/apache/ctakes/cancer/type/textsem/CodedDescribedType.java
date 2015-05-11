

/* First created by JCasGen Mon May 11 11:00:51 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** A (super)type that has a String code and a String description
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * XML source: /home/tseytlin/Work/DeepPhe/ctakes-cancer/src/main/resources/org/apache/ctakes/cancer/types/TypeSystem.xml
 * @generated */
public class CodedDescribedType extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CodedDescribedType.class);
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
  protected CodedDescribedType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CodedDescribedType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CodedDescribedType(JCas jcas) {
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
  //* Feature: code

  /** getter for code - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCode() {
    if (CodedDescribedType_Type.featOkTst && ((CodedDescribedType_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "org.apache.ctakes.cancer.type.textsem.CodedDescribedType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CodedDescribedType_Type)jcasType).casFeatCode_code);}
    
  /** setter for code - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCode(String v) {
    if (CodedDescribedType_Type.featOkTst && ((CodedDescribedType_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "org.apache.ctakes.cancer.type.textsem.CodedDescribedType");
    jcasType.ll_cas.ll_setStringValue(addr, ((CodedDescribedType_Type)jcasType).casFeatCode_code, v);}    
   
    
  //*--------------*
  //* Feature: description

  /** getter for description - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDescription() {
    if (CodedDescribedType_Type.featOkTst && ((CodedDescribedType_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "org.apache.ctakes.cancer.type.textsem.CodedDescribedType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CodedDescribedType_Type)jcasType).casFeatCode_description);}
    
  /** setter for description - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDescription(String v) {
    if (CodedDescribedType_Type.featOkTst && ((CodedDescribedType_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "org.apache.ctakes.cancer.type.textsem.CodedDescribedType");
    jcasType.ll_cas.ll_setStringValue(addr, ((CodedDescribedType_Type)jcasType).casFeatCode_description, v);}    
  }

    