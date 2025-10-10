
package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.devui.spi.DevContextBuildItem;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.runtime.management.ManagementInterfaceBuildTimeConfig;

/**
 * @since 09.10.2025
 */
public class AsyncApiDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    public CardPageBuildItem pages(NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
        ManagementInterfaceBuildTimeConfig managementBuildTimeConfig,
        LaunchModeBuildItem launchModeBuildItem,
        Optional<DevContextBuildItem> devContextBuildItem) {
        String path = ConfigProvider.getConfig()
            .getValue("quarkus.http.root-path", String.class).concat("/asyncapi");

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();

        cardPageBuildItem.addLibraryVersion("com.asyncapi", "asyncapi-core", "AsyncApi", "https://www.asyncapi.com/");
        cardPageBuildItem.setLogo("asyncApiLogo.png", "asyncApiLogo.png");
        cardPageBuildItem.addPage(Page.externalPageBuilder("AsyncApi UI")
            .url(path + ".html")
            .isHtmlContent()
            .icon("font-awesome-solid:map"));

        cardPageBuildItem.addPage(Page.externalPageBuilder("AsyncApi yaml")
            .url(path + ".yaml")
            .isYamlContent()
            .icon("font-awesome-solid:code"));

        cardPageBuildItem.addPage(Page.externalPageBuilder("AsyncApi diagram")
            .url(path + ".svg")
            .isHtmlContent()
            .icon("font-awesome-solid:diagram-project"));

        return cardPageBuildItem;
    }

    @BuildStep
    JsonRPCProvidersBuildItem createJsonRPCService() {
        return new JsonRPCProvidersBuildItem(AsyncApiJsonRpcService.class);
    }

}
