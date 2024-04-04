package io.quarkiverse.asyncapi.annotation.scanner;

class PathBuilder {
    private final String rootPath;

    PathBuilder(String rootPath) {
        this.rootPath = rootPath;
    }

    String appendPath(String path) {
        return normalizedRootPath() + path;
    }

    private String normalizedRootPath() {
        // strip trailing slashes
        return rootPath.replaceAll("/+$", "");
    }
}
