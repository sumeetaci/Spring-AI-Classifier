package com.restai.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIRouteMetaData {

	private String routeName;
	
	private String contextType;
	
	private String provider;
	
	private String releaseDateString;
	
	private String resourceSizeInBytes;
	
	private List<String> utterancesList = new ArrayList<String>();
	
	private Map<String, List<String>> ruleAndUtteranceListMap = new HashMap<>();
	
	
	/* @Override
	public String toString() {
		return "AIModelMetaData [resourceName=" + routeName + ", contextType=" + contextType
				+ ", modelProviderString=" + provider + ", resourceSizeInBytes=" + resourceSizeInBytes
				+ ", wordContextList=" + String.join(" | ", wordContextList) + ", wordAndItsFrequencyMap=" + wordAndItsFrequencyMap
				+ ", rAGTemplate=" + rAGTemplate + ", chunkSize=" + chunkSize + ", minChunkSizeChars="
				+ minChunkSizeChars + ", minChunkLengthToEmbed=" + minChunkLengthToEmbed + ", maxNumChunks="
				+ maxNumChunks + ", keepSeparator=" + keepSeparator + "]";
	} */
	
	private String rAGTemplate;
	
	private int chunkSize;
	private int minChunkSizeChars;
	private int minChunkLengthToEmbed;
	private int maxNumChunks;
	private boolean keepSeparator;
	public String getResourceNameString() {
		return routeName;
	}
	public void setResourceNameString(String resourceNameString) {
		this.routeName = resourceNameString;
	}
	public String getContextType() {
		return contextType;
	}
	public void setContextType(String contextTypeString) {
		this.contextType = contextTypeString;
	}
	public String getrAGTemplateString() {
		return rAGTemplate;
	}
	public void setrAGTemplateString(String rAGTemplateString) {
		this.rAGTemplate = rAGTemplateString;
	}
	public int getChunkSize() {
		return chunkSize;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	public int getMinChunkSizeChars() {
		return minChunkSizeChars;
	}
	public void setMinChunkSizeChars(int minChunkSizeChars) {
		this.minChunkSizeChars = minChunkSizeChars;
	}
	public int getMinChunkLengthToEmbed() {
		return minChunkLengthToEmbed;
	}
	public void setMinChunkLengthToEmbed(int minChunkLengthToEmbed) {
		this.minChunkLengthToEmbed = minChunkLengthToEmbed;
	}
	public int getMaxNumChunks() {
		return maxNumChunks;
	}
	public void setMaxNumChunks(int maxNumChunks) {
		this.maxNumChunks = maxNumChunks;
	}
	public boolean isKeepSeparator() {
		return keepSeparator;
	}
	public void setKeepSeparator(boolean keepSeparator) {
		this.keepSeparator = keepSeparator;
	}
	public String getResourceSizeInBytes() {
		return resourceSizeInBytes;
	}
	public void setResourceSizeInBytes(String resourceSizeInMB) {
		this.resourceSizeInBytes = resourceSizeInMB;
	}
	public List<String> getWordContextList() {
		return utterancesList;
	}
	public void setWordContextList(List<String> wordContextList) {
		this.utterancesList = wordContextList;
	}
	
	public Map<String, List<String>> getWordAndItsFrequencyMap(){
		return ruleAndUtteranceListMap;
	}
	
	public void setWordAndItsFrequencyMap( Map<String, List<String>> map) {
		ruleAndUtteranceListMap.putAll(map);
	}
	
	public String getModelProviderString() {
		return provider;
	}
	public void setModelProviderString(String modelProviderString) {
		this.provider = modelProviderString;
	}
	public String getReleaseDateString() {
		return releaseDateString;
	}
	public void setReleaseDateString(String releaseDateString) {
		this.releaseDateString = releaseDateString;
	}
	
	
}
