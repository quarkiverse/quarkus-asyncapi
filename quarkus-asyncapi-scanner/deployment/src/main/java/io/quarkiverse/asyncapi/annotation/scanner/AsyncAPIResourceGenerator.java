package io.quarkiverse.asyncapi.annotation.scanner;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.eclipse.microprofile.config.ConfigProvider;

import com.asyncapi.v2._0_0.model.AsyncAPI;

import io.quarkiverse.asyncapi.annotation.scanner.config.AsyncApiRuntimeConfig;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.vertx.http.deployment.FilterBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.filters.Filter;
import io.vertx.ext.web.Route;

public class AsyncAPIResourceGenerator {

    @Record(RUNTIME_INIT)
    @BuildStep(onlyIf = IsEnabled.class)
    void scanAsyncAPIs(
            CombinedIndexBuildItem aIndex,
            AsyncApiRecorder aRecorder,
            AsyncApiRuntimeConfig aConfig,
            RecorderContext aRecorderContext) {
        aRecorderContext.registerSubstitution(BigDecimal.class, Double.class, BigDecimalSubstitution.class);
        AsyncApiBuilder builder = new AsyncApiBuilder();
        AsyncAPI asyncAPI = builder.build(aIndex.getIndex(), aConfig);
        aRecorder.store(asyncAPI, aConfig);
    }

    static class IsEnabled implements BooleanSupplier {

        AsyncApiRuntimeConfig config;

        public boolean getAsBoolean() {
            if (!config.enabled) {
                Logger.getLogger(AsyncAPIResourceGenerator.class.getName())
                        .warning("Async API disabled (see config asyncapi.annotation.scanner.enabled)");
            }
            return config.enabled;
        }
    }

    @BuildStep
    void handleHttpRequests(
            BuildProducer<RouteBuildItem> aRoutes,
            NonApplicationRootPathBuildItem aNonApplicationRootPathBuildItem,
            List<FilterBuildItem> aFilterBuildItems) {
        String path = ConfigProvider.getConfig()
                .getValue("quarkus.http.root-path", String.class).concat("/asyncapi");
        AsyncApiHandler handler = new AsyncApiHandler();
        Consumer<Route> corsFilter = null;
        // Add CORS filter if the path is not attached to main root
        // as 'http-vertx' only adds CORS filter to http route path
        if (!aNonApplicationRootPathBuildItem.isAttachedToMainRouter()) {
            for (FilterBuildItem filterBuildItem : aFilterBuildItems) {
                if (filterBuildItem.getPriority() == FilterBuildItem.CORS) {
                    corsFilter = corsFilter(filterBuildItem.toFilter());
                    break;
                }
            }
        }
        aRoutes.produce(aNonApplicationRootPathBuildItem.routeBuilder()
                .routeFunction(path, corsFilter)
                .routeConfigKey("quarkus.asyncapi.path")
                .handler(handler)
                .displayOnNotFoundPage("Async API Schema document")
                .blockingRoute()
                .build());
        List<String> targets = List.of("json", "yaml", "yml", "html", "puml", "svg");
        for (String target : targets) {
            aRoutes.produce(aNonApplicationRootPathBuildItem.routeBuilder()
                    .routeFunction(path + "." + target, corsFilter)
                    .handler(handler)
                    .build());
        }
    }

    Consumer<Route> corsFilter(Filter filter) {
        //cors always enabled
        if (filter.getHandler() != null) {
            return (Route route) -> route.order(-1 * filter.getPriority()).handler(filter.getHandler());
        }
        return null;
    }

}
