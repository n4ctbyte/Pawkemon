package game;

public class StatusEffect {
    public enum Type {
        STUN, TAUNT, DOT, BUFF, DEBUFF, HEAL_OVER_TIME, SHIELD, POISON
    }

    private Type type;
    private int duration;
    private int value;
    private Attribute attribute;
    private boolean applied = false;
    private String source = "";

    public StatusEffect(Type type, int duration, int value, Attribute attribute) {
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.attribute = attribute;
    }

    public void reduceDuration() {
        duration--;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public StatusEffect clone() {
        StatusEffect clone = new StatusEffect(this.type, this.duration, this.value, this.attribute);
        clone.applied = this.applied;
        return clone;
    }

    public boolean isActive() { return duration > 0; }

    public Type getType() { return type; }
    public int getValue() { return value; }
    public int getDuration() { return duration; }
    public Attribute getAttribute() { return attribute; }
    public boolean isApplied() { return applied; }
    public void setApplied(boolean applied) { this.applied = applied; }
}