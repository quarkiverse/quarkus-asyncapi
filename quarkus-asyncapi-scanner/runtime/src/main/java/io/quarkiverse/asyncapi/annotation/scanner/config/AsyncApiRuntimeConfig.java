package io.quarkiverse.asyncapi.annotation.scanner.config;

import java.util.Map;
import java.util.Optional;

import io.quarkiverse.asyncapi.annotation.scanner.AsyncApiFilter;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
@ConfigMapping(prefix = "quarkus.asyncapi.annotation.scanner")
public interface AsyncApiRuntimeConfig {

    /**
     * Enable AysncApi-Scanning
     */
    @WithDefault("true")
    public boolean enabled();

    /**
     * Version of the WebComponent to be used in html-view to be found at [HOST]/asyncapi.html
     *
     * @see https://www.npmjs.com/package/@asyncapi/react-component
     */
    @WithDefault("1.4.10")
    public String webcomponentversion();

    /**
     * Version of the ReasComponent to be used in html-view to be found at [HOST]/asyncapi.html
     *
     * @see https://www.npmjs.com/package/@asyncapi/react-component
     */
    @WithDefault("2.0.0")
    public String reactcomponentversion();

    /**
     * Version of the WebComponentJS to be used in html-view to be found at [HOST]/asyncapi.html
     *
     * @see https://www.npmjs.com/package/@webcomponents/webcomponentsjs
     */
    @WithDefault("2.8.0")
    public String webcomponentjsversion();

    /**
     * Full qualified name of the implementing AsyncApiFilter
     *
     * @see AsyncApiFilter
     */
    public Optional<String> filter();

    /**
     * AsyncApi specification version
     */
    @WithDefault("3.0.0")
    public String version();

    /**
     * Default ContentType
     */
    @WithDefault("application/json")
    public String defaultContentType();

    /**
     * see https://www.asyncapi.com/docs/reference/specification/v3.6.0#serversObject
     */
    @WithName("server")
    public Map<String, Server> servers();

    /**
     * see https://www.asyncapi.com/docs/reference/specification/v3.6.0#channelItemObject
     */
    @WithName("channel")
    public Map<String, Channel> channels();

    /**
     * Title
     */
    @WithName("info.title")
    @WithDefault("AsyncApi")
    public Optional<String> infoTitle();

    /**
     * Project-version
     */
    @WithName("info.version")
    @WithDefault("1.0.0")
    public String infoVersion();

    /**
     * Project-description
     */
    @WithName("info.description")
    public Optional<String> infoDescription();

    /**
     * Contact-Name
     */
    @WithName("info.contact.name")
    public Optional<String> infoContactName();

    /**
     * Contact-Email
     */
    @WithName("info.contact.email")
    @WithDefault("you@mail.org")
    public Optional<String> infoContactEmail();

    /**
     * Contact-URL
     */
    @WithName("info.contact.url")
    public Optional<String> infoContactUrl();

    /**
     * License-Name
     */
    @WithName("info.license.name")
    @WithDefault("Commercial")
    public Optional<String> infoLicensName();

    /**
     * License-URL
     */
    @WithName("info.license.url")
    public Optional<String> infoLicenseUrl();
}
