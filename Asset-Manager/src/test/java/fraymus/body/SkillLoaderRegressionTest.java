package fraymus.body;

import java.nio.file.Files;
import java.nio.file.Path;

/** Standalone checks for the complete skill instructions included in prompts. */
public final class SkillLoaderRegressionTest {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("fraynix-skill-regression-");
        String usage = "First instruction.\n\nSecond instruction.\nThird instruction.";
        for (String newline : new String[]{"\n", "\r\n"}) {
            Path path = root.resolve(newline.length() + ".md");
            Files.writeString(path, ("# Fixture skill\n> Fixture description\n\n## Usage\n\n"
                + usage + "\n\n## Examples\nExample must stay out of usage.\n").replace("\n", newline));
            var loader = new SkillLoader();
            loader.ingest(path.toString());
            var skill = loader.getSkill("Fixture skill");
            if (skill == null || !skill.description.equals("Fixture description")
                    || !skill.usage.replace("\r\n", "\n").equals(usage)) {
                throw new AssertionError("Multiline skill instructions were truncated");
            }
            if (loader.getDetailedSkillContext().contains("Example must stay out")) {
                throw new AssertionError("Usage leaked into the next section");
            }
        }
        Path end = root.resolve("end.md");
        Files.writeString(end, "# End skill\n## Usage\n" + usage);
        var loader = new SkillLoader();
        loader.ingest(end.toString());
        if (!loader.getSkill("End skill").usage.equals(usage)) throw new AssertionError("EOF skill instructions lost");
        System.out.println("SkillLoaderRegressionTest passed: LF, CRLF, blank lines, section boundary and EOF");
    }
}
