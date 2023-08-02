package io.quarkiverse.asyncapi.annotation.scanner;

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Pojo holds a String for all supported languages
 *
 * @param <T>
 * @since 20.06.2023
 */
@Schema(description = "Object holding translated text for all supported languages")
public class I18n<T extends I18n> implements Serializable {

    private static final long serialVersionUID = -1003383281201287866L;

    @Schema(description = "german")
    private String de;
    @Schema(description = "english")
    private String en;
    @Schema(description = "french")
    private String fr;
    @Schema(description = "chinese")
    private String zh;
    @Schema(description = "italian")
    private String it;
    @Schema(description = "hindi")
    private String hi;
    @Schema(description = "korean")
    private String ko;
    @Schema(description = "portuguese")
    private String pt;
    @Schema(description = "spanish")
    private String es;
    @Schema(description = "polish")
    private String pl;
    @Schema(description = "japanese")
    private String ja;

    public I18n() {
        //default constructor
    }

    //<editor-fold defaultstate="collapsed" desc="getter & setter">
    public String getDe() {
        return de;
    }

    public T setDe(String aDe) {
        this.de = aDe;
        return (T) this;
    }

    public String getEn() {
        return en;
    }

    public T setEn(String aEn) {
        this.en = aEn;
        return (T) this;
    }

    public String getFr() {
        return fr;
    }

    public T setFr(String aFr) {
        this.fr = aFr;
        return (T) this;
    }

    public String getZh() {
        return zh;
    }

    public T setZh(String aZh) {
        this.zh = aZh;
        return (T) this;
    }

    public String getIt() {
        return it;
    }

    public T setIt(String aIt) {
        this.it = aIt;
        return (T) this;
    }

    public String getHi() {
        return hi;
    }

    public T setHi(String aHi) {
        this.hi = aHi;
        return (T) this;
    }

    public String getKo() {
        return ko;
    }

    public T setKo(String aKo) {
        this.ko = aKo;
        return (T) this;
    }

    public String getPt() {
        return pt;
    }

    public T setPt(String aPt) {
        this.pt = aPt;
        return (T) this;
    }

    public String getEs() {
        return es;
    }

    public T setEs(String aEs) {
        this.es = aEs;
        return (T) this;
    }

    public String getPl() {
        return pl;
    }

    public T setPl(String aPl) {
        this.pl = aPl;
        return (T) this;
    }

    public String getJa() {
        return ja;
    }

    public T setJa(String aJa) {
        this.ja = aJa;
        return (T) this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toString">
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
                + "de=" + de + ", "
                + "en=" + en + ", "
                + "fr=" + fr + ", "
                + "zh=" + zh + ", "
                + "it=" + it + ", "
                + "hi=" + hi + ", "
                + "ko=" + ko + ", "
                + "pt=" + pt + ", "
                + "es=" + es + ", "
                + "pl=" + pl + ", "
                + "jp=" + ja
                + '}';
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hashCode & equals">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.de);
        hash = 97 * hash + Objects.hashCode(this.en);
        hash = 97 * hash + Objects.hashCode(this.fr);
        hash = 97 * hash + Objects.hashCode(this.zh);
        hash = 97 * hash + Objects.hashCode(this.it);
        hash = 97 * hash + Objects.hashCode(this.hi);
        hash = 97 * hash + Objects.hashCode(this.ko);
        hash = 97 * hash + Objects.hashCode(this.pt);
        hash = 97 * hash + Objects.hashCode(this.es);
        hash = 97 * hash + Objects.hashCode(this.pl);
        hash = 97 * hash + Objects.hashCode(this.ja);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final I18n<?> other = (I18n<?>) obj;
        if (!Objects.equals(this.de, other.de)) {
            return false;
        }
        if (!Objects.equals(this.en, other.en)) {
            return false;
        }
        if (!Objects.equals(this.fr, other.fr)) {
            return false;
        }
        if (!Objects.equals(this.zh, other.zh)) {
            return false;
        }
        if (!Objects.equals(this.it, other.it)) {
            return false;
        }
        if (!Objects.equals(this.hi, other.hi)) {
            return false;
        }
        if (!Objects.equals(this.ko, other.ko)) {
            return false;
        }
        if (!Objects.equals(this.pt, other.pt)) {
            return false;
        }
        if (!Objects.equals(this.es, other.es)) {
            return false;
        }
        if (!Objects.equals(this.pl, other.pl)) {
            return false;
        }
        return Objects.equals(this.ja, other.ja);
    }
    //</editor-fold>
}
