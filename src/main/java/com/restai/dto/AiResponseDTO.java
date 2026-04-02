package com.restai.dto;

import java.util.List;
// DTO class to represent the AI response. More fields like description can be added as needed. Add to schema.sql as well.
public class AiResponseDTO {
	private String image_name;
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getModel() {
		return model;
	}
	@Override
	public String toString() {
		return "AiResponseDTO [image_name=" + image_name + ", provider=" + provider + ", model=" + model + ", category="
				+ category + ", tags=" + tags + "]";
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	private String provider;
	private String model;
	private String category;
    private List<String> tags;
    // Getters and Setters
}
