package io.quarkiverse.asyncapi.annotation.scanner;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Set/Get String in active Locale
 *
 * @since 22.05.2023
 */
@Schema(oneOf = { String.class,
        I18n.class }, description = "Per default POST/PUT/GET will set/return translation as single String for current Locale (see HTTP-Header 'Accept-Language').\n"
                + "As a fallback for POST/PUT a JSON that contains all languages (see I18n-object) may be provided in order to create/update all translations at once.")
@io.quarkiverse.asyncapi.annotation.Schema(oneOf = { String.class,
        I18n.class }, description = "Per default Incoming/Outgoing message will set/return translation as single String for current Locale (see HTTP-Header 'Accept-Language').\n"
                + "As a fallback for Incoming messages a JSON that contains all languages (see I18n-object) may be provided in order to create/update all translations at once.")
public class Translation extends I18n<Translation> {

    //<editor-fold defaultstate="collapsed" desc="overridden getter just needed to hide properties in OpenApi">
    @Override
    @Schema(hidden = true)
    public String getFr() {
        return super.getFr();
    }

    @Override
    @Schema(hidden = true)
    public String getEn() {
        return super.getEn();
    }

    @Override
    @Schema(hidden = true)
    public String getDe() {
        return super.getDe();
    }

    @Override
    @Schema(hidden = true)
    public String getJa() {
        return super.getJa();
    }

    @Override
    @Schema(hidden = true)
    public String getPl() {
        return super.getPl();
    }

    @Override
    @Schema(hidden = true)
    public String getEs() {
        return super.getEs();
    }

    @Override
    @Schema(hidden = true)
    public String getPt() {
        return super.getPt();
    }

    @Override
    @Schema(hidden = true)
    public String getKo() {
        return super.getKo();
    }

    @Override
    @Schema(hidden = true)
    public String getHi() {
        return super.getHi();
    }

    @Override
    @Schema(hidden = true)
    public String getIt() {
        return super.getIt();
    }

    @Override
    @Schema(hidden = true)
    public String getZh() {
        return super.getZh();
    }
    //</editor-fold>
}
