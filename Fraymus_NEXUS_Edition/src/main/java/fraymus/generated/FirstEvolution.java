package fraymus.generated;

public class FirstEvolution {
    private String message;

    public FirstEvolution() {
        this.message = "I am the first self-generated organism!";
        System.out.println("🧬 " + message);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SelfGenerated[" + message + "]";
    }
}
