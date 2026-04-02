package com.restai.models;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restai.common.Utils;

public class AIModelMetaData {

	private String resourceName;
	
	private String contextType;
	
	private String provider;
	
	private String releaseDateString;
	
	private LocalDateTime releaseDate;
	
	private String resourceSizeInBytes;
	
	private int priority;
	
	private List<String> wordContextList = new ArrayList<String>();
	
	private Map<String, Long> wordAndItsFrequencyMap = new HashMap<>();
	
	
	@Override
	public String toString() {
		return "AIModelMetaData [resourceName=" + resourceName + ", contextType=" + contextType
				+ ", modelProviderString=" + provider + ", resourceSizeInBytes=" + resourceSizeInBytes
				+ ", wordContextList=" + String.join(" | ", wordContextList) + ", wordAndItsFrequencyMap=" + wordAndItsFrequencyMap
				+ ", rAGTemplate=" + rAGTemplate + ", chunkSize=" + chunkSize + ", minChunkSizeChars="
				+ minChunkSizeChars + ", minChunkLengthToEmbed=" + minChunkLengthToEmbed + ", maxNumChunks="
				+ maxNumChunks + ", keepSeparator=" + keepSeparator + "]";
	}
	
	private String rAGTemplate;
	
	private int chunkSize;
	private int minChunkSizeChars;
	private int minChunkLengthToEmbed;
	private int maxNumChunks;
	private boolean keepSeparator;
	public String getResourceNameString() {
		return resourceName;
	}
	public void setResourceNameString(String resourceNameString) {
		this.resourceName = resourceNameString;
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
		return wordContextList;
	}
	public void setWordContextList(List<String> wordContextList) {
		this.wordContextList = wordContextList;
	}
	
	public Map<String, Long> getWordAndItsFrequencyMap(){
		return wordAndItsFrequencyMap;
	}
	
	public void setWordAndItsFrequencyMap( Map<String, Long> map) {
		wordAndItsFrequencyMap.putAll(map);
	}
	
	public AIModelMetaData getDocumentMetaDataByResourceName(String resourceName) {
		return this;
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
		LocalDateTime getTimeStampDateTime = LocalDateTime.parse(releaseDateString, Utils.FullDateWithtimeFormatter);
		setReleaseDate(getTimeStampDateTime);
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public LocalDateTime getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(LocalDateTime releaseDate) {
		this.releaseDate = releaseDate;
	}
	
}
