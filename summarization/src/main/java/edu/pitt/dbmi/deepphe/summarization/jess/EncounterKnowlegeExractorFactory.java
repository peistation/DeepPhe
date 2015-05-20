package edu.pitt.dbmi.deepphe.summarization.jess;

public class EncounterKnowlegeExractorFactory {
	
	private static EncounterKnowledgeExtractor encounterKnowledgeExtractor = new StubEncounterKnowledgeExtractor();
	
	private EncounterKnowlegeExractorFactory() {}
	
	public static EncounterKnowledgeExtractor getEncounterKnowledgeExtractor() {
		return encounterKnowledgeExtractor;
	}
	public static void setEncounterKnowledgeExtractor(EncounterKnowledgeExtractor encounterKnowledgeExtractorImpl) {
		encounterKnowledgeExtractor = encounterKnowledgeExtractorImpl;
	}
}
