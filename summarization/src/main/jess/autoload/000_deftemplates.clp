(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Identified)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Goal)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summarizable)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summary)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Diagnosis)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Er)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Pr)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.Her2)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.ReceptorStatus)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.TnmTgrade)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.TnmNgrade)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.TnmMgrade)
(import edu.pitt.dbmi.deepphe.summarization.jess.kb.TumorSize)

(deftemplate Identified 
     (declare (from-class Identified)
                  (include-variables TRUE)))
(deftemplate Goal extends Identified 
     (declare (from-class Goal)
                  (include-variables TRUE)))
(deftemplate Summarizable extends Identified 
     (declare (from-class Summarizable)
                  (include-variables TRUE)))
(deftemplate Summary extends Identified 
     (declare (from-class Summary)
                  (include-variables TRUE)))
(deftemplate Patient extends Summarizable 
     (declare (from-class Patient)
                  (include-variables TRUE)))
(deftemplate Encounter extends Summarizable 
     (declare (from-class Encounter)
                  (include-variables TRUE)))
(deftemplate Diagnosis extends Summary 
     (declare (from-class Diagnosis)
                  (include-variables TRUE)))
(deftemplate Er extends Summary 
     (declare (from-class Er)
                  (include-variables TRUE)))
(deftemplate Pr extends Summary 
     (declare (from-class Pr)
                  (include-variables TRUE)))
(deftemplate Her2 extends Summary 
     (declare (from-class Her2)
                  (include-variables TRUE)))
(deftemplate ReceptorStatus extends Summary 
     (declare (from-class ReceptorStatus)
                  (include-variables TRUE)))
(deftemplate TnmTgrade extends Summary 
     (declare (from-class TnmTgrade)
                  (include-variables TRUE)))
(deftemplate TnmNgrade extends Summary 
     (declare (from-class TnmNgrade)
                  (include-variables TRUE)))
(deftemplate TnmMgrade extends Summary 
     (declare (from-class TnmMgrade)
                  (include-variables TRUE)))
(deftemplate TumorSize extends Summary 
     (declare (from-class TumorSize)
                  (include-variables TRUE)))


