package fraymus.generated;

public class SecondEvolution {
    private String message;

    public SecondEvolution() {
        this.message = "I am the EVOLVED organism - faster, stronger, better!";
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
