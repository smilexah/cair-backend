package ai.lab.cair.entity.enums;

//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonValue;
//
//public enum TokenType {
//    ACCESS_TOKEN("access_token"),
//    REFRESH_TOKEN("refresh_token");
//
//    private final String value;
//
//    TokenType(String value) { this.value = value; }
//
//    @JsonValue
//    public String getValue() { return value; }
//
//    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
//    public static TokenType fromValue(String v) {
//        if (v == null) return null;
//        for (var t : values()) {
//            if (t.value.equalsIgnoreCase(v) || t.name().equalsIgnoreCase(v)) return t;
//        }
//        throw new IllegalArgumentException("Unknown TokenType: " + v);
//    }
//
//    @Override
//    public String toString() {
//        return name();
//    }
//}

// todo: отрефакторить все использования toString(), исползовагние tOString как значение очень плохо лучше геттеры
public enum TokenType {
    ACCESS_TOKEN, REFRESH_TOKEN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}