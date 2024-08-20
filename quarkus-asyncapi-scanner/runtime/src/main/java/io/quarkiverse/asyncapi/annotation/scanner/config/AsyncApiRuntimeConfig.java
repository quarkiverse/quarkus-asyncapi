package io.quarkiverse.asyncapi.annotation.scanner.config;

import java.util.Map;
import java.util.Optional;

import io.quarkiverse.asyncapi.annotation.scanner.AsyncApiFilter;
import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigItem;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "asyncapi.annotation.scanner")
public class AsyncApiRuntimeConfig {

    /**
     * Enable AysncApi-Scanning
     */
    @ConfigItem(defaultValue = "true")
    public boolean enabled;

    /**
     * Version of the WebComponent to be used in html-view to be found at [HOST]/asyncapi.html
     *
     * @see https://www.npmjs.com/package/@asyncapi/react-component
     */
    @ConfigItem(defaultValue = "1.4.10")
    public String webcomponentversion;
    /**
     * Version of the ReasComponent to be used in html-view to be found at [HOST]/asyncapi.html
     *
     * @see https://www.npmjs.com/package/@asyncapi/react-component
     */
    @ConfigItem(defaultValue = "2.0.0")
    public String reactcomponentversion;
    /**
     * Version of the WebComponentJS to be used in html-view to be found at [HOST]/asyncapi.html
     *
     * @see https://www.npmjs.com/package/@webcomponents/webcomponentsjs
     */
    @ConfigItem(defaultValue = "2.8.0")
    public String webcomponentjsversion;

    /**
     * Full qualified name of the implementing AsyncApiFilter
     *
     * @see AsyncApiFilter
     */
    @ConfigItem
    public Optional<String> filter = Optional.empty();

    /**
     * AsyncApi specification version
     */
    @ConfigItem(defaultValue = "3.0.0")
    public String version;

    /**
     * Default ContentType
     */
    @ConfigItem(defaultValue = "application/json")
    public String defaultContentType;

    /**
     * see https://www.asyncapi.com/docs/reference/specification/v3.6.0#serversObject
     */
    @ConfigDocMapKey("server")
    @ConfigItem(name = "server")
    public Map<String, Server> servers;

    /**
     * see https://www.asyncapi.com/docs/reference/specification/v3.6.0#channelItemObject
     */
    @ConfigDocMapKey("channel")
    @ConfigItem(name = "channel")
    public Map<String, Channel> channels;

    /**
     * Title
     */
    @ConfigItem(name = "info.title", defaultValue = "AsyncApi")
    public Optional<String> infoTitle = Optional.empty();
    /**
     * Project-version
     */
    @ConfigItem(name = "info.version", defaultValue = "1.0.0")
    public String infoVersion;
    /**
     * Project-description
     */
    @ConfigItem(name = "info.description")
    public Optional<String> infoDescription = Optional.empty();

    /**
     * Contact-Name
     */
    @ConfigItem(name = "info.contact.name")
    public Optional<String> infoContactName = Optional.empty();
    /**
     * Contact-Email
     */
    @ConfigItem(name = "info.contact.email", defaultValue = "you@mail.org")
    public Optional<String> infoContactEmail = Optional.empty();
    /**
     * Contact-URL
     */
    @ConfigItem(name = "info.contact.url")
    public Optional<String> infoContactUrl = Optional.empty();
    /**
     * License-Name
     */
    @ConfigItem(name = "info.license.name", defaultValue = "Commercial")
    public Optional<String> infoLicensName = Optional.empty();
    /**
     * License-URL
     */
    @ConfigItem(name = "info.license.url")
    public Optional<String> infoLicenseUrl = Optional.empty();
}
