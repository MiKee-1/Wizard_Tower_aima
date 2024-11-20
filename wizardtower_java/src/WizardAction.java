public class WizardAction {
    private String name;

    public WizardAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WizardAction)) return false;
        return name.equals(((WizardAction) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}