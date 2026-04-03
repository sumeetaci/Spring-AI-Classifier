package com.restai.models;

public class ClassificationContent {
	
	 private String response;
	    private String provider;
		public String getResponse() {
			return response;
		}
		public void setResponse(String response) {
			this.response = response;
		}
		public String getProvider() {
			return provider;
		}
		public void setProvider(String provider) {
			this.provider = provider;
		}
		@Override
		public String toString() {
			return "ClassificationContent [response=" + response + ", provider=" + provider + "]";
		}
    
}
