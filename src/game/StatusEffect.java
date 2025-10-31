package game;

public class StatusEffect {
    public enum Type {
        STUN, TAUNT, DOT, BUFF, DEBUFF, HEAL_OVER_TIME, POISON
    }

    private Type type;
    private int duration;
    private int value;
    private Attribute attribute;

    public StatusEffect(Type type, int duration, int value, Attribute attribute) {
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.attribute = attribute;
    }

    public void reduceDuration() {
        duration--;
    }

    public boolean isActive() { return duration > 0; }

    public Type getType() { return type; }
    public int getValue() { return value; }
    public int getDuration() { return duration; }
    public Attribute getAttribute() { return attribute; }
}