package neutra1.linter.models.enums;

public enum OutputFormat {
    ERRORFORMAT("errorformat"),
    GITHUB_ACTIONS("github-actions");

    private final String label;

    OutputFormat(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static OutputFormat fromLabel(String label) {
        for (OutputFormat format : values()) {
            if (format.label.equalsIgnoreCase(label)) {
                return format;
            }
        }
        throw new IllegalArgumentException("expected one of: " + ERRORFORMAT + ", " + GITHUB_ACTIONS);
    }
}
