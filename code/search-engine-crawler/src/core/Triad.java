package core;


/**
 * Clase que crea objetos de tipo Triad, demandados en el enunciado del proyecto
 * y necesarios para implementar el algoritmo hits
 * @author ujarky
 */
public class Triad {
	
    //URL de la pagina origen
    private String source;
    //URL de la pagina destino
    private String cible;
    //Palabra que representa el link de la pagina destino en la pagina origen
    private String mot;

    /**
     * Crea una Trad iniciando sus 3 valores
     * @param source
     * @param cible
     * @param mot
     */
    public Triad( String source, String cible, String mot) {
        this.source = source;
        this.cible = cible;
        this.mot = mot;
        purify();

    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCible() {
        return this.cible;
    }

    public void setCible(String cible) {
        this.cible = cible;
    }

    public String getMot() {
        return this.mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    private void purify() {
        if(this.cible != null && this.cible.endsWith("/"))
            this.cible = this.cible.substring(0, this.cible.length()-1);
        if(this.source != null && this.source.endsWith("/"))
            this.source = this.source.substring(0, this.source.length()-1);
    }


    @Override
    public String toString() {
        return "Triad [source=" + this.source + ", cible=" + this.cible + ", mot=" + this.mot + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.cible == null) ? 0 : this.cible.hashCode());
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
        return result;
    }

    /**
     * Dos Triads son iguales si esta contiene
     * el mismo origen y el mismo destino
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
                return true;
        if (obj == null)
                return false;
        if (getClass() != obj.getClass())
                return false;
        Triad other = (Triad) obj;
        if (this.cible == null) {
                if (other.cible != null)
                        return false;
        } else if (!this.cible.equals(other.cible))
                return false;
        if (this.source == null) {
                if (other.source != null)
                        return false;
        } else if (!this.source.equals(other.source))
                return false;

        return true;
    }

}
