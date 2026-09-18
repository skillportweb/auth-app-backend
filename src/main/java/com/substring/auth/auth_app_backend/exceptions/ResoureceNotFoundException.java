package com.substring.auth.auth_app_backend.exceptions;

public class ResoureceNotFoundException extends RuntimeException{
 public ResoureceNotFoundException(String message) {
	 super(message);
 }
 public ResoureceNotFoundException() {
	 super("Resource not found");
 }
}
