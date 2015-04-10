package edu.pitt.dbmi.deepphe.i2b2;

import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author kjm84
 */
public class PartialPath implements Comparable<PartialPath> {
    
    private String path = "\\DEEPPHE";
    private int level = 0;
    
    private OWLReasoner reasoner;
    private OWLClass cls;
    
    public boolean isExpandable() {
        if (reasoner == null || cls == null) {
            return false;
        }
        else {
            return !reasoner.getSubClasses(cls, true).isEmpty();
        }
    }
    
    public List<PartialPath> expand() {
        
        final List<PartialPath> extensions = new ArrayList<>();
        
        String iriAsString = cls.getIRI().toString();
        int lastPoundPos = iriAsString.lastIndexOf("#");
        String simpleName = iriAsString.substring(lastPoundPos + 1);
        path =  path + "\\" + simpleName;
        
        NodeSet<OWLClass> subClses  = reasoner.getSubClasses(cls, true);
        for (Node<OWLClass> subClsNode : subClses) {
            OWLClass subCls = subClsNode.getRepresentativeElement();
            if (subCls.getIRI().toString().equals("http://www.w3.org/2002/07/owl#Nothing")) {
                continue;
            }
            PartialPath pathExtension = new PartialPath();
            pathExtension.setReasoner(reasoner);
            pathExtension.setCls(subCls);
            pathExtension.setPath(path);
            pathExtension.setLevel(level + 1);
            extensions.add(pathExtension);
        }
        
        return extensions;
    }
    
    public void setReasoner(OWLReasoner reasoner) {
        this.reasoner = reasoner;
    }
    
    public void setCls(OWLClass cls) {
        this.cls = cls;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
   
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    @Override
    public String toString() {
        String clsString = (cls == null) ? "topLevel" : cls.getIRI().toString();
        return "Level: " + getLevel() + "\n Path: " + path + "\n IRI: " + clsString ;
    }

    @Override
    public int compareTo(PartialPath o) {
        if (this.getLevel() < o.getLevel()) {
            return -1;
        }
        else if (this.getLevel() > o.getLevel()) {
            return 1;
        }
        else /* levels are the same */ {
            if (this.getPath().equals(o.getPath())) {
                return 0;
            }
            else {
                return this.getPath().compareTo(o.getPath());
            }
            
        }
    }
    
    
}
