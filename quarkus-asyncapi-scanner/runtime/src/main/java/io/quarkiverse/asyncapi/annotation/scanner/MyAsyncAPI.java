
package io.quarkiverse.asyncapi.annotation.scanner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.component.Components;
import com.asyncapi.v3._0_0.model.info.Info;

/**
 * Workaround for https://github.com/asyncapi/jasyncapi/issues/198
 * AsyncAPI v3 is not serializable due final fields...
 *
 * @since 06.05.2024
 */
public class MyAsyncAPI implements Serializable {

    private String asyncapi = "3.0.0";
    private String id;
    private String defaultContentType;
    private Info info = new Info();
    private Map<String, Object> servers = new HashMap<>();
    private Map<String, Object> channels = new HashMap<>();
    private Map<String, Object> operations = new HashMap<>();
    private Components components;
    private Map<String, Object> extensionFields;

    public MyAsyncAPI() {
        //default constructor
    }

    public MyAsyncAPI(AsyncAPI aAsyncAPI) {
        asyncapi = aAsyncAPI.getAsyncapi();
        id = aAsyncAPI.getId();
        defaultContentType = aAsyncAPI.getDefaultContentType();
        info = aAsyncAPI.getInfo();
        servers = aAsyncAPI.getServers();
        channels = aAsyncAPI.getChannels();
        operations = aAsyncAPI.getOperations();
        components = aAsyncAPI.getComponents();
        extensionFields = aAsyncAPI.getExtensionFields();
    }

    public AsyncAPI toAsyncAPI() {
        AsyncAPI asyncAPI = AsyncAPI.builder()
                .asyncapi(asyncapi)
                .id(id)
                .defaultContentType(defaultContentType)
                .info(info)
                .servers(servers)
                .channels(channels)
                .operations(operations)
                .components(components)
                .build();
        asyncAPI.setExtensionFields(extensionFields);
        return asyncAPI;
    }

    //<editor-fold defaultstate="collapsed" desc="getter & setter">
    public String getAsyncapi() {
        return asyncapi;
    }

    public void setAsyncapi(String aAsyncapi) {
        this.asyncapi = aAsyncapi;
    }

    public String getId() {
        return id;
    }

    public void setId(String aId) {
        this.id = aId;
    }

    public String getDefaultContentType() {
        return defaultContentType;
    }

    public void setDefaultContentType(String aDefaultContentType) {
        this.defaultContentType = aDefaultContentType;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info aInfo) {
        this.info = aInfo;
    }

    public Map<String, Object> getServers() {
        return servers;
    }

    public void setServers(Map<String, Object> aServers) {
        this.servers = aServers;
    }

    public Map<String, Object> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, Object> aChannels) {
        this.channels = aChannels;
    }

    public Map<String, Object> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, Object> aOperations) {
        this.operations = aOperations;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components aComponents) {
        this.components = aComponents;
    }

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> aExtensionFields) {
        this.extensionFields = aExtensionFields;
    }
    //</editor-fold>
}
