package com.restai.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;


@Entity
@Table(name = "image_metadata")
public class ImageMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "file_category")
    private String fileCategory;
    
    @Column(name = "model_name")
    private String modelName;
    
    @Column(name = "model_provider")
    private String modelProvider;


    public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelCategory) {
		this.modelName = modelCategory;
	}

	public String getModelProvider() {
		return modelProvider;
	}

	public void setModelProvider(String modelProvider) {
		this.modelProvider = modelProvider;
	}

	@Column(name = "file_created_at")
    private LocalDateTime fileCreatedAt = LocalDateTime.now();

    // Native PostgreSQL Array for tags
    @ElementCollection
    @CollectionTable(name = "image_tags", joinColumns = @JoinColumn(name = "image_id")) 
    @Column(name = "tag") 
    private List<String> fileTags = new ArrayList<>();

    // The image itself (Binary Data)
    @Lob
    @JdbcTypeCode(java.sql.Types.BINARY)
    @Column(name = "file_data")
    private byte[] fileData;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getFileCategory() {
		return fileCategory;
	}

	public void setFileCategory(String fileCategory) {
		this.fileCategory = fileCategory;
	}

	public LocalDateTime getFileCreatedAt() {
		return fileCreatedAt;
	}

	public void setFileCreatedAt(LocalDateTime fileCreatedAt) {
		this.fileCreatedAt = fileCreatedAt;
	}

	public List<String> getFileTags() {
		return fileTags;
	}

	public void setFileTags(List<String> fileTags) {
		this.fileTags = fileTags;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	@Override
	public String toString() {
		return "ImageMetadata [id=" + id + ", fileName=" + fileName + ", uploadedBy=" + uploadedBy + ", fileCategory="
				+ fileCategory + ", modelCategory=" + modelName + ", modelProvider=" + modelProvider
				+ ", fileCreatedAt=" + fileCreatedAt + ", fileTags=" + fileTags + "]";
	}

    
    
}
