package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * Interface for all entities that have Translation-fields
 *
 * @author christiant
 * @see Translation
 */
public interface HasTranslations {

    @Schema(readOnly = true, description = "All defined text in all languages for all translatable properties of this entity (read-only)")
    @JsonGetter
    default Map<String, I18n> getI18n() {
        return Map.of(); //DUMMY
    }
}
