package io.quarkiverse.asyncapi.annotation.scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.config.ConfigProvider;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @since 10.02.2023
 */
public class AsyncApiHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER = Logger.getLogger(AsyncApiHandler.class.getName());
    static final String HTML_PATTERN = "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n"
            + "  <head>\n"
            + "    <script src=\"https://unpkg.com/@webcomponents/webcomponentsjs@%s/webcomponents-bundle.js\"></script>\n"
            + "    <script\n"
            + "      src=\"https://unpkg.com/@asyncapi/web-component@%s/lib/asyncapi-web-component.js\"\n"
            + "      defer\n"
            + "    ></script>\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <asyncapi-component\n"
            + "      cssImportPath=\"https://unpkg.com/@asyncapi/react-component@%s/styles/default.css\"\n"
            + "      schemaUrl=\"%s/asyncapi.yaml\"\n"
            + "    >\n"
            + "    </asyncapi-component>\n"
            + "  </body>\n"
            + "</html>\n";

    @Override
    public void handle(RoutingContext aRoutingContext) {
        HttpServerResponse resp = aRoutingContext.response();
        if (ConfigProvider.getConfig().getValue("quarkus.asyncapi.annotation.scanner.enabled", Boolean.class)) {
            Format format = getFormat(aRoutingContext);
            resp.headers().set("Content-Type", format.getMimeType() + ";charset=UTF-8");
            String output;
            switch (format) {
                case HTML:
                    output = getHtml(aRoutingContext);
                    break;
                default:
                    output = readFile(format);
            }
            resp.end(Buffer.buffer(output)); // see http://localhost:8080/asyncapi
        } else {
            resp.headers().set("Content-Type", "text/plain;charset=UTF-8");
            resp.end("Async API disabled (see config asyncapi.annotation.scanner.enabled)");
        }
    }

    String readFile(Format aFormat) {
        try {
            Path path = Paths.get(System.getProperty("java.io.tmpdir"), aFormat.getFileName());
            return Files.readString(path);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "AsyncApiHandler.read() Unable to read file as " + aFormat, e);
            return "Unable to read file";
        }
    }

    String getHtml(RoutingContext aRoutingContext) {
        String version = ConfigProvider.getConfig()
                .getValue("quarkus.asyncapi.annotation.scanner.webcomponentversion", String.class);
        String rootPath = ConfigProvider.getConfig()
                .getValue("quarkus.http.root-path", String.class);
        //TODO logo
        return String.format(HTML_PATTERN, version, version, version, rootPath, rootPath);
    }

    Format getFormat(RoutingContext aRoutingContext) {
        String path = aRoutingContext.normalizedPath();
        // Content negotiation with file extension
        Format format = Format.YAML;
        if (path.endsWith(".json")) {
            format = Format.JSON;
        } else if (path.endsWith(".yaml") || path.endsWith(".yml")) {
            format = Format.YAML;
        } else if (path.endsWith(".html")) {
            format = Format.HTML;
        } else if (path.endsWith(".puml")) {
            format = Format.PUML;
        } else if (path.endsWith(".svg")) {
            format = Format.SVG;
        } else {
            // Content negotiation with Accept header
            String accept = aRoutingContext.request().headers().get("Accept");
            List<String> formatParams = aRoutingContext.queryParam("format");
            String formatParam = formatParams.isEmpty() ? null : formatParams.get(0);
            // Check Accept, then query parameter "format" for JSON; else use YAML.
            if ((accept != null && accept.contains(Format.JSON.getMimeType())) || ("JSON".equalsIgnoreCase(formatParam))) {
                format = Format.JSON;
            }
        }
        return format;
    }

    enum Format {
        JSON("application/json", AsyncApiRecorder.ASYNC_API_JSON),
        YAML("application/yaml", AsyncApiRecorder.ASYNC_API_YAML),
        HTML("text/html", null),
        PUML("text/plain", AsyncApiRecorder.ASYNC_API_PUML),
        SVG("image/svg+xml", AsyncApiRecorder.ASYNC_API_SVG);

        private final String mimeType;
        private final String fileName;

        Format(String aMimeType, String aFileName) {
            mimeType = aMimeType;
            fileName = aFileName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
