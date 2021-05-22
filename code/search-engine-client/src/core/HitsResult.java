package core;

public class HitsResult {
	
    private String url;
    private double authority;
    private double hub;

    public HitsResult(String url) {
        this.url = url;
        this.authority = 1;
        this.hub = 1;
    }

    public String getUrl() {
            return this.url;
    }

    public void setUrl(String url) {
            this.url = url;
    }

    public double getAuthority() {
            return this.authority;
    }

    public void setAuthority(double authority) {
            this.authority = authority;
    }

    public double getHub() {
            return this.hub;
    }

    public void setHub(double hub) {
            this.hub = hub;
    }

    public String getStringToTable(){
        return this.authority + "\t" + this.hub + "\t" + this.url; 
    }

    @Override
    public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
            return result;
    }

    @Override
    public boolean equals(Object obj) {
            if (this == obj)
                    return true;
            if (obj == null)
                    return false;
            if (getClass() != obj.getClass())
                    return false;
            HitsResult other = (HitsResult) obj;
            if (this.url == null) {
                    if (other.url != null)
                            return false;
            } else if (!this.url.equals(other.url))
                    return false;
            return true;
    }

    @Override
    public String toString() {
            return "{\"url\":\"" + this.url + "\",\"authority\":" + this.authority + ",\"hub\":" + this.hub + "}";
    }
        
}

