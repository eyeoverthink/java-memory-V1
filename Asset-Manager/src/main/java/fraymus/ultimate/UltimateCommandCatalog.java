package fraymus.ultimate;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Shared shape for operator surfaces that expose grouped command taxonomies.
 */
public interface UltimateCommandCatalog {

    String surfaceId();

    String surfaceTitle();

    String surfaceDescription();

    String primarySubsystemId();

    List<CommandSection> sections();

    List<String> linkedSubsystemIds(CommandSection section);

    default int sectionCount() {
        return sections().size();
    }

    default int commandCount() {
        return sections().stream().mapToInt(CommandSection::commandCount).sum();
    }

    default Optional<CommandSection> findSection(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        String needle = normalize(query);
        return sections().stream()
            .filter(section -> normalize(section.id()).equals(needle)
                || normalize(section.title()).equals(needle))
            .findFirst();
    }

    default String renderSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(surfaceTitle()).append("\n");
        sb.append(surfaceDescription()).append("\n");
        sb.append("Sections: ").append(sectionCount())
            .append(" | Catalogued commands: ").append(commandCount()).append("\n");
        for (CommandSection section : sections()) {
            sb.append("  - ")
                .append(section.id())
                .append(" :: ")
                .append(section.title())
                .append(" (")
                .append(section.commandCount())
                .append(")\n");
        }
        return sb.toString().trim();
    }

    default String renderSection(String query) {
        Optional<CommandSection> match = findSection(query);
        if (match.isEmpty()) {
            return "Usage: commands <surface> <section>\nTry: commands";
        }

        CommandSection section = match.get();
        StringBuilder sb = new StringBuilder();
        sb.append("SURFACE: ").append(surfaceTitle()).append("\n");
        sb.append("SECTION: ").append(section.title()).append("\n");
        sb.append("Id: ").append(section.id()).append("\n");
        sb.append("Theme: ").append(section.theme()).append("\n");
        sb.append("Commands:\n");
        for (CommandEntry entry : section.commands()) {
            sb.append("  - ")
                .append(entry.syntax())
                .append(" :: ")
                .append(entry.description())
                .append("\n");
        }
        return sb.toString().trim();
    }

    default List<String> linkedSubsystemIds(String query) {
        return findSection(query)
            .map(this::linkedSubsystemIds)
            .orElse(List.of());
    }

    default Optional<String> defaultSubsystemId(CommandSection section) {
        List<String> links = linkedSubsystemIds(section);
        return links.isEmpty() ? Optional.empty() : Optional.of(links.get(0));
    }

    default Optional<String> defaultSubsystemId(String query) {
        return findSection(query).flatMap(this::defaultSubsystemId);
    }

    static String normalize(String value) {
        return value == null
            ? ""
            : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
    }

    record CommandSection(String id, String title, String theme, List<CommandEntry> commands) {
        public CommandSection {
            commands = List.copyOf(commands);
        }

        public int commandCount() {
            return commands.size();
        }
    }

    record CommandEntry(String syntax, String description) {
    }
}
