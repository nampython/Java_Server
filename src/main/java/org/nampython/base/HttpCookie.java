package org.nampython.base;

public class HttpCookie {
    private String name;
    private String value;
    private String path;

    public HttpCookie(String name, String value) {
        this.setName(name);
        this.setValue(value);
    }

    public String toRFCString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append("=").append(this.getValue());
        if (this.getPath() != null) {
            sb.append("; path=").append(this.getPath());
        }

        return sb.toString();
    }
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
