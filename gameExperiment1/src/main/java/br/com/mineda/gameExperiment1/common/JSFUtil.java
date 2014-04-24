package br.com.mineda.gameExperiment1.common;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class JSFUtil {
	
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
    public static void addErrorMessage(final String message) {  
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));  
    } 
    
    public static void addErrorMessage(final Throwable t) {  
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, t.getMessage(), null));  
    }     

}
