package com.restai.components;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

/* @Component
@ConfigurationProperties(prefix = "app")
public class ProviderConfig {
    // Spring binds the YAML 'provider-mapping' to this map
    private Map<String, Class<?>> providerMapping;

    public Map<String, Class<?>> getProviderMapping() {
        return providerMapping;
    }

    public void setProviderMapping(Map<String, Class<?>> providerMapping) {
        this.providerMapping = providerMapping;
    }
}
*/

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "ai")
public class ProviderConfig {

    // This MUST match the name in your properties (ai.models)
    private List<ModelSettings> models = new ArrayList<>();

    public List<ModelSettings> getModels() { return models; }
    public void setModels(List<ModelSettings> models) { this.models = models; }

    // Helper method to provide the mapping your Initializer expects
    public Map<String, Class<? extends ChatModel>> getProviderMapping() {
        return models.stream().collect(Collectors.toMap(
            ModelSettings::getName,
            ModelSettings::getChatmodel
        ));
    }

    public static class ModelSettings {
        private String name;
        private String provider;
        private Class<? extends ChatModel> chatmodel;

        // Getters and Setters are MANDATORY for property binding
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public Class<? extends ChatModel> getChatmodel() { return chatmodel; }
        public void setChatmodel(Class<? extends ChatModel> chatmodel) { this.chatmodel = chatmodel; }
    }
}