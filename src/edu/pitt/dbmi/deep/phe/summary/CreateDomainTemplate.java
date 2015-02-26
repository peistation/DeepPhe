package edu.pitt.dbmi.deep.phe.summary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import edu.pitt.dbmi.nlp.noble.extract.model.Template;
import edu.pitt.dbmi.nlp.noble.extract.model.TemplateCreator;
import edu.pitt.dbmi.nlp.noble.extract.model.TemplateFactory;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;
import edu.pitt.dbmi.nlp.noble.ontology.ILogicExpression;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.IOntologyException;
import edu.pitt.dbmi.nlp.noble.ontology.IProperty;
import edu.pitt.dbmi.nlp.noble.ontology.IRestriction;
import edu.pitt.dbmi.nlp.noble.ontology.LogicExpression;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;
import edu.pitt.dbmi.nlp.noble.terminology.TerminologyException;

public class CreateDomainTemplate {
	/**
	 * create extraction model for a domain in OWL format 
	 * @param inputFile
	 * @param outputFile
	 * @throws TerminologyException 
	 * @throws IOException 
	 * @throws IOntologyException 
	 * @throws FileNotFoundException 
	 */
	public static IOntology createDomainOntology(File inputFile,File outputFile) throws FileNotFoundException, IOntologyException, IOException, TerminologyException{
		// create ontology and save the template
		TemplateCreator termCreator = new TemplateCreator();
		System.out.println("initializing ..");
		IOntology ontology = termCreator.createOntology(inputFile);
		System.out.println("saving ontology "+outputFile.getAbsolutePath()+" ...");
		ontology.write(new FileOutputStream(outputFile),IOntology.OWL_FORMAT);
		return ontology;
	}
	
	/**
	 * create domain template file
	 * @param ontologyFile
	 * @param templateFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOntologyException
	 * @throws IOException
	 * @throws TerminologyException
	 * @throws Exception
	 */
	public static Template createDomainTemplate(File ontologyFile, File templateFile) throws FileNotFoundException, IOntologyException, IOException, TerminologyException, Exception{
		System.out.println("Creating template ..");
		TemplateFactory tf = TemplateFactory.getInstance();
		for(Template template : TemplateFactory.importOntologyTemplate(ontologyFile.getAbsolutePath())){
			tf.exportTemplate(template,new FileOutputStream(templateFile));
			return template;
		}
		return null;
	}
	
	/**
	 * customize document data ranges for a given ontology
	 * @param outputOntology
	 * @throws IOntologyException 
	 */
	private static void augmentDomainOntology(File  ontology) throws IOntologyException {
		System.out.println("Augmenting ontology "+ontology);
		List<String> keywordsOnly = Arrays.asList("Age","Gender","Species","Ethnicity","Publication_Type","Language");
		
		// create MH restriction
		IOntology ont = OOntology.loadOntology(ontology);
		IProperty hasMentionOf = ont.getProperty("hasMentionOf");
		IProperty hasDocumentRange = ont.getProperty("hasDocumentRange");
		IRestriction mh = ont.createRestriction(IRestriction.HAS_VALUE);
		mh.setProperty(hasDocumentRange);
		mh.setParameter(ont.createLogicExpression(ILogicExpression.EMPTY,"MH"));
		IRestriction ln = ont.createRestriction(IRestriction.HAS_VALUE);
		ln.setProperty(hasDocumentRange);
		ln.setParameter(ont.createLogicExpression(ILogicExpression.EMPTY,"LA"));
		
		
		// iterate over Slot classes
		for(IClass cls: ont.getClass("Slot").getDirectSubClasses()){
			// add special keyword document handling
			String nm = cls.getName();
			IRestriction [] rr = cls.getRestrictions(hasMentionOf);
			IRestriction r = rr.length > 0?rr[0]:null;
			
			if(nm.endsWith("_Slot") && keywordsOnly.contains(nm.substring(0, nm.length()-5)) && r != null){
				cls.removeNecessaryRestriction(r);
				ILogicExpression ex = new LogicExpression(ILogicExpression.AND);
				ex.add(r);
				if(nm.startsWith("Language"))
					ex.add(ln);
				else
					ex.add(mh);
				cls.addNecessaryRestriction(ex);
				
			}
			// handle study design
			if(nm.contains("Study_Design") && r != null){
				cls.removeNecessaryRestriction(r);
				
				
				IRestriction mh1 = ont.createRestriction(IRestriction.HAS_VALUE);
				mh1.setProperty(hasDocumentRange);
				mh1.setParameter(ont.createLogicExpression(ILogicExpression.EMPTY,"(MH|TI)"));
				
				IRestriction mh2 = ont.createRestriction(IRestriction.HAS_VALUE);
				mh2.setProperty(hasDocumentRange);
				mh2.setParameter(ont.createLogicExpression(ILogicExpression.EMPTY,"AB[-2]"));
				
				ILogicExpression exp = new LogicExpression(ILogicExpression.OR);
				exp.add(mh1);
				exp.add(mh2);
				
				ILogicExpression ex = new LogicExpression(ILogicExpression.AND);
				ex.add(r);
				ex.add(exp);
				cls.addNecessaryRestriction(ex);
			}
		}
		
		ont.save();
		
	}

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File dataDirectory = new File("/home/tseytlin/Data");
		File seedTerminology = new File(dataDirectory,"BreastCancer.txt");
		File outputOntology = new File(dataDirectory,"BreastCancer.owl");
		File templateFile = new File(dataDirectory,"BreastCancer.template");
		
		// create domain
		//createDomainOntology(seedTerminology, outputOntology);
		
		// augment ontology to add ranges
		//augmentDomainOntology(outputOntology);
		
		// create template
		createDomainTemplate(outputOntology, templateFile);
		
	}
}
