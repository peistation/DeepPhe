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

(deftemplate Identified ""
     (declare (from-class Identified)
                  (include-variables TRUE)))
(deftemplate Goal ""
     (declare (from-class Goal)
                  (include-variables TRUE)))
(deftemplate Summarizable ""
     (declare (from-class Summarizable)
                  (include-variables TRUE)))
(deftemplate Summary ""
     (declare (from-class Summary)
                  (include-variables TRUE)))
(deftemplate Patient ""
     (declare (from-class Patient)
                  (include-variables TRUE)))
(deftemplate Encounter ""
     (declare (from-class Encounter)
                  (include-variables TRUE)))
(deftemplate Diagnosis ""
     (declare (from-class Diagnosis)
                  (include-variables TRUE)))
(deftemplate Er ""
     (declare (from-class Er)
                  (include-variables TRUE)))
(deftemplate Pr ""
     (declare (from-class Pr)
                  (include-variables TRUE)))
(deftemplate Her2 ""
     (declare (from-class Her2)
                  (include-variables TRUE)))
(deftemplate ReceptorStatus ""
     (declare (from-class ReceptorStatus)
                  (include-variables TRUE)))

;
;  plan-for-breast-cancer
;
;
;
(defrule plan-for-breast-cancer""
   (declare (salience 1000))
      ?g <- (Goal (name "establish-plan"))
       (Diagnosis (preferredTerm ?preferredTerm&:(eq ?preferredTerm "Malignant Breast Neoplasm")))
=>
     (printout t "Establishing goals for  Malignant Breast Neoplasm" crlf)
     (retract ?g)
     (bind ?priority 0)
     (bind ?g (add (new Goal)))
     (modify ?g (name "extract-receptor-status") (priority ?priority) (isActive 1))
     (bind ?priority (+ ?priority 1))
     (bind ?g (add (new Goal)))
     (modify ?g (name "extract-tnm") (priority ?priority)))
;;(undefrule  plan-for-breast-cancer )
    
;
; ER, PER. Her2 Phenotyping Rules:
; 
;  Author:  Rebecca Jacobson
;  Date:  May 2015
;

;
; ER - Pathology Report
;
;  If patient has encounter data "tumor" from Pathology with ER relation <ER, value>
;  => phenotype ER = value

(defrule er-pathology-report "Create phenotype from single value of ER from Pathology Report FD or addendum"
      (declare (salience 1000))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Encounter (id ?encounterId) (kind ?kind&:(eq ?kind "Pathology")))
      (Er (summarizableId ?encounterId)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure))
      (Patient (id ?patientId))
      (not (Er (summarizableId ?patientId)))
=>
      (printout t "er-pathology-report fires..." crlf)
      (bind ?patientEr (add (new Er)))
      (modify ?patientEr (summarizableId ?patientId)
                         (code ?code)
                         (preferredTerm ?preferredTerm)
                         (value ?value)
                         (unitOfMeasure ?unitOfMeasure)
                         (isActive 1)))

;
; PR - Pathology Report
;
;  If patient has encounter data "tumor" from Pathology with PR relation <PR, value>
;  => phenotype PR = value

(defrule pr-pathology-report "Create phenotype from single value of PR from Pathology Report FD or addendum"
      (declare (salience 1000))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Encounter (id ?encounterId) (kind ?kind&:(eq ?kind "Pathology")))
      (Pr (summarizableId ?encounterId)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure))
      (Patient (id ?patientId))
      (not (Pr (summarizableId ?patientId)))
=>
    (printout t "pr-pathology-report fires..." crlf)
      (bind ?patientPr (add (new Pr)))
      (modify ?patientPr (summarizableId ?patientId)
                         (code ?code)
                         (preferredTerm ?preferredTerm)
                         (value ?value)
                         (unitOfMeasure ?unitOfMeasure)
                         (isActive 1)))

;
; Her2 - Pathology Report
;
;  If patient has encounter data "tumor" from Pathology with Her2 relation <Her2, value>
;  => phenotype Her2 = value

(defrule her2-pathology-report "Create phenotype from single Pathology Report Her2 FD or addendum"
      (declare (salience 1000))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Encounter (id ?encounterId) (kind ?kind&:(eq ?kind "Pathology")))
      (Her2 (summarizableId ?encounterId)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure))
      (Patient (id ?patientId))
      (not (Her2 (summarizableId ?patientId)))
=>
  (printout t " her2-pathology-report fires..." crlf)
      (bind ?patientHer2 (add (new Her2)))
      (modify ?patientHer2 (summarizableId ?patientId)
                         (code ?code)
                         (preferredTerm ?preferredTerm)
                         (value ?value)
                         (unitOfMeasure ?unitOfMeasure)
                         (isActive 1)))

;
; Receptor status triple positive
;
; If ER+, PR+, HER2+ => phenotype receptor status = Triple Positive
;
(defrule receptor-status-triple-positive ""
      (declare (salience 900))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Er (summarizableId ?patientId)(value ?value&:(eq ?value "positive")))
      (Pr (summarizableId ?patientId)(value ?value))
      (Her2 (summarizableId ?patientId)(value ?value))
      (not (ReceptorStatus (summarizableId ?patientId) (value "TriplePositive")))
=>
       (printout t "receptor-status-triple-positive.fires..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)(value "TriplePositive")(isActive 1)))

;
; Receptor status triple negative
;
; If ER-, PR-, HER2- => phenotype receptor status = Triple Negative
;
(defrule receptor-status-triple-negative ""
      (declare (salience 900))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Er (summarizableId ?patientId)(value ?value&:(eq ?value "negative")))
      (Pr (summarizableId ?patientId)(value ?value))
      (Her2 (summarizableId ?patientId)(value ?value))
      (not (ReceptorStatus (summarizableId ?patientId)(value "TripleNegative")))
=>
      (printout t "receptor-status-triple-positive.fires..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId) (value "TripleNegative")(isActive 1)))

;
; Receptor status Her2 positive
;
; If HER2+ => phenotype receptor status = Her2 Positive
;
(defrule receptor-status-her2-positive ""
      (declare (salience 800))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Her2 (summarizableId ?patientId)(value ?value&:(eq ?value "positive")))
      (not (ReceptorStatus (summarizableId ?patientId) (value "Her2Positive")))
=>
      (printout t "receptor-status-her2-positive..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)(value "Her2Positive")(isActive 1)))

;
; Receptor status - Hormone receptor negative
;
; If ER-, PR- => phenotype receptor status = Hormone Receptor Negative
;
(defrule receptor-status-hormone-receptor-negative ""
      (declare (salience 800))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Er (summarizableId ?patientId)(value ?value&:(eq ?value "negative")))
      (Pr (summarizableId ?patientId)(value ?value))
      (not (ReceptorStatus (summarizableId ?patientId)(value "HormoneReceptorNegative")))
=>
      (printout t "receptor-status-hormone-receptor-negative..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)(value "HormoneReceptorNegative")(isActive 1)))
