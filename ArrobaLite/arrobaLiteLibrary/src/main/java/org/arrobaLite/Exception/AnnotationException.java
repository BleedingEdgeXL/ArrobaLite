/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arrobaLite.Exception;

/**
 *
 * @author d070169
 */
public class AnnotationException extends Exception{ 

    public AnnotationException(String message){ super(message); }
    public AnnotationException(String message,Throwable error){ super(message,error); }
    public AnnotationException(Throwable error){ super(error); }
}
